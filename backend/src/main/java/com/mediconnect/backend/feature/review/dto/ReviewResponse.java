package com.mediconnect.backend.feature.review.dto;

import com.mediconnect.backend.feature.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a patient review")
public class ReviewResponse {

    public ReviewResponse() {
    }

    public ReviewResponse(Long reviewId, Long appointmentId, Integer rating, String comment, Instant createdAt) {
        this.reviewId = reviewId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @Schema(description = "Review identifier", example = "1")
    private Long reviewId;

    @Schema(description = "Appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Rating given (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "Review comments", example = "Great doctor!")
    private String comment;

    @Schema(description = "Review creation timestamp", example = "2026-10-19T10:00:00Z")
    private Instant createdAt;

    public static ReviewResponse fromEntity(Review review) {
        if (review == null) {
            return null;
        }
        return new ReviewResponse(
                review.getReviewId(),
                review.getAppointment() != null ? review.getAppointment().getAppointmentId() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}

