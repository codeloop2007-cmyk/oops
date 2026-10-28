package com.mediconnect.backend.feature.doctorspecialization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class DoctorSpecializationId implements Serializable {

    public DoctorSpecializationId() {
    }

    public DoctorSpecializationId(Long doctorUserId, Long specializationId) {
        this.doctorUserId = doctorUserId;
        this.specializationId = specializationId;
    }

    @Column(name = "doctor_user_id")
    private Long doctorUserId;

    @Column(name = "specialization_id")
    private Long specializationId;
}
