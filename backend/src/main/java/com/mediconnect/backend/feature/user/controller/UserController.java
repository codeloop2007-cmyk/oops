package com.mediconnect.backend.feature.user.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.user.dto.RegisterUserRequest;
import com.mediconnect.backend.feature.user.dto.UserResponse;
import com.mediconnect.backend.feature.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User & Authentication", description = "Endpoints for user identity synchronization and profile registration")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Synchronize or fetch current user",
            description = "Finds existing user by client identity UUID passed in X-Client-User-Id header. "
                    + "If no user exists, creates a new record in PENDING status. Concurrency-safe against duplicate creations."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User synchronized or retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing or malformed X-Client-User-Id header",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @Parameter(
                    name = "X-Client-User-Id",
                    description = "Client identity UUID from external provider/client",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string", format = "uuid", example = "123e4567-e89b-12d3-a456-426614174000")
            )
            @RequestHeader("X-Client-User-Id") UUID clientIdentityId
    ) {
        UserResponse response = userService.syncUser(clientIdentityId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register user profile",
            description = "Validates and updates profile fields for a pending user, marking their registration status as COMPLETED. "
                    + "Disallows repeated registration requests and duplicate email/phone numbers."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile successfully registered and marked COMPLETED",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure on request body or missing/malformed X-Client-User-Id header",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found for provided client identity; synchronization via /me is required first",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: User is already registered or email/phone is already in use",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Parameter(
                    name = "X-Client-User-Id",
                    description = "Client identity UUID from external provider/client",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string", format = "uuid", example = "123e4567-e89b-12d3-a456-426614174000")
            )
            @RequestHeader("X-Client-User-Id") UUID clientIdentityId,
            @Valid @RequestBody RegisterUserRequest request
    ) {
        UserResponse response = userService.registerUser(clientIdentityId, request);
        return ResponseEntity.ok(response);
    }
}

