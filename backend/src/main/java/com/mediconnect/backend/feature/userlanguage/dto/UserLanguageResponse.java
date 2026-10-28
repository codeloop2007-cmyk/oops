package com.mediconnect.backend.feature.userlanguage.dto;

import com.mediconnect.backend.feature.userlanguage.entity.UserLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representing a spoken language configured for a user")
public class UserLanguageResponse {

    public UserLanguageResponse() {
    }

    public UserLanguageResponse(Long userId, String languageCode) {
        this.userId = userId;
        this.languageCode = languageCode;
    }

    @Schema(description = "User identifier", example = "1")
    private Long userId;

    @Schema(description = "Language code (e.g. en, es, fr)", example = "es")
    private String languageCode;

    public static UserLanguageResponse fromEntity(UserLanguage userLanguage) {
        if (userLanguage == null || userLanguage.getId() == null) {
            return null;
        }
        return new UserLanguageResponse(
                userLanguage.getId().getUserId(),
                userLanguage.getId().getLanguageCode()
        );
    }
}

