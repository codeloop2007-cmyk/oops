package com.mediconnect.backend.feature.videosession.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for initializing a video consultation session")
public class CreateVideoSessionRequest {

    public CreateVideoSessionRequest() {
    }

    public CreateVideoSessionRequest(Long appointmentId, String provider, String roomId, String joinCodeHash, String sessionStatus) {
        this.appointmentId = appointmentId;
        this.provider = provider;
        this.roomId = roomId;
        this.joinCodeHash = joinCodeHash;
        this.sessionStatus = sessionStatus;
    }

    @NotNull(message = "Appointment ID is required")
    @Schema(description = "Appointment identifier", example = "1")
    private Long appointmentId;

    @NotBlank(message = "Provider name is required")
    @Schema(description = "Video provider platform name", example = "WebRTC")
    private String provider;

    @NotBlank(message = "Room ID is required")
    @Schema(description = "Unique video room identifier", example = "room-med-123")
    private String roomId;

    @NotBlank(message = "Join code hash is required")
    @Schema(description = "Hashed secret join passcode for security verification", example = "hash-secret-9876")
    private String joinCodeHash;

    @Schema(description = "Initial session status (e.g. WAITING, ACTIVE)", example = "WAITING", defaultValue = "WAITING")
    private String sessionStatus = "WAITING";
}

