package com.mediconnect.backend.feature.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for booking a new appointment")
public class CreateAppointmentRequest {

    public CreateAppointmentRequest() {
    }

    public CreateAppointmentRequest(Long patientUserId, Long slotId, String consultationMode, String reason) {
        this.patientUserId = patientUserId;
        this.slotId = slotId;
        this.consultationMode = consultationMode;
        this.reason = reason;
    }

    @NotNull(message = "Patient user ID is required")
    @Schema(description = "Patient user identifier", example = "1")
    private Long patientUserId;

    @NotNull(message = "Slot ID is required")
    @Schema(description = "Slot identifier to book", example = "1")
    private Long slotId;

    @NotBlank(message = "Consultation mode is required")
    @Schema(description = "Consultation mode (e.g. VIDEO, IN_PERSON)", example = "VIDEO")
    private String consultationMode;

    @Schema(description = "Reason for consultation / symptoms", example = "Persistent headache for 3 days")
    private String reason;
}

