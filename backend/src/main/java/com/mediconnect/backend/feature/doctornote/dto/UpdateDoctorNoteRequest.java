package com.mediconnect.backend.feature.doctornote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing clinical note")
public class UpdateDoctorNoteRequest {

    public UpdateDoctorNoteRequest() {
    }

    public UpdateDoctorNoteRequest(String noteContent) {
        this.noteContent = noteContent;
    }

    @NotBlank(message = "Note content is required")
    @Schema(description = "Updated clinical notes", example = "Patient feeling much better after medication.")
    private String noteContent;
}

