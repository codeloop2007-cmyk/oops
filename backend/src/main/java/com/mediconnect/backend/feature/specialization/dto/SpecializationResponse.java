package com.mediconnect.backend.feature.specialization.dto;

import com.mediconnect.backend.feature.specialization.entity.Specialization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Specialization response payload")
public class SpecializationResponse {

    public SpecializationResponse() {
    }

    public SpecializationResponse(Long specializationId, String name) {
        this.specializationId = specializationId;
        this.name = name;
    }

    @Schema(description = "Specialization unique identifier", example = "1")
    private Long specializationId;

    @Schema(description = "Name of the medical specialization", example = "Cardiology")
    private String name;

    public static SpecializationResponse fromEntity(Specialization specialization) {
        if (specialization == null) {
            return null;
        }
        return new SpecializationResponse(specialization.getSpecializationId(), specialization.getName());
    }
}

