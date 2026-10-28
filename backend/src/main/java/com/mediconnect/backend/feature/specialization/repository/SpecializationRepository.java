package com.mediconnect.backend.feature.specialization.repository;

import com.mediconnect.backend.feature.specialization.entity.Specialization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    Optional<Specialization> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndSpecializationIdNot(String name, Long specializationId);
}

