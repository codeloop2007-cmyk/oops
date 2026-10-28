package com.mediconnect.backend.feature.doctorsearch.controller;

import com.mediconnect.backend.feature.doctorsearch.dto.DoctorSearchResponse;
import com.mediconnect.backend.feature.doctorsearch.service.DoctorSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/api/v1/doctors", "/api/doctors"})
public class DoctorSearchController {

    private final DoctorSearchService doctorSearchService;

    public DoctorSearchController(DoctorSearchService doctorSearchService) {
        this.doctorSearchService = doctorSearchService;
    }

    @GetMapping("/search")
    public List<DoctorSearchResponse> searchDoctors(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit,
            @RequestParam(defaultValue = "RELEVANCE") String sort,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) BigDecimal minFee,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String language) {
        return doctorSearchService.searchDoctors(q, limit, sort, specialization, minFee, maxFee, city, language);
    }
}