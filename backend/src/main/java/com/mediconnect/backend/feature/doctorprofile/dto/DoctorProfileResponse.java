package com.mediconnect.backend.feature.doctorprofile.dto;

import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.specialization.dto.SpecializationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Doctor profile response payload")
public class DoctorProfileResponse {

    public DoctorProfileResponse() {
    }

    public DoctorProfileResponse(Long userId, String licenseNumber, String qualification,
                                 String bio, BigDecimal consultationFee, String verificationStatus,
                                 String timezone, List<SpecializationResponse> specializations) {
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.qualification = qualification;
        this.bio = bio;
        this.consultationFee = consultationFee;
        this.verificationStatus = verificationStatus;
        this.timezone = timezone;
        this.specializations = specializations != null ? specializations : new ArrayList<>();
    }

    @Schema(description = "User ID of the doctor", example = "1")
    private Long userId;

    @Schema(description = "Medical license number", example = "DOC-12345")
    private String licenseNumber;

    @Schema(description = "Medical qualifications and degrees", example = "MD, MBBS")
    private String qualification;

    @Schema(description = "Doctor biography or professional summary", example = "Experienced cardiologist with 10+ years practice.")
    private String bio;

    @Schema(description = "Consultation fee amount", example = "100.00")
    private BigDecimal consultationFee;

    @Schema(description = "Profile verification status", example = "PENDING")
    private String verificationStatus;

    @Schema(description = "Timezone identifier", example = "America/New_York")
    private String timezone;

    @Schema(description = "List of medical specializations assigned to the doctor")
    private List<SpecializationResponse> specializations = new ArrayList<>();

    public static DoctorProfileResponse fromEntity(DoctorProfile profile) {
        return fromEntity(profile, new ArrayList<>());
    }

    public static DoctorProfileResponse fromEntity(DoctorProfile profile, List<SpecializationResponse> specializations) {
        if (profile == null) {
            return null;
        }
        return new DoctorProfileResponse(
                profile.getUserId(),
                profile.getLicenseNumber(),
                profile.getQualification(),
                profile.getBio(),
                profile.getConsultationFee(),
                profile.getVerificationStatus(),
                profile.getTimezone(),
                specializations
        );
    }
}

