package com.mediconnect.backend.agent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@Tag(name = "AI Agent", description = "Medical assistant agent — forwards queries to the Python AI service")
public class AiAgentController {

    private final AiAgentClient client;

    public AiAgentController(AiAgentClient client) {
        this.client = client;
    }

    @PostMapping("/ask")
    @Operation(
            summary = "Ask the medical assistant",
            description = """
                    Sends a natural-language query to the Gemini ReAct agent running
                    on the Python AI service. The agent decides which tools to call
                    (e.g. `find_doctor`) and returns a structured response with:
                    - `summary` — short recommendation
                    - `doctorIds` — list of matched doctor IDs
                    - `doctors` — full details for each match
                    """
    )
    public AgentResponse ask(@Valid @RequestBody AgentRequest req) {
        return client.ask(req.getQuery());
    }
}