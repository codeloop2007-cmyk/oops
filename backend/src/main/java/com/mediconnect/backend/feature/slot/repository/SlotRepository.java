package com.mediconnect.backend.feature.slot.repository;

import com.mediconnect.backend.feature.slot.entity.Slot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByDoctorProfile_UserId(Long doctorUserId);

    List<Slot> findByDoctorProfile_UserIdAndIsBookable(Long doctorUserId, Boolean isBookable);

    List<Slot> findByIsBookable(Boolean isBookable);
}

