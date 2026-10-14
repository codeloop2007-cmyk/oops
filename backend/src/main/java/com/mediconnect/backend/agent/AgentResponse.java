package com.mediconnect.backend.agent;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.mediconnect.backend.doctor.Doctor;

@Data
public class AgentResponse {
    private String summary;
    private List<Long> doctorIds = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
}