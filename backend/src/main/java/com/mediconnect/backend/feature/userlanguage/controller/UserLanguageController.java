package com.mediconnect.backend.feature.userlanguage.controller;

import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import com.mediconnect.backend.feature.userlanguage.dto.AddUserLanguageRequest;
import com.mediconnect.backend.feature.userlanguage.dto.UserLanguageResponse;
import com.mediconnect.backend.feature.userlanguage.service.UserLanguageService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/languages")
@Tag(name = "User Languages", description = "Endpoints for managing spoken languages for users (including doctors)")
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    public UserLanguageController(UserLanguageService userLanguageService) {
        this.userLanguageService = userLanguageService;
    }

    @Operation(summary = "Add spoken language to user", description = "Associates a spoken language with a user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Language added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserLanguageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Language already added for this user",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserLanguageResponse> addLanguage(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody AddUserLanguageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userLanguageService.addLanguageToUser(userId, request));
    }

    @Operation(summary = "Get languages for user", description = "Retrieves all spoken languages associated with a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of languages retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserLanguageResponse.class)))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserLanguageResponse>> getLanguages(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userLanguageService.getUserLanguages(userId));
    }

    @Operation(summary = "Remove spoken language from user", description = "Disassociates a spoken language from a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Language removed successfully"),
            @ApiResponse(responseCode = "404", description = "User or language association not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{languageCode}")
    public ResponseEntity<Void> removeLanguage(
            @PathVariable("userId") Long userId,
            @PathVariable("languageCode") String languageCode) {
        userLanguageService.removeLanguageFromUser(userId, languageCode);
        return ResponseEntity.noContent().build();
    }
}

