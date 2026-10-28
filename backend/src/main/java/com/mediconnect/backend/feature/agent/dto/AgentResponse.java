package com.mediconnect.backend.feature.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AgentResponse(
        @Schema(
                description = "Short human-friendly clinical recommendation summary",
                example = "Based on your symptoms of chest pain, you should see a cardiologist for further evaluation."
        )
        String summary,

        @Schema(
                description = "List of doctor IDs matched for this query",
                example = "[1, 3]"
        )
        List<Long> doctorIds,

        @Schema(
                description = "Full details of each matched doctor"
        )
        List<DoctorMatchDto> doctors
) {
}

