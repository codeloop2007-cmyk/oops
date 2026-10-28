package com.mediconnect.backend.feature.user.dto;

import com.mediconnect.backend.feature.user.valueobject.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing user's profile details")
public class UpdateUserRequest {

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String name, String preferredLanguage, Address address) {
        this.name = name;
        this.preferredLanguage = preferredLanguage;
        this.address = address;
    }

    @Schema(description = "Full name of the user", example = "Jane Doe")
    private String name;

    @Schema(description = "Preferred application language code", example = "en")
    private String preferredLanguage;

    @Valid
    @Schema(description = "Physical address details of the user")
    private Address address;
}

