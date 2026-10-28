package com.mediconnect.backend.feature.prescriptionitem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for adding medication item to a prescription")
public class CreatePrescriptionItemRequest {

    public CreatePrescriptionItemRequest() {
    }

    public CreatePrescriptionItemRequest(String medicationName, String strength, String dosage,
                                         String frequency, String duration, String instructions) {
        this.medicationName = medicationName;
        this.strength = strength;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    @NotBlank(message = "Medication name is required")
    @Schema(description = "Name of medication", example = "Amoxicillin")
    private String medicationName;

    @Schema(description = "Strength of medication", example = "500mg")
    private String strength;

    @Schema(description = "Dosage quantity", example = "1 capsule")
    private String dosage;

    @Schema(description = "Dosage frequency", example = "3 times daily")
    private String frequency;

    @Schema(description = "Course duration", example = "7 days")
    private String duration;

    @Schema(description = "Special instructions for patient", example = "Take with food")
    private String instructions;
}

