package com.mediconnect.backend.feature.doctornote.dto;

import com.mediconnect.backend.feature.doctornote.entity.DoctorNote;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a clinical doctor note")
public class DoctorNoteResponse {

    public DoctorNoteResponse() {
    }

    public DoctorNoteResponse(Long noteId, Long doctorUserId, Long patientUserId, Long appointmentId,
                              String noteContent, Instant createdAt, Instant updatedAt) {
        this.noteId = noteId;
        this.doctorUserId = doctorUserId;
        this.patientUserId = patientUserId;
        this.appointmentId = appointmentId;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Schema(description = "Note identifier", example = "1")
    private Long noteId;

    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @Schema(description = "Patient user identifier", example = "2")
    private Long patientUserId;

    @Schema(description = "Associated appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Clinical note content", example = "Patient reported mild fever and fatigue.")
    private String noteContent;

    @Schema(description = "Creation timestamp", example = "2026-10-19T10:00:00Z")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp", example = "2026-10-19T10:00:00Z")
    private Instant updatedAt;

    public static DoctorNoteResponse fromEntity(DoctorNote note) {
        if (note == null) {
            return null;
        }
        return new DoctorNoteResponse(
                note.getNoteId(),
                note.getDoctorProfile() != null ? note.getDoctorProfile().getUserId() : null,
                note.getPatient() != null ? note.getPatient().getUserId() : null,
                note.getAppointment() != null ? note.getAppointment().getAppointmentId() : null,
                note.getNoteContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}

