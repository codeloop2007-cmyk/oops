package com.mediconnect.backend.feature.appointment.dto;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of an appointment")
public class AppointmentResponse {

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long appointmentId, Long patientUserId, Long slotId, Long doctorUserId,
                               String status, String consultationMode, String reason,
                               Instant bookedAt, Instant cancelledAt) {
        this.appointmentId = appointmentId;
        this.patientUserId = patientUserId;
        this.slotId = slotId;
        this.doctorUserId = doctorUserId;
        this.status = status;
        this.consultationMode = consultationMode;
        this.reason = reason;
        this.bookedAt = bookedAt;
        this.cancelledAt = cancelledAt;
    }

    @Schema(description = "Appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Patient user identifier", example = "1")
    private Long patientUserId;

    @Schema(description = "Slot identifier", example = "1")
    private Long slotId;

    @Schema(description = "Doctor user identifier", example = "2")
    private Long doctorUserId;

    @Schema(description = "Appointment status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Consultation mode (VIDEO, IN_PERSON)", example = "VIDEO")
    private String consultationMode;

    @Schema(description = "Reason for consultation", example = "Routine checkup")
    private String reason;

    @Schema(description = "Booking timestamp", example = "2026-10-19T10:00:00Z")
    private Instant bookedAt;

    @Schema(description = "Cancellation timestamp (if cancelled)", example = "null")
    private Instant cancelledAt;

    public static AppointmentResponse fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        Long doctorUserId = null;
        if (appointment.getSlot() != null && appointment.getSlot().getDoctorProfile() != null) {
            doctorUserId = appointment.getSlot().getDoctorProfile().getUserId();
        }
        return new AppointmentResponse(
                appointment.getAppointmentId(),
                appointment.getPatient() != null ? appointment.getPatient().getUserId() : null,
                appointment.getSlot() != null ? appointment.getSlot().getSlotId() : null,
                doctorUserId,
                appointment.getStatus(),
                appointment.getConsultationMode(),
                appointment.getReason(),
                appointment.getBookedAt(),
                appointment.getCancelledAt()
        );
    }
}

