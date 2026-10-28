package com.mediconnect.backend.feature.prescription.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.prescription.dto.CreatePrescriptionRequest;
import com.mediconnect.backend.feature.prescription.dto.PrescriptionResponse;
import com.mediconnect.backend.feature.prescription.dto.UpdatePrescriptionRequest;
import com.mediconnect.backend.feature.prescription.service.PrescriptionService;
import com.mediconnect.backend.feature.prescriptionitem.dto.CreatePrescriptionItemRequest;
import com.mediconnect.backend.feature.prescriptionitem.dto.PrescriptionItemResponse;
import com.mediconnect.backend.feature.prescriptionitem.dto.UpdatePrescriptionItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prescriptions")
@Tag(name = "Prescription Management", description = "Endpoints for issuing, viewing, and managing patient prescriptions and medication items")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Create prescription", description = "Creates a new medical prescription, optionally with medication items")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prescription created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrescriptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Patient, doctor, or appointment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.createPrescription(request));
    }

    @Operation(summary = "Get prescription by ID", description = "Retrieves a prescription with its medication items")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prescription retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrescriptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prescription not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @Operation(summary = "List prescriptions", description = "Retrieves prescriptions with optional filters by patient, doctor, appointment, or status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of prescriptions retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PrescriptionResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptions(
            @RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "appointmentId", required = false) Long appointmentId,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(prescriptionService.getPrescriptions(patientId, doctorId, appointmentId, status));
    }

    @Operation(summary = "Update prescription", description = "Updates prescription status or notes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prescription updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrescriptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prescription not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> updatePrescription(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdatePrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, request));
    }

    @Operation(summary = "Delete prescription", description = "Deletes a prescription and all its items")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prescription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Prescription not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable("id") Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add item to prescription", description = "Adds a medication item to an existing prescription")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prescription item added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrescriptionItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prescription not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/items")
    public ResponseEntity<PrescriptionItemResponse> addItem(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreatePrescriptionItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.addItemToPrescription(id, request));
    }

    @Operation(summary = "Update prescription item", description = "Updates a medication item within a prescription")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prescription item updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrescriptionItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prescription or item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<PrescriptionItemResponse> updateItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody UpdatePrescriptionItemRequest request) {
        return ResponseEntity.ok(prescriptionService.updatePrescriptionItem(id, itemId, request));
    }

    @Operation(summary = "Delete prescription item", description = "Deletes a medication item from a prescription")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prescription item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Prescription or item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId) {
        prescriptionService.deletePrescriptionItem(id, itemId);
        return ResponseEntity.noContent().build();
    }
}

