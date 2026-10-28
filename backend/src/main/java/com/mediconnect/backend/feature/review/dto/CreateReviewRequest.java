package com.mediconnect.backend.feature.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for leaving a review on a completed appointment")
public class CreateReviewRequest {

    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Long appointmentId, Integer rating, String comment) {
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
    }

    @NotNull(message = "Appointment ID is required")
    @Schema(description = "Appointment identifier", example = "1")
    private Long appointmentId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Schema(description = "Rating from 1 to 5 stars", example = "5")
    private Integer rating;

    @Schema(description = "Optional patient review comments or feedback", example = "Dr. Smith was very attentive and helpful.")
    private String comment;
}

