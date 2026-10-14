package com.mediconnect.backend.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgentRequest {

    @NotBlank(message = "query is required")
    @Size(min = 2, max = 1000, message = "query must be between 2 and 1000 characters")
    private String query;
}