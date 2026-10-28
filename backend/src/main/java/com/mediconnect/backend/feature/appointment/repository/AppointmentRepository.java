package com.mediconnect.backend.feature.appointment.repository;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_UserId(Long patientUserId);

    List<Appointment> findBySlot_DoctorProfile_UserId(Long doctorUserId);

    List<Appointment> findByStatusIgnoreCase(String status);

    List<Appointment> findByPatient_UserIdAndStatusIgnoreCase(Long patientUserId, String status);

    List<Appointment> findBySlot_DoctorProfile_UserIdAndStatusIgnoreCase(Long doctorUserId, String status);
}

