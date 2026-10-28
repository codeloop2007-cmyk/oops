package com.mediconnect.backend.feature.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing appointment")
public class UpdateAppointmentRequest {

    public UpdateAppointmentRequest() {
    }

    public UpdateAppointmentRequest(String status, String consultationMode, String reason) {
        this.status = status;
        this.consultationMode = consultationMode;
        this.reason = reason;
    }

    @Schema(description = "Appointment status (e.g. CONFIRMED, CANCELLED, COMPLETED)", example = "CONFIRMED")
    private String status;

    @Schema(description = "Consultation mode (e.g. VIDEO, IN_PERSON)", example = "VIDEO")
    private String consultationMode;

    @Schema(description = "Reason for consultation or follow-up notes", example = "Follow-up consultation")
    private String reason;
}

