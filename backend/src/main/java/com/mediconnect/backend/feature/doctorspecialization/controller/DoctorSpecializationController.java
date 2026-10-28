package com.mediconnect.backend.feature.doctorspecialization.controller;

import com.mediconnect.backend.feature.doctorspecialization.service.DoctorSpecializationService;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/specializations")
@Tag(name = "Doctor Specialization Assignment", description = "Endpoints for managing doctor specializations")
public class DoctorSpecializationController {

    private final DoctorSpecializationService doctorSpecializationService;

    public DoctorSpecializationController(DoctorSpecializationService doctorSpecializationService) {
        this.doctorSpecializationService = doctorSpecializationService;
    }

    @Operation(summary = "Add specialization to doctor", description = "Associates a medical specialization with a doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Specialization assigned successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SpecializationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor profile or specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Specialization already assigned to doctor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{specializationId}")
    public ResponseEntity<SpecializationResponse> addSpecialization(
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("specializationId") Long specializationId) {
        SpecializationResponse response = doctorSpecializationService.addSpecializationToDoctor(doctorId, specializationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Remove specialization from doctor", description = "Removes a medical specialization association from a doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Specialization removed successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor profile, specialization, or assignment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{specializationId}")
    public ResponseEntity<Void> removeSpecialization(
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("specializationId") Long specializationId) {
        doctorSpecializationService.removeSpecializationFromDoctor(doctorId, specializationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get specializations of doctor", description = "Retrieves all medical specializations assigned to a doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of specializations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor profile not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<SpecializationResponse>> getDoctorSpecializations(@PathVariable("doctorId") Long doctorId) {
        return ResponseEntity.ok(doctorSpecializationService.getSpecializationsForDoctor(doctorId));
    }
}

