package com.mediconnect.backend.feature.doctornote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for creating a clinical note")
public class CreateDoctorNoteRequest {

    public CreateDoctorNoteRequest() {
    }

    public CreateDoctorNoteRequest(Long doctorUserId, Long patientUserId, Long appointmentId, String noteContent) {
        this.doctorUserId = doctorUserId;
        this.patientUserId = patientUserId;
        this.appointmentId = appointmentId;
        this.noteContent = noteContent;
    }

    @NotNull(message = "Doctor user ID is required")
    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @NotNull(message = "Patient user ID is required")
    @Schema(description = "Patient user identifier", example = "2")
    private Long patientUserId;

    @Schema(description = "Optional appointment identifier", example = "1")
    private Long appointmentId;

    @NotBlank(message = "Note content is required")
    @Schema(description = "Clinical notes, observations, or diagnostic findings", example = "Patient reported mild fever and fatigue. Vitals normal.")
    private String noteContent;
}

