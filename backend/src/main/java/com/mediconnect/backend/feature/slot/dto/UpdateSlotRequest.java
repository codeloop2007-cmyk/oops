package com.mediconnect.backend.feature.slot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating an existing slot")
public class UpdateSlotRequest {

    public UpdateSlotRequest() {
    }

    public UpdateSlotRequest(Instant startAt, Instant endAt, Boolean isBookable) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.isBookable = isBookable;
    }

    @Schema(description = "Slot start timestamp (UTC)", example = "2026-10-20T09:00:00Z")
    private Instant startAt;

    @Schema(description = "Slot end timestamp (UTC)", example = "2026-10-20T09:30:00Z")
    private Instant endAt;

    @Schema(description = "Whether the slot is currently bookable", example = "false")
    private Boolean isBookable;
}

