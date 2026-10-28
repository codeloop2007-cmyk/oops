package com.mediconnect.backend.feature.doctorprofile.service;

import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.doctorprofile.dto.CreateDoctorProfileRequest;
import com.mediconnect.backend.feature.doctorprofile.dto.DoctorProfileResponse;
import com.mediconnect.backend.feature.doctorprofile.dto.UpdateDoctorProfileRequest;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.exception.DuplicateLicenseNumberException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.doctorspecialization.repository.DoctorSpecializationRepository;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.DuplicateResourceException;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final DoctorEmbeddingRepository doctorEmbeddingRepository;

    public DoctorProfileService(
            DoctorProfileRepository doctorProfileRepository,
            UserRepository userRepository,
            DoctorSpecializationRepository doctorSpecializationRepository,
            DoctorEmbeddingRepository doctorEmbeddingRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
        this.doctorSpecializationRepository = doctorSpecializationRepository;
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
    }

    @Transactional
    public DoctorProfileResponse createDoctorProfile(CreateDoctorProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

        if (doctorProfileRepository.existsById(request.getUserId())) {
            throw new DuplicateResourceException("Doctor profile already exists for user id: " + request.getUserId());
        }

        String trimmedLicense = request.getLicenseNumber().trim();
        if (doctorProfileRepository.existsByLicenseNumber(trimmedLicense)) {
            throw new DuplicateLicenseNumberException("Doctor profile with license number '" + trimmedLicense + "' already exists");
        }

        DoctorProfile profile = new DoctorProfile();
        profile.setUser(user);
        profile.setUserId(user.getUserId());
        user.setDoctorProfile(profile);
        profile.setLicenseNumber(trimmedLicense);
        profile.setQualification(request.getQualification().trim());
        profile.setBio(request.getBio() != null ? request.getBio().trim() : null);
        profile.setConsultationFee(request.getConsultationFee());
        profile.setVerificationStatus(request.getVerificationStatus() != null && !request.getVerificationStatus().isBlank()
                ? request.getVerificationStatus().trim()
                : "PENDING");
        profile.setTimezone(request.getTimezone().trim());

        DoctorProfile saved = doctorProfileRepository.save(profile);
        return DoctorProfileResponse.fromEntity(saved, List.of());
    }

    @Transactional(readOnly = true)
    public DoctorProfileResponse getDoctorProfile(Long doctorId) {
        DoctorProfile profile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId));

        List<SpecializationResponse> specializations = doctorSpecializationRepository.findByDoctorProfile_UserId(doctorId)
                .stream()
                .map(ds -> SpecializationResponse.fromEntity(ds.getSpecialization()))
                .toList();

        return DoctorProfileResponse.fromEntity(profile, specializations);
    }

    @Transactional(readOnly = true)
    public List<DoctorProfileResponse> getAllDoctorProfiles() {
        return doctorProfileRepository.findAll().stream()
                .map(profile -> {
                    List<SpecializationResponse> specializations = doctorSpecializationRepository.findByDoctorProfile_UserId(profile.getUserId())
                            .stream()
                            .map(ds -> SpecializationResponse.fromEntity(ds.getSpecialization()))
                            .toList();
                    return DoctorProfileResponse.fromEntity(profile, specializations);
                })
                .toList();
    }

    @Transactional
    public DoctorProfileResponse updateDoctorProfile(Long doctorId, UpdateDoctorProfileRequest request) {
        DoctorProfile profile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId));

        if (request.getLicenseNumber() != null && !request.getLicenseNumber().isBlank()) {
            String trimmedLicense = request.getLicenseNumber().trim();
            doctorProfileRepository.findByLicenseNumber(trimmedLicense)
                    .filter(existing -> !existing.getUserId().equals(doctorId))
                    .ifPresent(existing -> {
                        throw new DuplicateLicenseNumberException("License number '" + trimmedLicense + "' is already in use by another doctor");
                    });
            profile.setLicenseNumber(trimmedLicense);
        }

        if (request.getQualification() != null && !request.getQualification().isBlank()) {
            profile.setQualification(request.getQualification().trim());
        }

        if (request.getBio() != null) {
            profile.setBio(request.getBio().trim());
        }

        if (request.getConsultationFee() != null) {
            profile.setConsultationFee(request.getConsultationFee());
        }

        if (request.getVerificationStatus() != null && !request.getVerificationStatus().isBlank()) {
            profile.setVerificationStatus(request.getVerificationStatus().trim());
        }

        if (request.getTimezone() != null && !request.getTimezone().isBlank()) {
            profile.setTimezone(request.getTimezone().trim());
        }

        DoctorProfile updated = doctorProfileRepository.save(profile);

        // Mark embedding as stale so the worker recalculates it
        doctorEmbeddingRepository.findByDoctorProfile_UserId(doctorId)
                .ifPresent(embedding -> {
                    embedding.setIsUpToDate(false);
                    doctorEmbeddingRepository.save(embedding);
                });

        List<SpecializationResponse> specializations = doctorSpecializationRepository.findByDoctorProfile_UserId(doctorId)
                .stream()
                .map(ds -> SpecializationResponse.fromEntity(ds.getSpecialization()))
                .toList();

        return DoctorProfileResponse.fromEntity(updated, specializations);
    }

    @Transactional
    public void deleteDoctorProfile(Long doctorId) {
        if (!doctorProfileRepository.existsById(doctorId)) {
            throw new DoctorProfileNotFoundException("Doctor profile not found with id: " + doctorId);
        }
        doctorProfileRepository.deleteById(doctorId);
    }
}

