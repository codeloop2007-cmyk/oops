package com.mediconnect.backend.feature.specialization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for creating a medical specialization")
public class CreateSpecializationRequest {

    public CreateSpecializationRequest() {
    }

    public CreateSpecializationRequest(String name) {
        this.name = name;
    }

    @NotBlank(message = "Specialization name must not be blank")
    @Size(max = 100, message = "Specialization name must not exceed 100 characters")
    @Schema(description = "Name of the medical specialization", example = "Cardiology", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}

