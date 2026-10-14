package com.mediconnect.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "userId is required")
    @Size(min = 3, max = 30, message = "userId must be between 3 and 30 characters")
    private String userId;

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 60, message = "name must be between 2 and 60 characters")
    private String name;

    @NotBlank(message = "gmail is required")
    @Email(message = "gmail must be a valid email address")
    private String gmail;
}