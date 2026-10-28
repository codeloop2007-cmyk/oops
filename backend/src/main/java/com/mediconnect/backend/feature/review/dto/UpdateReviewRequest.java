package com.mediconnect.backend.feature.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing review")
public class UpdateReviewRequest {

    public UpdateReviewRequest() {
    }

    public UpdateReviewRequest(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Schema(description = "Updated rating from 1 to 5 stars", example = "4")
    private Integer rating;

    @Schema(description = "Updated review feedback", example = "Overall good experience.")
    private String comment;
}

