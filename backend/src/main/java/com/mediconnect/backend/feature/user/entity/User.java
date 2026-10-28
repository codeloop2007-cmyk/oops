package com.mediconnect.backend.feature.user.entity;

import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.user.valueobject.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_user_client_identity_id", columnNames = "client_identity_id"),
                @UniqueConstraint(name = "uk_app_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_app_user_phone", columnNames = "phone")
        }
)
@Getter
@Setter
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "client_identity_id", nullable = false, unique = true, updatable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID clientIdentityId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private RegistrationStatus registrationStatus = RegistrationStatus.PENDING;

    @Column(name = "name")
    private String name;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Embedded
    private Address address;

    @NotNull
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant updatedAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private DoctorProfile doctorProfile;
}
