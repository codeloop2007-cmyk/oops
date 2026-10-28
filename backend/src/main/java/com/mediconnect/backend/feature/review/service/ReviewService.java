package com.mediconnect.backend.feature.review.service;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException;
import com.mediconnect.backend.feature.appointment.repository.AppointmentRepository;
import com.mediconnect.backend.feature.review.dto.CreateReviewRequest;
import com.mediconnect.backend.feature.review.dto.ReviewResponse;
import com.mediconnect.backend.feature.review.dto.UpdateReviewRequest;
import com.mediconnect.backend.feature.review.entity.Review;
import com.mediconnect.backend.feature.review.exception.DuplicateReviewException;
import com.mediconnect.backend.feature.review.exception.ReviewNotFoundException;
import com.mediconnect.backend.feature.review.repository.ReviewRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    public ReviewService(ReviewRepository reviewRepository, AppointmentRepository appointmentRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        if (reviewRepository.existsByAppointment_AppointmentId(request.getAppointmentId())) {
            throw new DuplicateReviewException("A review has already been submitted for appointment id: " + request.getAppointmentId());
        }

        Review review = new Review();
        review.setAppointment(appointment);
        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment().trim() : null);
        review.setCreatedAt(Instant.now());

        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        return ReviewResponse.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long doctorUserId, Long patientUserId) {
        List<Review> reviews;
        if (doctorUserId != null) {
            reviews = reviewRepository.findByAppointment_Slot_DoctorProfile_UserId(doctorUserId);
        } else if (patientUserId != null) {
            reviews = reviewRepository.findByAppointment_Patient_UserId(patientUserId);
        } else {
            reviews = reviewRepository.findAll();
        }
        return reviews.stream().map(ReviewResponse::fromEntity).toList();
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment().trim());
        }

        Review updated = reviewRepository.save(review);
        return ReviewResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}

