package com.mediconnect.backend.feature.user.service;

import com.mediconnect.backend.feature.user.entity.RegistrationStatus;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserSyncHelper {

    private final UserRepository userRepository;

    public UserSyncHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User createPendingUser(UUID clientIdentityId) {
        User newUser = new User();
        newUser.setClientIdentityId(clientIdentityId);
        newUser.setRegistrationStatus(RegistrationStatus.PENDING);
        Instant now = Instant.now();
        newUser.setCreatedAt(now);
        newUser.setUpdatedAt(now);
        return userRepository.saveAndFlush(newUser);
    }
}

