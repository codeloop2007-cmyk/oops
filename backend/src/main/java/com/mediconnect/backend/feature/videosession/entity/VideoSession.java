package com.mediconnect.backend.feature.videosession.entity;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video_session")
@Getter
@Setter
public class VideoSession {

    public VideoSession() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_session_id")
    private Long videoSessionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @NotBlank
    @Column(name = "provider", nullable = false)
    private String provider;

    @NotBlank
    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    @NotBlank
    @Column(name = "join_code_hash", nullable = false)
    private String joinCodeHash;

    @NotBlank
    @Column(name = "session_status", nullable = false)
    private String sessionStatus;

    @Column(name = "started_at", columnDefinition = "TIMESTAMPTZ")
    private Instant startedAt;

    @Column(name = "ended_at", columnDefinition = "TIMESTAMPTZ")
    private Instant endedAt;
}

