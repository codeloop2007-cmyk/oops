package com.mediconnect.backend.feature.doctornote.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.doctornote.dto.CreateDoctorNoteRequest;
import com.mediconnect.backend.feature.doctornote.dto.DoctorNoteResponse;
import com.mediconnect.backend.feature.doctornote.dto.UpdateDoctorNoteRequest;
import com.mediconnect.backend.feature.doctornote.service.DoctorNoteService;
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
@RequestMapping("/api/v1/doctor-notes")
@Tag(name = "Doctor Note Management", description = "Endpoints for creating and retrieving clinical doctor notes")
public class DoctorNoteController {

    private final DoctorNoteService doctorNoteService;

    public DoctorNoteController(DoctorNoteService doctorNoteService) {
        this.doctorNoteService = doctorNoteService;
    }

    @Operation(summary = "Create clinical doctor note", description = "Creates a clinical note for a patient by a doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doctor note created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorNoteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor, patient, or appointment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DoctorNoteResponse> createNote(@Valid @RequestBody CreateDoctorNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorNoteService.createNote(request));
    }

    @Operation(summary = "Get doctor note by ID", description = "Retrieves a clinical doctor note by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor note retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorNoteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor note not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorNoteResponse> getNoteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(doctorNoteService.getNoteById(id));
    }

    @Operation(summary = "List doctor notes", description = "Retrieves doctor notes with optional filters by doctor, patient, or appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of doctor notes retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = DoctorNoteResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<DoctorNoteResponse>> getNotes(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "appointmentId", required = false) Long appointmentId) {
        return ResponseEntity.ok(doctorNoteService.getNotes(doctorId, patientId, appointmentId));
    }

    @Operation(summary = "Update doctor note", description = "Updates content of an existing clinical doctor note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor note updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorNoteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor note not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorNoteResponse> updateNote(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateDoctorNoteRequest request) {
        return ResponseEntity.ok(doctorNoteService.updateNote(id, request));
    }

    @Operation(summary = "Delete doctor note", description = "Deletes a clinical doctor note by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Doctor note deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor note not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable("id") Long id) {
        doctorNoteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}

