package com.mediconnect.backend.feature.doctorprofile.controller;

import com.mediconnect.backend.feature.doctorprofile.dto.CreateDoctorProfileRequest;
import com.mediconnect.backend.feature.doctorprofile.dto.DoctorProfileResponse;
import com.mediconnect.backend.feature.doctorprofile.dto.UpdateDoctorProfileRequest;
import com.mediconnect.backend.feature.doctorprofile.service.DoctorProfileService;
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
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctor Profile Management", description = "Endpoints for doctor profile CRUD operations")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @Operation(summary = "Create doctor profile", description = "Creates a doctor profile for an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doctor profile created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error on request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Doctor profile or license number already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DoctorProfileResponse> createDoctorProfile(@Valid @RequestBody CreateDoctorProfileRequest request) {
        DoctorProfileResponse response = doctorProfileService.createDoctorProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get doctor profile by ID", description = "Retrieves doctor profile and assigned specializations by doctor user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor profile retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor profile not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfileById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(doctorProfileService.getDoctorProfile(id));
    }

    @Operation(summary = "List all doctor profiles", description = "Retrieves all doctor profiles with their specializations")
    @ApiResponse(responseCode = "200", description = "List of doctor profiles retrieved successfully")
    @GetMapping
    public ResponseEntity<List<DoctorProfileResponse>> getAllDoctorProfiles() {
        return ResponseEntity.ok(doctorProfileService.getAllDoctorProfiles());
    }

    @Operation(summary = "Update doctor profile", description = "Updates fields of an existing doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor profile updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor profile not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "License number conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorProfileResponse> updateDoctorProfile(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateDoctorProfileRequest request) {
        return ResponseEntity.ok(doctorProfileService.updateDoctorProfile(id, request));
    }

    @Operation(summary = "Delete doctor profile", description = "Deletes a doctor profile by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Doctor profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor profile not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorProfile(@PathVariable("id") Long id) {
        doctorProfileService.deleteDoctorProfile(id);
        return ResponseEntity.noContent().build();
    }
}

