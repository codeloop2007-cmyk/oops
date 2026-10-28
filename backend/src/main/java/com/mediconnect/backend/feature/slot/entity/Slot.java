package com.mediconnect.backend.feature.slot.entity;

import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "slot")
@Getter
@Setter
public class Slot {

    public Slot() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_user_id", nullable = false)
    private DoctorProfile doctorProfile;

    @NotNull
    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant startAt;

    @NotNull
    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant endAt;

    @NotNull
    @Column(name = "is_bookable", nullable = false)
    private Boolean isBookable;

    @NotNull
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;
}

