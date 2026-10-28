package com.mediconnect.backend.feature.user.repository;

import com.mediconnect.backend.feature.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByClientIdentityId(UUID clientIdentityId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByClientIdentityId(UUID clientIdentityId);
}

