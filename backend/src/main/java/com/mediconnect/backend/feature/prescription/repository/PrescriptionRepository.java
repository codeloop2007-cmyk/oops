package com.mediconnect.backend.feature.prescription.repository;

import com.mediconnect.backend.feature.prescription.entity.Prescription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatient_UserId(Long patientUserId);

    List<Prescription> findByDoctorProfile_UserId(Long doctorUserId);

    List<Prescription> findByAppointment_AppointmentId(Long appointmentId);

    List<Prescription> findByStatusIgnoreCase(String status);
}

