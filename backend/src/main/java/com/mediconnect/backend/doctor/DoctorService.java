package com.mediconnect.backend.doctor;

import com.mediconnect.backend.embedding.EmbeddingClient;
import com.mediconnect.backend.vector.VectorType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository repo;
    private final EmbeddingClient embedder;

    public DoctorService(DoctorRepository repo, EmbeddingClient embedder) {
        this.repo = repo;
        this.embedder = embedder;
    }

    public List<DoctorResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DoctorResponse getById(Long id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found: " + id)));
    }

    public DoctorResponse create(DoctorRequest req) {
        Doctor d = new Doctor();
        applyRequest(d, req);
        d.setEmbedding(embed(doctorText(d)));
        return toResponse(repo.save(d));
    }

    public DoctorResponse update(Long id, DoctorRequest req) {
        Doctor d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found: " + id));
        applyRequest(d, req);
        d.setEmbedding(embed(doctorText(d))); // refresh embedding on update
        return toResponse(repo.save(d));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found: " + id);
        repo.deleteById(id);
    }

    public List<DoctorResponse> semanticSearch(String query, int limit) {
        if (query == null || query.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query 'q' is required");
        if (limit <= 0 || limit > 50) limit = 10;

        float[] vec = embedder.embed(query);
        String vecStr = VectorType.toVectorString(vec);
        return repo.semanticSearch(vecStr, limit).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----- helpers -----

    private void applyRequest(Doctor d, DoctorRequest r) {
        d.setName(r.getName());
        d.setSpecialization(r.getSpecialization());
        d.setQualification(r.getQualification());
        d.setLanguage(r.getLanguage());
        d.setLocation(r.getLocation());
        d.setConsultationFee(r.getConsultationFee());
    }

    /** Concatenate searchable fields into a single text blob. */
    private String doctorText(Doctor d) {
        StringBuilder sb = new StringBuilder();
        append(sb, d.getName());
        append(sb, d.getSpecialization());
        append(sb, d.getQualification());
        append(sb, d.getLanguage());
        append(sb, d.getLocation());
        return sb.toString().trim();
    }

    private void append(StringBuilder sb, String s) {
        if (s != null && !s.isBlank()) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(s);
        }
    }

    private float[] embed(String text) {
        return embedder.embed(text);
    }

    private DoctorResponse toResponse(Doctor d) {
        return new DoctorResponse(
                d.getDoctorId(), d.getName(), d.getSpecialization(),
                d.getQualification(), d.getLanguage(), d.getLocation(),
                d.getConsultationFee());
    }
}