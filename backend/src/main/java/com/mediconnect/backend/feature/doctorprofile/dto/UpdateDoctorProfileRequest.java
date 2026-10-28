package com.mediconnect.backend.feature.doctorprofile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for updating a doctor profile")
public class UpdateDoctorProfileRequest {

    public UpdateDoctorProfileRequest() {
    }

    public UpdateDoctorProfileRequest(String licenseNumber, String qualification, String bio,
                                      BigDecimal consultationFee, String verificationStatus, String timezone) {
        this.licenseNumber = licenseNumber;
        this.qualification = qualification;
        this.bio = bio;
        this.consultationFee = consultationFee;
        this.verificationStatus = verificationStatus;
        this.timezone = timezone;
    }

    @Schema(description = "Medical license number", example = "DOC-12345")
    private String licenseNumber;

    @Schema(description = "Medical qualifications and degrees", example = "MD, FACC")
    private String qualification;

    @Schema(description = "Doctor biography or professional summary", example = "Specialist in interventional cardiology.")
    private String bio;

    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
    @Schema(description = "Consultation fee amount", example = "150.00")
    private BigDecimal consultationFee;

    @Schema(description = "Profile verification status", example = "VERIFIED")
    private String verificationStatus;

    @Schema(description = "Timezone identifier", example = "America/New_York")
    private String timezone;
}

