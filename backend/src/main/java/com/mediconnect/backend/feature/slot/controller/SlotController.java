package com.mediconnect.backend.feature.slot.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.slot.dto.CreateSlotRequest;
import com.mediconnect.backend.feature.slot.dto.SlotResponse;
import com.mediconnect.backend.feature.slot.dto.UpdateSlotRequest;
import com.mediconnect.backend.feature.slot.service.SlotService;
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
@RequestMapping("/api/v1/slots")
@Tag(name = "Slot Management", description = "Endpoints for managing doctor appointment availability slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @Operation(summary = "Create appointment slot", description = "Creates a new availability slot for a doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Slot created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SlotResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.createSlot(request));
    }

    @Operation(summary = "Get slot by ID", description = "Retrieves an appointment slot by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Slot retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SlotResponse.class))),
            @ApiResponse(responseCode = "404", description = "Slot not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<SlotResponse> getSlotById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(slotService.getSlotById(id));
    }

    @Operation(summary = "List appointment slots", description = "Lists appointment slots with optional filters for doctor and bookable status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of slots retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SlotResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<SlotResponse>> getSlots(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "isBookable", required = false) Boolean isBookable) {
        return ResponseEntity.ok(slotService.getSlots(doctorId, isBookable));
    }

    @Operation(summary = "Update appointment slot", description = "Updates an appointment slot's timing or bookable status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Slot updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SlotResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Slot not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SlotResponse> updateSlot(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateSlotRequest request) {
        return ResponseEntity.ok(slotService.updateSlot(id, request));
    }

    @Operation(summary = "Delete appointment slot", description = "Deletes an appointment slot by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Slot deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Slot not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable("id") Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}

