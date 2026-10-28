package com.mediconnect.backend.feature.prescriptionitem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an individual prescription medication item")
public class UpdatePrescriptionItemRequest {

    public UpdatePrescriptionItemRequest() {
    }

    public UpdatePrescriptionItemRequest(String medicationName, String strength, String dosage,
                                         String frequency, String duration, String instructions) {
        this.medicationName = medicationName;
        this.strength = strength;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    @Schema(description = "Medication name", example = "Amoxicillin")
    private String medicationName;

    @Schema(description = "Strength", example = "500mg")
    private String strength;

    @Schema(description = "Dosage", example = "1 capsule")
    private String dosage;

    @Schema(description = "Frequency", example = "3 times daily")
    private String frequency;

    @Schema(description = "Duration", example = "10 days")
    private String duration;

    @Schema(description = "Instructions", example = "Take with a glass of water")
    private String instructions;
}

