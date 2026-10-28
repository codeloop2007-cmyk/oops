package com.mediconnect.backend.feature.videosession.service;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException;
import com.mediconnect.backend.feature.appointment.repository.AppointmentRepository;
import com.mediconnect.backend.feature.videosession.dto.CreateVideoSessionRequest;
import com.mediconnect.backend.feature.videosession.dto.UpdateVideoSessionRequest;
import com.mediconnect.backend.feature.videosession.dto.VideoSessionResponse;
import com.mediconnect.backend.feature.videosession.entity.VideoSession;
import com.mediconnect.backend.feature.videosession.exception.DuplicateVideoSessionException;
import com.mediconnect.backend.feature.videosession.exception.VideoSessionNotFoundException;
import com.mediconnect.backend.feature.videosession.repository.VideoSessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoSessionService {

    private final VideoSessionRepository videoSessionRepository;
    private final AppointmentRepository appointmentRepository;

    public VideoSessionService(
            VideoSessionRepository videoSessionRepository,
            AppointmentRepository appointmentRepository) {
        this.videoSessionRepository = videoSessionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public VideoSessionResponse createVideoSession(CreateVideoSessionRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        if (videoSessionRepository.existsByAppointment_AppointmentId(request.getAppointmentId())) {
            throw new DuplicateVideoSessionException("Video session already exists for appointment id: " + request.getAppointmentId());
        }

        String trimmedRoom = request.getRoomId().trim();
        if (videoSessionRepository.existsByRoomId(trimmedRoom)) {
            throw new DuplicateVideoSessionException("Video session with room id '" + trimmedRoom + "' already exists");
        }

        VideoSession videoSession = new VideoSession();
        videoSession.setAppointment(appointment);
        videoSession.setProvider(request.getProvider().trim());
        videoSession.setRoomId(trimmedRoom);
        videoSession.setJoinCodeHash(request.getJoinCodeHash().trim());
        videoSession.setSessionStatus(request.getSessionStatus() != null && !request.getSessionStatus().isBlank()
                ? request.getSessionStatus().trim().toUpperCase()
                : "WAITING");

        VideoSession saved = videoSessionRepository.save(videoSession);
        return VideoSessionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public VideoSessionResponse getVideoSessionById(Long videoSessionId) {
        VideoSession videoSession = videoSessionRepository.findById(videoSessionId)
                .orElseThrow(() -> new VideoSessionNotFoundException("Video session not found with id: " + videoSessionId));
        return VideoSessionResponse.fromEntity(videoSession);
    }

    @Transactional(readOnly = true)
    public VideoSessionResponse getVideoSessionByAppointmentId(Long appointmentId) {
        VideoSession videoSession = videoSessionRepository.findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() -> new VideoSessionNotFoundException("Video session not found for appointment id: " + appointmentId));
        return VideoSessionResponse.fromEntity(videoSession);
    }

    @Transactional(readOnly = true)
    public List<VideoSessionResponse> getAllVideoSessions() {
        return videoSessionRepository.findAll().stream()
                .map(VideoSessionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public VideoSessionResponse updateVideoSession(Long videoSessionId, UpdateVideoSessionRequest request) {
        VideoSession videoSession = videoSessionRepository.findById(videoSessionId)
                .orElseThrow(() -> new VideoSessionNotFoundException("Video session not found with id: " + videoSessionId));

        if (request.getSessionStatus() != null && !request.getSessionStatus().isBlank()) {
            videoSession.setSessionStatus(request.getSessionStatus().trim().toUpperCase());
        }
        if (request.getStartedAt() != null) {
            videoSession.setStartedAt(request.getStartedAt());
        }
        if (request.getEndedAt() != null) {
            videoSession.setEndedAt(request.getEndedAt());
        }

        VideoSession updated = videoSessionRepository.save(videoSession);
        return VideoSessionResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteVideoSession(Long videoSessionId) {
        if (!videoSessionRepository.existsById(videoSessionId)) {
            throw new VideoSessionNotFoundException("Video session not found with id: " + videoSessionId);
        }
        videoSessionRepository.deleteById(videoSessionId);
    }
}

