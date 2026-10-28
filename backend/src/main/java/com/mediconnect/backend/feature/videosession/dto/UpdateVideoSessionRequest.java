package com.mediconnect.backend.feature.videosession.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating a video consultation session status or timing")
public class UpdateVideoSessionRequest {

    public UpdateVideoSessionRequest() {
    }

    public UpdateVideoSessionRequest(String sessionStatus, Instant startedAt, Instant endedAt) {
        this.sessionStatus = sessionStatus;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    @Schema(description = "Updated session status (e.g. ACTIVE, COMPLETED, CANCELLED)", example = "ACTIVE")
    private String sessionStatus;

    @Schema(description = "Timestamp when the video call actually started", example = "2026-10-19T10:05:00Z")
    private Instant startedAt;

    @Schema(description = "Timestamp when the video call concluded", example = "2026-10-19T10:35:00Z")
    private Instant endedAt;
}

