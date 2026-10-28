package com.mediconnect.backend.feature.doctorembedding.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.doctorembedding.dto.DoctorEmbeddingResponse;
import com.mediconnect.backend.feature.doctorembedding.dto.UpsertDoctorEmbeddingRequest;
import com.mediconnect.backend.feature.doctorembedding.service.DoctorEmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/embedding")
@Tag(name = "Doctor Vector Embedding", description = "Endpoints for AI vector embeddings associated with doctor profiles")
public class DoctorEmbeddingController {

    private final DoctorEmbeddingService doctorEmbeddingService;

    public DoctorEmbeddingController(DoctorEmbeddingService doctorEmbeddingService) {
        this.doctorEmbeddingService = doctorEmbeddingService;
    }

    @Operation(summary = "Upsert doctor embedding", description = "Creates or updates semantic vector embedding representation for a doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor embedding saved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorEmbeddingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor profile not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<DoctorEmbeddingResponse> upsertEmbedding(
            @PathVariable("doctorId") Long doctorId,
            @Valid @RequestBody UpsertDoctorEmbeddingRequest request) {
        return ResponseEntity.ok(doctorEmbeddingService.upsertEmbedding(doctorId, request));
    }

    @Operation(summary = "Get doctor embedding", description = "Retrieves vector embedding for a doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor embedding retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DoctorEmbeddingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor profile or embedding not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<DoctorEmbeddingResponse> getEmbedding(@PathVariable("doctorId") Long doctorId) {
        return ResponseEntity.ok(doctorEmbeddingService.getEmbeddingByDoctorId(doctorId));
    }

    @Operation(summary = "Delete doctor embedding", description = "Deletes vector embedding for a doctor profile")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Doctor embedding deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor profile or embedding not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteEmbedding(@PathVariable("doctorId") Long doctorId) {
        doctorEmbeddingService.deleteEmbedding(doctorId);
        return ResponseEntity.noContent().build();
    }
}

