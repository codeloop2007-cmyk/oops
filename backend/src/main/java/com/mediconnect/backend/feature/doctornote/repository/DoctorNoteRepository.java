package com.mediconnect.backend.feature.doctornote.repository;

import com.mediconnect.backend.feature.doctornote.entity.DoctorNote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorNoteRepository extends JpaRepository<DoctorNote, Long> {

    List<DoctorNote> findByDoctorProfile_UserId(Long doctorUserId);

    List<DoctorNote> findByPatient_UserId(Long patientUserId);

    List<DoctorNote> findByAppointment_AppointmentId(Long appointmentId);

    List<DoctorNote> findByDoctorProfile_UserIdAndPatient_UserId(Long doctorUserId, Long patientUserId);
}

