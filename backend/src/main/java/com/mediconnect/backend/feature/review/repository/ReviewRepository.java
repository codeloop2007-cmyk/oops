package com.mediconnect.backend.feature.review.repository;

import com.mediconnect.backend.feature.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByAppointment_AppointmentId(Long appointmentId);

    List<Review> findByAppointment_Slot_DoctorProfile_UserId(Long doctorUserId);

    List<Review> findByAppointment_Patient_UserId(Long patientUserId);

    boolean existsByAppointment_AppointmentId(Long appointmentId);
}

