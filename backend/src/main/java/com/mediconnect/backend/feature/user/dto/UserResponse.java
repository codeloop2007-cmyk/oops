package com.mediconnect.backend.feature.user.dto;

import com.mediconnect.backend.feature.user.entity.RegistrationStatus;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.valueobject.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response payload representing the authenticated or synchronized user")
public class UserResponse {

    public UserResponse() {
    }

    public UserResponse(Long userId, UUID clientIdentityId, RegistrationStatus registrationStatus,
                        String name, String email, String phone, String preferredLanguage,
                        Instant createdAt, Instant updatedAt) {
        this(userId, clientIdentityId, registrationStatus, name, email, phone, preferredLanguage, null, createdAt, updatedAt);
    }

    public UserResponse(Long userId, UUID clientIdentityId, RegistrationStatus registrationStatus,
                        String name, String email, String phone, String preferredLanguage,
                        Address address, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.clientIdentityId = clientIdentityId;
        this.registrationStatus = registrationStatus;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.preferredLanguage = preferredLanguage;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Schema(description = "Internal unique database user identifier", example = "1")
    private Long userId;

    @Schema(description = "Client identity UUID from external client or identity provider", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID clientIdentityId;

    @Schema(description = "Current user registration status", example = "PENDING")
    private RegistrationStatus registrationStatus;

    @Schema(description = "Full name of the user", example = "Jane Doe")
    private String name;

    @Schema(description = "Email address of the user", example = "jane.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phone;

    @Schema(description = "Preferred application language", example = "en")
    private String preferredLanguage;

    @Schema(description = "User physical address details")
    private Address address;

    @Schema(description = "Timestamp when the user account was created", example = "2026-10-17T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when the user account was last updated", example = "2026-10-17T12:00:00Z")
    private Instant updatedAt;

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getUserId(),
                user.getClientIdentityId(),
                user.getRegistrationStatus(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getPreferredLanguage(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
