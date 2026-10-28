package com.mediconnect.backend.feature.appointment.entity;

import com.mediconnect.backend.feature.review.entity.Review;
import com.mediconnect.backend.feature.slot.entity.Slot;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.videosession.entity.VideoSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appointment")
@Getter
@Setter
public class Appointment {

    public Appointment() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_user_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @NotBlank
    @Column(name = "status", nullable = false)
    private String status;

    @NotBlank
    @Column(name = "consultation_mode", nullable = false)
    private String consultationMode;

    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "booked_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant bookedAt;

    @Column(name = "cancelled_at", columnDefinition = "TIMESTAMPTZ")
    private Instant cancelledAt;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private VideoSession videoSession;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Review review;
}

