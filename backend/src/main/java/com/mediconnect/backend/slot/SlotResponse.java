package com.mediconnect.backend.slot;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SlotResponse {
    private Long slotId;
    private LocalDateTime time;
    private Long doctorId;
    private boolean booked;
    private Long bookedUserId;
    private String liveKitData;
}
