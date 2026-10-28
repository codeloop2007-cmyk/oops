package com.mediconnect.backend.feature.doctorsearch.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorSearchResponse {

    public DoctorSearchResponse() {
    }

    public DoctorSearchResponse(Long doctorId, String name, String specialization, String qualification,
                                String language, String location, BigDecimal consultationFee) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.language = language;
        this.location = location;
        this.consultationFee = consultationFee;
    }

    private Long doctorId;
    private String name;
    private String specialization;
    private String qualification;
    private String language;
    private String location;
    private BigDecimal consultationFee;
}