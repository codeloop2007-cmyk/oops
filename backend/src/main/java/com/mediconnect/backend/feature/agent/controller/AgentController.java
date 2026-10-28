package com.mediconnect.backend.feature.agent.controller;

import com.mediconnect.backend.feature.agent.dto.AgentRequest;
import com.mediconnect.backend.feature.agent.dto.AgentResponse;
import com.mediconnect.backend.feature.agent.service.AgentService;
import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Agent", description = "AI Medical Assistant Agent endpoints")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(
            summary = "Ask the medical assistant",
            description = "Sends a natural-language query to the Gemini ReAct agent running on the Python AI service. "
                    + "The agent decides which tools to call (e.g. `find_doctor`) and returns a structured response with "
                    + "summary, doctorIds, and doctor details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Agent recommendation successfully generated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AgentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error on request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "AI agent service bad gateway",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "AI agent service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping({"/api/v1/agent/ask", "/api/agent/ask", "/api/v1/agent"})
    public AgentResponse ask(@Valid @RequestBody AgentRequest request) {
        return agentService.askAgent(request);
    }
}

