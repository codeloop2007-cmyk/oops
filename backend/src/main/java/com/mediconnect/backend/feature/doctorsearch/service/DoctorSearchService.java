package com.mediconnect.backend.feature.doctorsearch.service;

import com.mediconnect.backend.feature.doctorsearch.dto.DoctorSearchResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DoctorSearchService {

    private final EntityManager entityManager;
    private final RestTemplate restTemplate;

    @Value("${embedding.service.base-url:http://localhost:8000}")
    private String embeddingServiceBaseUrl;

    public DoctorSearchService(EntityManager entityManager, RestTemplate restTemplate) {
        this.entityManager = entityManager;
        this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public List<DoctorSearchResponse> searchDoctors(
            String queryText,
            int limit,
            String sort,
            String specialization,
            BigDecimal minFee,
            BigDecimal maxFee,
            String city,
            String language) {
        if (queryText == null || queryText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }
        if (minFee != null && minFee.signum() < 0 || maxFee != null && maxFee.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fees cannot be negative");
        }
        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum fee cannot exceed maximum fee");
        }

        String orderBy = orderBy(sort);
        String embedding = embedQuery(queryText.trim());
        StringBuilder sql = new StringBuilder("""
                SELECT dp.user_id,
                       u.name,
                       COALESCE((SELECT string_agg(s.name, ', ' ORDER BY s.name)
                                 FROM doctor_specialization ds
                                 JOIN specialization s ON s.specialization_id = ds.specialization_id
                                 WHERE ds.doctor_user_id = dp.user_id), ''),
                       dp.qualification,
                       concat_ws(', ', nullif(u.preferred_language, ''),
                           (SELECT string_agg(DISTINCT ul.language_code, ', ')
                            FROM user_language ul WHERE ul.user_id = dp.user_id)),
                       nullif(concat_ws(', ', nullif(u.city, ''), nullif(u.state, ''), nullif(u.country, '')), ''),
                       dp.consultation_fee
                FROM doctor_profile dp
                JOIN app_user u ON u.user_id = dp.user_id
                JOIN doctor_embedding de ON de.doctor_user_id = dp.user_id
                WHERE de.is_up_to_date = true
                  AND de.embedding IS NOT NULL
                """);

        if (hasText(specialization)) {
            sql.append(" AND EXISTS (SELECT 1 FROM doctor_specialization ds JOIN specialization s ")
                    .append("ON s.specialization_id = ds.specialization_id ")
                    .append("WHERE ds.doctor_user_id = dp.user_id AND s.name ILIKE :specialization)");
        }
        if (minFee != null) {
            sql.append(" AND dp.consultation_fee >= :minFee");
        }
        if (maxFee != null) {
            sql.append(" AND dp.consultation_fee <= :maxFee");
        }
        if (hasText(city)) {
            sql.append(" AND u.city ILIKE :city");
        }
        if (hasText(language)) {
            sql.append(" AND (u.preferred_language ILIKE :language OR EXISTS ")
                    .append("(SELECT 1 FROM user_language ul WHERE ul.user_id = dp.user_id ")
                    .append("AND ul.language_code ILIKE :language))");
        }
        sql.append(" ORDER BY ").append(orderBy).append(" LIMIT :limit");

        Query nativeQuery = entityManager.createNativeQuery(sql.toString());
        if (sort == null || "RELEVANCE".equalsIgnoreCase(sort)) {
            nativeQuery.setParameter("embedding", embedding);
        }
        nativeQuery.setParameter("limit", limit);
        setPattern(nativeQuery, "specialization", specialization);
        setPattern(nativeQuery, "city", city);
        setPattern(nativeQuery, "language", language);
        if (minFee != null) {
            nativeQuery.setParameter("minFee", minFee);
        }
        if (maxFee != null) {
            nativeQuery.setParameter("maxFee", maxFee);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = nativeQuery.getResultList();
        return rows.stream()
                .map(row -> new DoctorSearchResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (BigDecimal) row[6]))
                .toList();
    }

    private String embedQuery(String queryText) {
        try {
            EmbedResponse response = restTemplate.postForObject(
                    embeddingServiceBaseUrl + "/embed", new EmbedRequest(queryText), EmbedResponse.class);
            if (response == null || response.embedding == null || response.embedding.size() != 384) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI service returned an invalid query embedding");
            }
            StringJoiner vector = new StringJoiner(",", "[", "]");
            response.embedding.forEach(value -> vector.add(Float.toString(value)));
            return vector.toString();
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI embedding service is unavailable", exception);
        }
    }

    private static String orderBy(String sort) {
        String normalized = sort == null ? "RELEVANCE" : sort.toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "FEE_ASC" -> "dp.consultation_fee ASC, dp.user_id ASC";
            case "FEE_DESC" -> "dp.consultation_fee DESC, dp.user_id ASC";
            case "NAME_ASC" -> "lower(u.name) ASC, dp.user_id ASC";
            case "RELEVANCE" -> "de.embedding <=> CAST(:embedding AS vector) ASC, dp.user_id ASC";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort option");
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static void setPattern(Query query, String name, String value) {
        if (hasText(value)) {
            query.setParameter(name, "%" + value.trim() + "%");
        }
    }

    private record EmbedRequest(String text) {
    }

    public static class EmbedResponse {
        private List<Float> embedding;

        public List<Float> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Float> embedding) {
            this.embedding = embedding;
        }
    }
}