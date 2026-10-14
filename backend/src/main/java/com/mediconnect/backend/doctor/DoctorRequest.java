package com.mediconnect.backend.doctor;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotBlank(message = "specialization is required")
    @Size(max = 100, message = "specialization must be at most 100 characters")
    private String specialization;

    @Size(max = 150, message = "qualification must be at most 150 characters")
    private String qualification;

    @Size(max = 100, message = "language must be at most 100 characters")
    private String language;

    @Size(max = 150, message = "location must be at most 150 characters")
    private String location;

    @NotNull(message = "consultationFee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "consultationFee must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "consultationFee must have at most 8 digits and 2 decimals")
    private BigDecimal consultationFee;
}