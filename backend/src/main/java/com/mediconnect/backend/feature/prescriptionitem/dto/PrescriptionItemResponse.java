package com.mediconnect.backend.feature.prescriptionitem.dto;

import com.mediconnect.backend.feature.prescriptionitem.entity.PrescriptionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a medication item on a prescription")
public class PrescriptionItemResponse {

    public PrescriptionItemResponse() {
    }

    public PrescriptionItemResponse(Long itemId, Long prescriptionId, String medicationName,
                                    String strength, String dosage, String frequency,
                                    String duration, String instructions) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.medicationName = medicationName;
        this.strength = strength;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    @Schema(description = "Prescription item identifier", example = "1")
    private Long itemId;

    @Schema(description = "Parent prescription identifier", example = "1")
    private Long prescriptionId;

    @Schema(description = "Medication name", example = "Amoxicillin")
    private String medicationName;

    @Schema(description = "Strength", example = "500mg")
    private String strength;

    @Schema(description = "Dosage", example = "1 capsule")
    private String dosage;

    @Schema(description = "Frequency", example = "3 times daily")
    private String frequency;

    @Schema(description = "Duration", example = "7 days")
    private String duration;

    @Schema(description = "Special instructions", example = "Take with food")
    private String instructions;

    public static PrescriptionItemResponse fromEntity(PrescriptionItem item) {
        if (item == null) {
            return null;
        }
        return new PrescriptionItemResponse(
                item.getItemId(),
                item.getPrescription() != null ? item.getPrescription().getPrescriptionId() : null,
                item.getMedicationName(),
                item.getStrength(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getInstructions()
        );
    }
}

