package com.mediconnect.backend.feature.userlanguage.service;

import com.mediconnect.backend.feature.doctorembedding.repository.DoctorEmbeddingRepository;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import com.mediconnect.backend.feature.userlanguage.dto.AddUserLanguageRequest;
import com.mediconnect.backend.feature.userlanguage.dto.UserLanguageResponse;
import com.mediconnect.backend.feature.userlanguage.entity.UserLanguage;
import com.mediconnect.backend.feature.userlanguage.entity.UserLanguageId;
import com.mediconnect.backend.feature.userlanguage.exception.UserLanguageAlreadyExistsException;
import com.mediconnect.backend.feature.userlanguage.exception.UserLanguageNotFoundException;
import com.mediconnect.backend.feature.userlanguage.repository.UserLanguageRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLanguageService {

    private final UserLanguageRepository userLanguageRepository;
    private final UserRepository userRepository;
    private final DoctorEmbeddingRepository doctorEmbeddingRepository;

    public UserLanguageService(UserLanguageRepository userLanguageRepository,
                               UserRepository userRepository,
                               DoctorEmbeddingRepository doctorEmbeddingRepository) {
        this.userLanguageRepository = userLanguageRepository;
        this.userRepository = userRepository;
        this.doctorEmbeddingRepository = doctorEmbeddingRepository;
    }

    @Transactional
    public UserLanguageResponse addLanguageToUser(Long userId, AddUserLanguageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        String code = request.getLanguageCode().trim().toLowerCase();
        UserLanguageId id = new UserLanguageId(userId, code);

        if (userLanguageRepository.existsById_UserIdAndId_LanguageCode(userId, code)) {
            throw new UserLanguageAlreadyExistsException("Language '" + code + "' already added for user id: " + userId);
        }

        UserLanguage userLanguage = new UserLanguage();
        userLanguage.setId(id);
        userLanguage.setUser(user);

        UserLanguage saved = userLanguageRepository.save(userLanguage);

        // Language is part of the embedding source text — mark doctor embedding as stale if applicable
        markEmbeddingStale(userId);

        return UserLanguageResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<UserLanguageResponse> getUserLanguages(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return userLanguageRepository.findByUser_UserId(userId).stream()
                .map(UserLanguageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void removeLanguageFromUser(Long userId, String languageCode) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        String code = languageCode.trim().toLowerCase();
        if (!userLanguageRepository.existsById_UserIdAndId_LanguageCode(userId, code)) {
            throw new UserLanguageNotFoundException("Language '" + code + "' is not associated with user id: " + userId);
        }

        userLanguageRepository.deleteById_UserIdAndId_LanguageCode(userId, code);

        // Language is part of the embedding source text — mark doctor embedding as stale if applicable
        markEmbeddingStale(userId);
    }

    // ---- private helper ----

    /** Sets {@code is_up_to_date = false} on the doctor's embedding row if this user is a doctor. */
    private void markEmbeddingStale(Long userId) {
        doctorEmbeddingRepository.findByDoctorProfile_UserId(userId)
                .ifPresent(embedding -> {
                    embedding.setIsUpToDate(false);
                    doctorEmbeddingRepository.save(embedding);
                });
    }
}
