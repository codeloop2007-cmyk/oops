package com.mediconnect.backend.feature.doctorembedding.repository;

import com.mediconnect.backend.feature.doctorembedding.entity.DoctorEmbedding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorEmbeddingRepository extends JpaRepository<DoctorEmbedding, Long> {

    Optional<DoctorEmbedding> findByDoctorProfile_UserId(Long doctorUserId);

    boolean existsByDoctorProfile_UserId(Long doctorUserId);

    /** All embeddings that are marked as out-of-date and need recalculation. */
    List<DoctorEmbedding> findAllByIsUpToDateFalse();

    /**
     * Returns doctor user IDs from doctor_profile that have NO embedding row yet.
     * These are newly created doctors whose embedding has never been computed.
     */
    @Query("SELECT dp.userId FROM DoctorProfile dp WHERE dp.userId NOT IN " +
           "(SELECT de.doctorUserId FROM DoctorEmbedding de)")
    List<Long> findDoctorIdsWithoutEmbedding();
}
