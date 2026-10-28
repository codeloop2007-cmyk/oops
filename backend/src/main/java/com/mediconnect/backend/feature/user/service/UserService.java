package com.mediconnect.backend.feature.user.service;

import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.user.dto.RegisterUserRequest;
import com.mediconnect.backend.feature.user.dto.UserResponse;
import com.mediconnect.backend.feature.user.entity.RegistrationStatus;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.DuplicateResourceException;
import com.mediconnect.backend.feature.user.exception.UserAlreadyRegisteredException;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSyncHelper userSyncHelper;
    private final DoctorEmbeddingRepository doctorEmbeddingRepository;

    public UserService(UserRepository userRepository, UserSyncHelper userSyncHelper,
                       DoctorEmbeddingRepository doctorEmbeddingRepository) {
        this.userRepository = userRepository;
        this.userSyncHelper = userSyncHelper;
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
    }

    public UserResponse syncUser(UUID clientIdentityId) {
        Optional<User> existingUser = userRepository.findByClientIdentityId(clientIdentityId);
        if (existingUser.isPresent()) {
            return UserResponse.fromEntity(existingUser.get());
        }

        try {
            User createdUser = userSyncHelper.createPendingUser(clientIdentityId);
            return UserResponse.fromEntity(createdUser);
        } catch (DataIntegrityViolationException ex) {
            return userRepository.findByClientIdentityId(clientIdentityId)
                    .map(UserResponse::fromEntity)
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional
    public UserResponse registerUser(UUID clientIdentityId, RegisterUserRequest request) {
        User user = userRepository.findByClientIdentityId(clientIdentityId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found for client identity. Please synchronize first via GET /api/v1/auth/me"));

        if (user.getRegistrationStatus() == RegistrationStatus.COMPLETED) {
            throw new UserAlreadyRegisteredException("User registration has already been completed");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPhone = request.getPhone().trim();

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getUserId().equals(user.getUserId()))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Email address is already in use by another user");
                });

        userRepository.findByPhone(normalizedPhone)
                .filter(existing -> !existing.getUserId().equals(user.getUserId()))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Phone number is already in use by another user");
                });

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        if (request.getPreferredLanguage() != null && !request.getPreferredLanguage().isBlank()) {
            user.setPreferredLanguage(request.getPreferredLanguage().trim());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        user.setRegistrationStatus(RegistrationStatus.COMPLETED);
        user.setUpdatedAt(Instant.now());

        try {
            User savedUser = userRepository.save(user);
            return UserResponse.fromEntity(savedUser);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Email address or phone number is already in use");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long userId, com.mediconnect.backend.feature.user.dto.UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        boolean embeddingRelevantChange = false;

        if (request.getName() != null && !request.getName().isBlank()) {
            embeddingRelevantChange = true;
            user.setName(request.getName().trim());
        }
        if (request.getPreferredLanguage() != null && !request.getPreferredLanguage().isBlank()) {
            embeddingRelevantChange = true;
            user.setPreferredLanguage(request.getPreferredLanguage().trim());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);

        // If name or language changed AND this user is a doctor, mark the embedding stale
        if (embeddingRelevantChange) {
            doctorEmbeddingRepository.findByDoctorProfile_UserId(userId)
                    .ifPresent(embedding -> {
                        embedding.setIsUpToDate(false);
                        doctorEmbeddingRepository.save(embedding);
                    });
        }

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
