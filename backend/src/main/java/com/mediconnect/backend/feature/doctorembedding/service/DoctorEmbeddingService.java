package com.mediconnect.backend.feature.doctorembedding.service;

import com.mediconnect.backend.feature.doctorembedding.dto.DoctorEmbeddingResponse;
import com.mediconnect.backend.feature.doctorembedding.dto.UpsertDoctorEmbeddingRequest;
import com.mediconnect.backend.feature.doctorembedding.entity.DoctorEmbedding;
import com.mediconnect.backend.feature.doctorembedding.exception.DoctorEmbeddingNotFoundException;
import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.pgvector.PGvector;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorEmbeddingService {

    private final DoctorEmbeddingRepository doctorEmbeddingRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorEmbeddingService(
            DoctorEmbeddingRepository doctorEmbeddingRepository,
            DoctorProfileRepository doctorProfileRepository) {
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Transactional
    public DoctorEmbeddingResponse upsertEmbedding(Long doctorUserId, UpsertDoctorEmbeddingRequest request) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorUserId)
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorUserId));

        DoctorEmbedding embedding = doctorEmbeddingRepository.findById(doctorUserId)
                .orElseGet(() -> {
                    DoctorEmbedding newEmbedding = new DoctorEmbedding();
                    newEmbedding.setDoctorProfile(doctorProfile);
                    newEmbedding.setDoctorUserId(doctorProfile.getUserId());
                    newEmbedding.setNew(true);
                    return newEmbedding;
                });

        if (embedding.getId() != null && doctorEmbeddingRepository.existsById(embedding.getId())) {
            embedding.setNew(false);
        }

        embedding.setEmbedding(new PGvector(request.getEmbedding()));
        embedding.setEmbeddedModel(request.getEmbeddedModel().trim());
        embedding.setSourceHash(request.getSourceHash().trim());
        embedding.setIsUpToDate(request.getIsUpToDate() != null ? request.getIsUpToDate() : true);
        embedding.setUpdatedAt(Instant.now());

        DoctorEmbedding saved = doctorEmbeddingRepository.save(embedding);
        return DoctorEmbeddingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DoctorEmbeddingResponse getEmbeddingByDoctorId(Long doctorUserId) {
        if (!doctorProfileRepository.existsById(doctorUserId)) {
            throw new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorUserId);
        }

        DoctorEmbedding embedding = doctorEmbeddingRepository.findByDoctorProfile_UserId(doctorUserId)
                .orElseThrow(() -> new DoctorEmbeddingNotFoundException("Embedding not found for doctor id: " + doctorUserId));
        return DoctorEmbeddingResponse.fromEntity(embedding);
    }

    @Transactional
    public void deleteEmbedding(Long doctorUserId) {
        if (!doctorProfileRepository.existsById(doctorUserId)) {
            throw new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorUserId);
        }

        DoctorEmbedding embedding = doctorEmbeddingRepository.findByDoctorProfile_UserId(doctorUserId)
                .orElseThrow(() -> new DoctorEmbeddingNotFoundException("Embedding not found for doctor id: " + doctorUserId));
        doctorEmbeddingRepository.delete(embedding);
    }
}

