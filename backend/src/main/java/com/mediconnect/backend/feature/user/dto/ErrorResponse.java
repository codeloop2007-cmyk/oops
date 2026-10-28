package com.mediconnect.backend.feature.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API error response format")
public class ErrorResponse {

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String message, Instant timestamp) {
        this(status, error, message, null, timestamp);
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> validationErrors, Instant timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
        this.timestamp = timestamp;
    }

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP error phrase", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Validation failed for one or more fields")
    private String message;

    @Schema(description = "Field-level validation error details when applicable")
    private Map<String, String> validationErrors;

    @Schema(description = "Timestamp when error occurred", example = "2026-10-17T12:00:00Z")
    private Instant timestamp;
}

