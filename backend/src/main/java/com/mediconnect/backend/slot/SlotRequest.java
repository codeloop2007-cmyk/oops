package com.mediconnect.backend.slot;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SlotRequest {

    @NotNull(message = "time is required")
    private LocalDateTime time;

    @NotNull(message = "doctorId is required")
    private Long doctorId;
}
