package com.mediconnect.backend.feature.doctorembedding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for storing or updating vector embeddings for a doctor profile")
public class UpsertDoctorEmbeddingRequest {

    public UpsertDoctorEmbeddingRequest() {
    }

    public UpsertDoctorEmbeddingRequest(List<Float> embedding, String embeddedModel, String sourceHash, Boolean isUpToDate) {
        this.embedding = embedding;
        this.embeddedModel = embeddedModel;
        this.sourceHash = sourceHash;
        this.isUpToDate = isUpToDate;
    }

    @NotEmpty(message = "Embedding vector is required")
    @Schema(description = "Vector representation array of floating point values (length 384)", example = "[0.012, -0.045, 0.128]")
    private List<Float> embedding;

    @NotBlank(message = "Embedded model name is required")
    @Schema(description = "Name of the AI embedding model used", example = "all-MiniLM-L6-v2")
    private String embeddedModel;

    @NotBlank(message = "Source hash is required")
    @Schema(description = "Hash of the source text used to produce the embedding", example = "sha256-abc123def456")
    private String sourceHash;

    @Schema(description = "Flag whether the vector is in sync with doctor profile", example = "true", defaultValue = "true")
    private Boolean isUpToDate = true;
}

