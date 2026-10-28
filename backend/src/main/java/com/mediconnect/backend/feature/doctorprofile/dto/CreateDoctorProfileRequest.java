package com.mediconnect.backend.feature.doctorprofile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for creating a doctor profile")
public class CreateDoctorProfileRequest {

    public CreateDoctorProfileRequest() {
    }

    public CreateDoctorProfileRequest(Long userId, String licenseNumber, String qualification,
                                      String bio, BigDecimal consultationFee, String verificationStatus,
                                      String timezone) {
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.qualification = qualification;
        this.bio = bio;
        this.consultationFee = consultationFee;
        this.verificationStatus = verificationStatus;
        this.timezone = timezone;
    }

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID of the doctor user", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "License number must not be blank")
    @Schema(description = "Medical license number", example = "DOC-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String licenseNumber;

    @NotBlank(message = "Qualification must not be blank")
    @Schema(description = "Medical qualifications and degrees", example = "MD, MBBS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String qualification;

    @Schema(description = "Doctor biography or professional summary", example = "Experienced cardiologist with 10+ years practice.")
    private String bio;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
    @Schema(description = "Consultation fee amount", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal consultationFee;

    @Schema(description = "Profile verification status", example = "PENDING")
    private String verificationStatus;

    @NotBlank(message = "Timezone must not be blank")
    @Schema(description = "Timezone identifier", example = "America/New_York", requiredMode = Schema.RequiredMode.REQUIRED)
    private String timezone;
}

