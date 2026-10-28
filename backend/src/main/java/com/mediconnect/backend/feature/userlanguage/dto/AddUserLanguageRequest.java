package com.mediconnect.backend.feature.userlanguage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to associate a spoken language with a user")
public class AddUserLanguageRequest {

    public AddUserLanguageRequest() {
    }

    public AddUserLanguageRequest(String languageCode) {
        this.languageCode = languageCode;
    }

    @NotBlank(message = "Language code is required")
    @Schema(description = "Language code (e.g. ISO 639-1 code)", example = "es")
    private String languageCode;
}

