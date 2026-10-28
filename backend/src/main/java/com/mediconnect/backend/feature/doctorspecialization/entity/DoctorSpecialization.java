package com.mediconnect.backend.feature.doctorspecialization.entity;

import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.specialization.entity.Specialization;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctor_specialization")
@Getter
@Setter
public class DoctorSpecialization {

    public DoctorSpecialization() {
    }

    @EmbeddedId
    private DoctorSpecializationId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("doctorUserId")
    @JoinColumn(name = "doctor_user_id", nullable = false)
    private DoctorProfile doctorProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("specializationId")
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
}

