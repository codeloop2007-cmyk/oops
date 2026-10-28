package com.mediconnect.backend.feature.doctorembedding.worker;

import com.mediconnect.backend.feature.doctorembedding.entity.DoctorEmbedding;
import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.doctorspecialization.repository.DoctorSpecializationRepository;
import com.mediconnect.backend.feature.userlanguage.repository.UserLanguageRepository;
import com.pgvector.PGvector;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Background worker that keeps doctor embeddings in sync.
 *
 * <p>On every scheduled run it:
 * <ol>
 *   <li>Finds all {@link DoctorEmbedding} rows where {@code is_up_to_date = false}.</li>
 *   <li>Finds all doctor profiles that have <em>no</em> embedding row yet.</li>
 *   <li>For each stale / missing embedding, builds the doctor's source text
 *       (name + language + bio + qualifications + specializations), calls the
 *       Python AI-service {@code /embed} endpoint, and persists the new vector.</li>
 * </ol>
 *
 * <p>The {@code is_up_to_date} flag is set to {@code false} by the relevant
 * services whenever a doctor's profile data, specializations, or user language
 * information changes.
 */
@Component
public class EmbeddingWorker {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingWorker.class);
    private static final String EMBEDDED_MODEL = "all-MiniLM-L6-v2";

    private final DoctorEmbeddingRepository doctorEmbeddingRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final UserLanguageRepository userLanguageRepository;
    private final RestTemplate restTemplate;

    @Value("${embedding.service.base-url:http://localhost:8000}")
    private String embeddingServiceBaseUrl;

    public EmbeddingWorker(
            DoctorEmbeddingRepository doctorEmbeddingRepository,
            DoctorProfileRepository doctorProfileRepository,
            DoctorSpecializationRepository doctorSpecializationRepository,
            UserLanguageRepository userLanguageRepository,
            RestTemplate restTemplate) {
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorSpecializationRepository = doctorSpecializationRepository;
        this.userLanguageRepository = userLanguageRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Scheduled sync run.  Default: every 60 seconds; override via
     * {@code embedding.worker.interval-ms} in {@code application.properties}.
     */
    @Scheduled(fixedDelayString = "${embedding.worker.interval-ms:60000}")
    @Transactional
    public void syncEmbeddings() {
        log.info("EmbeddingWorker: starting sync run");

        // ---- 1. Collect stale embeddings ----
        List<DoctorEmbedding> stale = doctorEmbeddingRepository.findAllByIsUpToDateFalse();
        log.info("EmbeddingWorker: found {} stale embedding(s)", stale.size());

        for (DoctorEmbedding embedding : stale) {
            try {
                refreshEmbedding(embedding.getDoctorUserId(), embedding);
            } catch (Exception ex) {
                log.error("EmbeddingWorker: failed to refresh embedding for doctorId={}: {}",
                        embedding.getDoctorUserId(), ex.getMessage(), ex);
            }
        }

        // ---- 2. Collect doctors with no embedding at all ----
        List<Long> missingIds = doctorEmbeddingRepository.findDoctorIdsWithoutEmbedding();
        log.info("EmbeddingWorker: found {} doctor(s) with no embedding", missingIds.size());

        for (Long doctorUserId : missingIds) {
            try {
                refreshEmbedding(doctorUserId, null);
            } catch (Exception ex) {
                log.error("EmbeddingWorker: failed to create embedding for doctorId={}: {}",
                        doctorUserId, ex.getMessage(), ex);
            }
        }

        log.info("EmbeddingWorker: sync run complete");
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Re-computes and persists the embedding for a single doctor.
     *
     * @param doctorUserId the doctor's user-id primary key
     * @param existing     the existing {@link DoctorEmbedding} row, or {@code null} if none yet
     */
    private void refreshEmbedding(Long doctorUserId, DoctorEmbedding existing) {
        DoctorProfile profile = doctorProfileRepository.findById(doctorUserId).orElse(null);
        if (profile == null) {
            log.warn("EmbeddingWorker: doctor profile not found for doctorId={}, skipping", doctorUserId);
            return;
        }

        String sourceText = buildSourceText(profile);
        String newHash = sha256(sourceText);

        // Skip if nothing changed (only relevant when row already exists)
        if (existing != null && newHash.equals(existing.getSourceHash()) && existing.getIsUpToDate()) {
            log.debug("EmbeddingWorker: doctorId={} hash unchanged, skipping API call", doctorUserId);
            return;
        }

        // Call Python /embed endpoint
        List<Float> vector = callEmbedApi(sourceText);
        if (vector == null || vector.isEmpty()) {
            log.error("EmbeddingWorker: received empty vector for doctorId={}", doctorUserId);
            return;
        }

        // Build or update the entity
        DoctorEmbedding entity = (existing != null) ? existing
                : doctorEmbeddingRepository.findById(doctorUserId).orElse(null);
        if (entity == null) {
            entity = new DoctorEmbedding();
            entity.setDoctorProfile(profile);
            entity.setDoctorUserId(doctorUserId);
            entity.setNew(true);
        } else {
            entity.setNew(false);
        }
        float[] floatArray = toFloatArray(vector);
        entity.setEmbedding(new PGvector(floatArray));
        entity.setEmbeddedModel(EMBEDDED_MODEL);
        entity.setSourceHash(newHash);
        entity.setIsUpToDate(true);
        entity.setUpdatedAt(Instant.now());

        doctorEmbeddingRepository.save(entity);
        log.info("EmbeddingWorker: saved embedding for doctorId={} (dim={})", doctorUserId, vector.size());
    }

    /**
     * Builds the plain-text representation of a doctor that will be embedded.
     *
     * <p>Format:
     * <pre>
     * Name: &lt;doctor name&gt;
     * Language: &lt;preferred language&gt; [, &lt;extra languages&gt;]
     * Qualification: &lt;qualification&gt;
     * Bio: &lt;bio&gt;
     * Specializations: &lt;comma-separated specializations&gt;
     * </pre>
     */
    private String buildSourceText(DoctorProfile profile) {
        StringBuilder sb = new StringBuilder();

        // Doctor name (from associated User)
        String name = (profile.getUser() != null && profile.getUser().getName() != null)
                ? profile.getUser().getName() : "";
        sb.append("Name: ").append(name).append("\n");

        // Preferred language (from User)
        String preferredLang = (profile.getUser() != null && profile.getUser().getPreferredLanguage() != null)
                ? profile.getUser().getPreferredLanguage() : "";

        // Additional languages (UserLanguage rows)
        List<String> additionalLangs = userLanguageRepository
                .findByUser_UserId(profile.getUserId())
                .stream()
                .map(ul -> ul.getId() != null ? ul.getId().getLanguageCode() : null)
                .filter(l -> l != null && !l.isBlank())
                .distinct()
                .collect(Collectors.toList());

        List<String> allLangs = new ArrayList<>();
        if (!preferredLang.isBlank()) allLangs.add(preferredLang);
        allLangs.addAll(additionalLangs);

        sb.append("Language: ").append(String.join(", ", allLangs)).append("\n");

        // Qualification
        sb.append("Qualification: ").append(nvl(profile.getQualification())).append("\n");

        // Bio
        sb.append("Bio: ").append(nvl(profile.getBio())).append("\n");

        // Specializations
        List<String> specializations = doctorSpecializationRepository
                .findByDoctorProfile_UserId(profile.getUserId())
                .stream()
                .map(ds -> ds.getSpecialization() != null ? ds.getSpecialization().getName() : null)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());

        sb.append("Specializations: ").append(String.join(", ", specializations));

        return sb.toString().trim();
    }

    /** Calls {@code POST /embed} on the Python AI service and returns the float vector. */
    @SuppressWarnings("unchecked")
    private List<Float> callEmbedApi(String text) {
        String url = embeddingServiceBaseUrl + "/embed";
        EmbedRequest requestBody = new EmbedRequest(text);
        try {
            ResponseEntity<EmbedApiResponse> response =
                    restTemplate.postForEntity(url, requestBody, EmbedApiResponse.class);
            if (response.getBody() == null) {
                log.error("EmbeddingWorker: /embed returned null body");
                return List.of();
            }
            return response.getBody().getEmbedding();
        } catch (Exception ex) {
            log.error("EmbeddingWorker: error calling /embed: {}", ex.getMessage(), ex);
            return List.of();
        }
    }

    /** Converts a {@code List<Float>} to a primitive {@code float[]}. */
    private static float[] toFloatArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /** SHA-256 of a string, hex-encoded. */
    private static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return "sha256-" + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }

    // -----------------------------------------------------------------------
    // Inner request / response types for the /embed endpoint
    // -----------------------------------------------------------------------

    /** Request body sent to the Python {@code /embed} endpoint. */
    static class EmbedRequest {
        private final String text;

        EmbedRequest(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    /** Subset of the response from {@code /embed} that we care about. */
    static class EmbedApiResponse {
        private String text;
        private int dimension;
        private List<Float> embedding;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public int getDimension() { return dimension; }
        public void setDimension(int dimension) { this.dimension = dimension; }
        public List<Float> getEmbedding() { return embedding; }
        public void setEmbedding(List<Float> embedding) { this.embedding = embedding; }
    }
}
