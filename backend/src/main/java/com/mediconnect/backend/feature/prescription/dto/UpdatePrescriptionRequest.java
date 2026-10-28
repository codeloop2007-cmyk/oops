package com.mediconnect.backend.feature.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing prescription status or notes")
public class UpdatePrescriptionRequest {

    public UpdatePrescriptionRequest() {
    }

    public UpdatePrescriptionRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    @Schema(description = "Prescription status (ACTIVE, FILLED, CANCELLED)", example = "FILLED")
    private String status;

    @Schema(description = "Updated general prescription notes", example = "Patient picked up medication.")
    private String notes;
}

