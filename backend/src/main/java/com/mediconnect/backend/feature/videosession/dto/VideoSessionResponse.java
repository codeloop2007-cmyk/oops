package com.mediconnect.backend.feature.videosession.dto;

import com.mediconnect.backend.feature.videosession.entity.VideoSession;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a video consultation session")
public class VideoSessionResponse {

    public VideoSessionResponse() {
    }

    public VideoSessionResponse(Long videoSessionId, Long appointmentId, String provider, String roomId,
                                String sessionStatus, Instant startedAt, Instant endedAt) {
        this.videoSessionId = videoSessionId;
        this.appointmentId = appointmentId;
        this.provider = provider;
        this.roomId = roomId;
        this.sessionStatus = sessionStatus;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    @Schema(description = "Video session identifier", example = "1")
    private Long videoSessionId;

    @Schema(description = "Appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Video provider platform name", example = "WebRTC")
    private String provider;

    @Schema(description = "Unique video room identifier", example = "room-med-123")
    private String roomId;

    @Schema(description = "Session status", example = "ACTIVE")
    private String sessionStatus;

    @Schema(description = "Session start timestamp", example = "2026-10-19T10:05:00Z")
    private Instant startedAt;

    @Schema(description = "Session end timestamp", example = "2026-10-19T10:35:00Z")
    private Instant endedAt;

    public static VideoSessionResponse fromEntity(VideoSession videoSession) {
        if (videoSession == null) {
            return null;
        }
        return new VideoSessionResponse(
                videoSession.getVideoSessionId(),
                videoSession.getAppointment() != null ? videoSession.getAppointment().getAppointmentId() : null,
                videoSession.getProvider(),
                videoSession.getRoomId(),
                videoSession.getSessionStatus(),
                videoSession.getStartedAt(),
                videoSession.getEndedAt()
        );
    }
}

