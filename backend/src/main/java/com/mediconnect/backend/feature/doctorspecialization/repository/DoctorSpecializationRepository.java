package com.mediconnect.backend.feature.doctorspecialization.repository;

import com.mediconnect.backend.feature.doctorspecialization.entity.DoctorSpecialization;
import com.mediconnect.backend.feature.doctorspecialization.entity.DoctorSpecializationId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, DoctorSpecializationId> {

    List<DoctorSpecialization> findByDoctorProfile_UserId(Long doctorUserId);

    Optional<DoctorSpecialization> findById_DoctorUserIdAndId_SpecializationId(Long doctorUserId, Long specializationId);

    boolean existsById_DoctorUserIdAndId_SpecializationId(Long doctorUserId, Long specializationId);

    void deleteById_DoctorUserIdAndId_SpecializationId(Long doctorUserId, Long specializationId);
}

