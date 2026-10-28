package com.mediconnect.backend.feature.doctorspecialization.service;

import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.doctorspecialization.entity.DoctorSpecialization;
import com.mediconnect.backend.feature.doctorspecialization.entity.DoctorSpecializationId;
import com.mediconnect.backend.feature.doctorspecialization.exception.SpecializationAlreadyAssignedException;
import com.mediconnect.backend.feature.doctorspecialization.exception.SpecializationNotAssignedException;
import com.mediconnect.backend.feature.doctorspecialization.repository.DoctorSpecializationRepository;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import com.mediconnect.backend.feature.specialization.entity.Specialization;
import com.mediconnect.backend.feature.specialization.exception.SpecializationNotFoundException;
import com.mediconnect.backend.feature.specialization.repository.SpecializationRepository;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorSpecializationService {

    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorEmbeddingRepository doctorEmbeddingRepository;

    public DoctorSpecializationService(
            DoctorSpecializationRepository doctorSpecializationRepository,
            DoctorProfileRepository doctorProfileRepository,
            SpecializationRepository specializationRepository,
            DoctorEmbeddingRepository doctorEmbeddingRepository) {
        this.doctorSpecializationRepository = doctorSpecializationRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.specializationRepository = specializationRepository;
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
    }

    @Transactional
    public SpecializationResponse addSpecializationToDoctor(Long doctorId, Long specializationId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId));

        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new SpecializationNotFoundException("Specialization not found with id: " + specializationId));

        DoctorSpecializationId id = new DoctorSpecializationId(doctorId, specializationId);
        if (doctorSpecializationRepository.existsById(id)) {
            throw new SpecializationAlreadyAssignedException(
                    "Specialization '" + specialization.getName() + "' is already assigned to doctor id: " + doctorId);
        }

        DoctorSpecialization doctorSpecialization = new DoctorSpecialization();
        doctorSpecialization.setId(id);
        doctorSpecialization.setDoctorProfile(doctorProfile);
        doctorSpecialization.setSpecialization(specialization);

        doctorSpecializationRepository.save(doctorSpecialization);

        // Specializations are part of the embedding source text — mark as stale
        markEmbeddingStale(doctorId);

        return SpecializationResponse.fromEntity(specialization);
    }

    @Transactional
    public void removeSpecializationFromDoctor(Long doctorId, Long specializationId) {
        if (!doctorProfileRepository.existsById(doctorId)) {
            throw new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId);
        }

        if (!specializationRepository.existsById(specializationId)) {
            throw new SpecializationNotFoundException("Specialization not found with id: " + specializationId);
        }

        DoctorSpecializationId id = new DoctorSpecializationId(doctorId, specializationId);
        if (!doctorSpecializationRepository.existsById(id)) {
            throw new SpecializationNotAssignedException(
                    "Specialization with id " + specializationId + " is not assigned to doctor id: " + doctorId);
        }

        doctorSpecializationRepository.deleteById(id);

        // Specializations are part of the embedding source text — mark as stale
        markEmbeddingStale(doctorId);
    }

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getSpecializationsForDoctor(Long doctorId) {
        if (!doctorProfileRepository.existsById(doctorId)) {
            throw new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId);
        }

    return doctorSpecializationRepository
            .findByDoctorProfile_UserId(doctorId)
            .stream()
            .filter(Objects::nonNull)
            .map(DoctorSpecialization::getSpecialization)
            .filter(Objects::nonNull)
            .map(SpecializationResponse::fromEntity)
            .toList();
    }

    // ---- private helper ----

    /** Sets {@code is_up_to_date = false} on the doctor's embedding row, if it exists. */
    private void markEmbeddingStale(Long doctorId) {
        doctorEmbeddingRepository.findByDoctorProfile_UserId(doctorId)
                .ifPresent(embedding -> {
                    embedding.setIsUpToDate(false);
                    doctorEmbeddingRepository.save(embedding);
                });
    }
}

