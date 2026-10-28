package com.mediconnect.backend.feature.user.dto;

import com.mediconnect.backend.feature.user.valueobject.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for completing user profile registration")
public class RegisterUserRequest {

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String name, String email, String phone, String preferredLanguage) {
        this(name, email, phone, preferredLanguage, null);
    }

    public RegisterUserRequest(String name, String email, String phone, String preferredLanguage, Address address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.preferredLanguage = preferredLanguage;
        this.address = address;
    }

    @NotBlank(message = "Name is required and must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "Full name of the user", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email is required and must not be blank")
    @Email(message = "Email must be a valid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Schema(description = "Email address of the user", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Phone number is required and must not be blank")
    @Size(min = 7, max = 20, message = "Phone number must be between 7 and 20 characters")
    @Schema(description = "Contact phone number of the user", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Size(max = 20, message = "Preferred language code must not exceed 20 characters")
    @Schema(description = "Preferred application language code (e.g. en, es, fr)", example = "en", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String preferredLanguage;

    @Valid
    @NotNull(message = "Address is required")
    @Schema(description = "User physical address details", requiredMode = Schema.RequiredMode.REQUIRED)
    private Address address;
}
