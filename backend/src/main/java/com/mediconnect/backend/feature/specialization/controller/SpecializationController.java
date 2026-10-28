package com.mediconnect.backend.feature.specialization.controller;

import com.mediconnect.backend.feature.specialization.dto.CreateSpecializationRequest;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import com.mediconnect.backend.feature.specialization.service.SpecializationService;
import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/specializations")
@Tag(name = "Specialization Management", description = "Endpoints for managing medical specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @Operation(summary = "Create a medical specialization", description = "Creates a new unique medical specialization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Specialization created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SpecializationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed on request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Specialization with this name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SpecializationResponse> createSpecialization(@Valid @RequestBody CreateSpecializationRequest request) {
        SpecializationResponse response = specializationService.createSpecialization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all specializations", description = "Retrieves the list of all available medical specializations")
    @ApiResponse(responseCode = "200", description = "List of specializations retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SpecializationResponse>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @Operation(summary = "Get specialization by ID", description = "Retrieves a medical specialization by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialization retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponse> getSpecializationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(specializationService.getSpecializationById(id));
    }

    @Operation(summary = "Update specialization by ID", description = "Updates a medical specialization name by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialization updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed on request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Specialization with this name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateSpecializationRequest request) {
        return ResponseEntity.ok(specializationService.updateSpecialization(id, request));
    }

    @Operation(summary = "Delete specialization by ID", description = "Deletes a medical specialization by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Specialization deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable("id") Long id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }
}

