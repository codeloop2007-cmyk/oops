package com.mediconnect.backend.slot;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "slots")
@Data
@NoArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    @Column(name = "slot_time", nullable = false)
    private LocalDateTime time;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "is_booked", nullable = false)
    private boolean booked = false;

    @Column(name = "booked_user_id")
    private Long bookedUserId;

    @Column(name = "live_kit_data", columnDefinition = "TEXT")
    private String liveKitData;
}
