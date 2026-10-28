package com.mediconnect.backend.feature.specialization.service;

import com.mediconnect.backend.feature.specialization.dto.CreateSpecializationRequest;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import com.mediconnect.backend.feature.specialization.entity.Specialization;
import com.mediconnect.backend.feature.specialization.exception.DuplicateSpecializationException;
import com.mediconnect.backend.feature.specialization.exception.SpecializationNotFoundException;
import com.mediconnect.backend.feature.specialization.repository.SpecializationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    @Transactional
    public SpecializationResponse createSpecialization(CreateSpecializationRequest request) {
        String trimmedName = request.getName().trim();
        if (specializationRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateSpecializationException("Specialization with name '" + trimmedName + "' already exists");
        }

        Specialization specialization = new Specialization();
        specialization.setName(trimmedName);

        Specialization saved = specializationRepository.save(specialization);
        return SpecializationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public SpecializationResponse getSpecializationById(Long specializationId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new SpecializationNotFoundException("Specialization not found with id: " + specializationId));
        return SpecializationResponse.fromEntity(specialization);
    }

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getAllSpecializations() {
        return specializationRepository.findAll().stream()
                .map(SpecializationResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SpecializationResponse updateSpecialization(Long specializationId, CreateSpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new SpecializationNotFoundException("Specialization not found with id: " + specializationId));

        String trimmedName = request.getName().trim();
        if (specializationRepository.existsByNameIgnoreCaseAndSpecializationIdNot(trimmedName, specializationId)) {
            throw new DuplicateSpecializationException("Specialization with name '" + trimmedName + "' already exists");
        }

        specialization.setName(trimmedName);
        Specialization saved = specializationRepository.save(specialization);
        return SpecializationResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteSpecialization(Long specializationId) {
        if (!specializationRepository.existsById(specializationId)) {
            throw new SpecializationNotFoundException("Specialization not found with id: " + specializationId);
        }
        specializationRepository.deleteById(specializationId);
    }
}

