package com.mediconnect.backend.feature.prescription.dto;

import com.mediconnect.backend.feature.prescriptionitem.dto.CreatePrescriptionItemRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for issuing a prescription")
public class CreatePrescriptionRequest {

    public CreatePrescriptionRequest() {
    }

    public CreatePrescriptionRequest(Long patientUserId, Long doctorUserId, Long appointmentId,
                                     String status, String notes, Instant issuedAt,
                                     List<CreatePrescriptionItemRequest> items) {
        this.patientUserId = patientUserId;
        this.doctorUserId = doctorUserId;
        this.appointmentId = appointmentId;
        this.status = status;
        this.notes = notes;
        this.issuedAt = issuedAt;
        this.items = items;
    }

    @NotNull(message = "Patient user ID is required")
    @Schema(description = "Patient user identifier", example = "2")
    private Long patientUserId;

    @NotNull(message = "Doctor user ID is required")
    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @Schema(description = "Optional appointment identifier", example = "1")
    private Long appointmentId;

    @Schema(description = "Prescription status (ACTIVE, FILLED, CANCELLED)", example = "ACTIVE", defaultValue = "ACTIVE")
    private String status = "ACTIVE";

    @Schema(description = "General prescription notes or warnings", example = "Complete full antibiotic course.")
    private String notes;

    @Schema(description = "Date and time when prescription was issued", example = "2026-10-19T10:00:00Z")
    private Instant issuedAt;

    @Valid
    @Schema(description = "List of medication items prescribed")
    private List<CreatePrescriptionItemRequest> items = new ArrayList<>();
}

