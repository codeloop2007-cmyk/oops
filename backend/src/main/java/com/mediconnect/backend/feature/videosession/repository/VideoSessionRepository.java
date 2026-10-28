package com.mediconnect.backend.feature.videosession.repository;

import com.mediconnect.backend.feature.videosession.entity.VideoSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoSessionRepository extends JpaRepository<VideoSession, Long> {

    Optional<VideoSession> findByAppointment_AppointmentId(Long appointmentId);

    Optional<VideoSession> findByRoomId(String roomId);

    boolean existsByAppointment_AppointmentId(Long appointmentId);

    boolean existsByRoomId(String roomId);
}

