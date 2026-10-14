package com.mediconnect.backend.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DoctorResponse {
    private Long doctorId;
    private String name;
    private String specialization;
    private String qualification;
    private String language;
    private String location;
    private BigDecimal consultationFee;
}