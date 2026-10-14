package com.mediconnect.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByGmail(String gmail);
    boolean existsByUserId(String userId);
    boolean existsByGmail(String gmail);
}