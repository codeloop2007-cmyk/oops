package com.mediconnect.backend.feature.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record DoctorMatchDto(
        @Schema(description = "Unique doctor ID from the backend", example = "1")
        Long doctorId,

        @Schema(description = "Doctor's full name", example = "Dr. John Doe")
        String name,

        @Schema(description = "Medical specialization", example = "Cardiology")
        String specialization,

        @Schema(description = "Degrees / qualifications", example = "MBBS, MD")
        String qualification,

        @Schema(description = "Languages spoken", example = "English, Spanish")
        String language,

        @Schema(description = "City / clinic location", example = "Boston, MA")
        String location,

        @Schema(description = "Consultation fee", example = "150.00")
        BigDecimal consultationFee
) {
}

