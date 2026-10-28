package com.mediconnect.backend.feature.prescription.dto;

import com.mediconnect.backend.feature.prescription.entity.Prescription;
import com.mediconnect.backend.feature.prescriptionitem.dto.PrescriptionItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a medical prescription with prescribed medication items")
public class PrescriptionResponse {

    public PrescriptionResponse() {
    }

    public PrescriptionResponse(Long prescriptionId, Long patientUserId, Long doctorUserId,
                                Long appointmentId, String status, String notes,
                                Instant issuedAt, Instant createdAt,
                                List<PrescriptionItemResponse> items) {
        this.prescriptionId = prescriptionId;
        this.patientUserId = patientUserId;
        this.doctorUserId = doctorUserId;
        this.appointmentId = appointmentId;
        this.status = status;
        this.notes = notes;
        this.issuedAt = issuedAt;
        this.createdAt = createdAt;
        this.items = items;
    }

    @Schema(description = "Prescription identifier", example = "1")
    private Long prescriptionId;

    @Schema(description = "Patient user identifier", example = "2")
    private Long patientUserId;

    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @Schema(description = "Associated appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Prescription status", example = "ACTIVE")
    private String status;

    @Schema(description = "General notes", example = "Take with plenty of water.")
    private String notes;

    @Schema(description = "Issue timestamp", example = "2026-10-19T10:00:00Z")
    private Instant issuedAt;

    @Schema(description = "Creation timestamp", example = "2026-10-19T10:00:00Z")
    private Instant createdAt;

    @Schema(description = "List of prescribed medication items")
    private List<PrescriptionItemResponse> items = new ArrayList<>();

    public static PrescriptionResponse fromEntity(Prescription prescription) {
        if (prescription == null) {
            return null;
        }
        List<PrescriptionItemResponse> itemResponses = prescription.getItems() != null
                ? prescription.getItems().stream().map(PrescriptionItemResponse::fromEntity).toList()
                : List.of();

        return new PrescriptionResponse(
                prescription.getPrescriptionId(),
                prescription.getPatient() != null ? prescription.getPatient().getUserId() : null,
                prescription.getDoctorProfile() != null ? prescription.getDoctorProfile().getUserId() : null,
                prescription.getAppointment() != null ? prescription.getAppointment().getAppointmentId() : null,
                prescription.getStatus(),
                prescription.getNotes(),
                prescription.getIssuedAt(),
                prescription.getCreatedAt(),
                itemResponses
        );
    }
}

