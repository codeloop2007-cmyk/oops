package com.mediconnect.backend.feature.doctorembedding.dto;

import com.mediconnect.backend.feature.doctorembedding.entity.DoctorEmbedding;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a doctor profile embedding vector")
public class DoctorEmbeddingResponse {

    public DoctorEmbeddingResponse() {
    }

    public DoctorEmbeddingResponse(Long doctorUserId, Boolean isUpToDate, Instant updatedAt,
                                   String embeddedModel, String sourceHash, List<Float> embedding) {
        this.doctorUserId = doctorUserId;
        this.isUpToDate = isUpToDate;
        this.updatedAt = updatedAt;
        this.embeddedModel = embeddedModel;
        this.sourceHash = sourceHash;
        this.embedding = embedding;
    }

    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @Schema(description = "Whether the embedding matches current doctor profile data", example = "true")
    private Boolean isUpToDate;

    @Schema(description = "Timestamp when embedding was computed", example = "2026-10-19T10:00:00Z")
    private Instant updatedAt;

    @Schema(description = "Model identifier used", example = "all-MiniLM-L6-v2")
    private String embeddedModel;

    @Schema(description = "Hash of source text", example = "sha256-abc123def456")
    private String sourceHash;

    @Schema(description = "Vector representation array of floats")
    private List<Float> embedding;

    public static DoctorEmbeddingResponse fromEntity(DoctorEmbedding entity) {
        if (entity == null) {
            return null;
        }
        List<Float> floatList = new ArrayList<>();
        if (entity.getEmbedding() != null && entity.getEmbedding().toArray() != null) {
            for (float val : entity.getEmbedding().toArray()) {
                floatList.add(val);
            }
        }
        return new DoctorEmbeddingResponse(
                entity.getDoctorUserId(),
                entity.getIsUpToDate(),
                entity.getUpdatedAt(),
                entity.getEmbeddedModel(),
                entity.getSourceHash(),
                floatList
        );
    }
}

