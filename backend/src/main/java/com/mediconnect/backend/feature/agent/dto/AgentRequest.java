package com.mediconnect.backend.feature.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentRequest(
        @Schema(
                description = "Natural-language medical inquiry or symptom description",
                example = "I have severe chest pain and feel breathless, who should I see?"
        )
        @NotBlank(message = "Query cannot be blank")
        @Size(min = 2, max = 1000, message = "Query must be between 2 and 1000 characters")
        String query
) {
}

