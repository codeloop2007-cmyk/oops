package com.mediconnect.backend.feature.slot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for creating a doctor appointment availability slot")
public class CreateSlotRequest {

    public CreateSlotRequest() {
    }

    public CreateSlotRequest(Long doctorUserId, Instant startAt, Instant endAt, Boolean isBookable) {
        this.doctorUserId = doctorUserId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isBookable = isBookable;
    }

    @NotNull(message = "Doctor user ID is required")
    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @NotNull(message = "Slot start time is required")
    @Schema(description = "Slot start timestamp (UTC)", example = "2026-10-20T09:00:00Z")
    private Instant startAt;

    @NotNull(message = "Slot end time is required")
    @Schema(description = "Slot end timestamp (UTC)", example = "2026-10-20T09:30:00Z")
    private Instant endAt;

    @Schema(description = "Whether the slot is currently bookable", example = "true", defaultValue = "true")
    private Boolean isBookable = true;
}

