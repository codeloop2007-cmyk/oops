package com.mediconnect.backend.feature.videosession.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.videosession.dto.CreateVideoSessionRequest;
import com.mediconnect.backend.feature.videosession.dto.UpdateVideoSessionRequest;
import com.mediconnect.backend.feature.videosession.dto.VideoSessionResponse;
import com.mediconnect.backend.feature.videosession.service.VideoSessionService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/video-sessions")
@Tag(name = "Video Session Management", description = "Endpoints for managing telehealth video consultation sessions")
public class VideoSessionController {

    private final VideoSessionService videoSessionService;

    public VideoSessionController(VideoSessionService videoSessionService) {
        this.videoSessionService = videoSessionService;
    }

    @Operation(summary = "Create video session", description = "Initializes a video consultation session for an appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Video session created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VideoSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Video session already exists for this appointment or room ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<VideoSessionResponse> createVideoSession(@Valid @RequestBody CreateVideoSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoSessionService.createVideoSession(request));
    }

    @Operation(summary = "Get video session by ID", description = "Retrieves a video session by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video session retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VideoSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Video session not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VideoSessionResponse> getVideoSessionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(videoSessionService.getVideoSessionById(id));
    }

    @Operation(summary = "Get video session by appointment ID", description = "Retrieves the video session linked to a specific appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video session retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VideoSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Video session not found for appointment",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<VideoSessionResponse> getVideoSessionByAppointmentId(@PathVariable("appointmentId") Long appointmentId) {
        return ResponseEntity.ok(videoSessionService.getVideoSessionByAppointmentId(appointmentId));
    }

    @Operation(summary = "List all video sessions", description = "Retrieves all video consultation sessions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of video sessions retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VideoSessionResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<VideoSessionResponse>> getAllVideoSessions() {
        return ResponseEntity.ok(videoSessionService.getAllVideoSessions());
    }

    @Operation(summary = "Update video session", description = "Updates video session status or timestamps")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video session updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VideoSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Video session not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<VideoSessionResponse> updateVideoSession(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateVideoSessionRequest request) {
        return ResponseEntity.ok(videoSessionService.updateVideoSession(id, request));
    }

    @Operation(summary = "Delete video session", description = "Deletes a video session by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Video session deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Video session not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoSession(@PathVariable("id") Long id) {
        videoSessionService.deleteVideoSession(id);
        return ResponseEntity.noContent().build();
    }
}

