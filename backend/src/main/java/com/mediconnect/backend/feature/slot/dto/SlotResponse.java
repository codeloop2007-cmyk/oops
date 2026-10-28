package com.mediconnect.backend.feature.slot.dto;

import com.mediconnect.backend.feature.slot.entity.Slot;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response representation of a doctor appointment slot")
public class SlotResponse {

    public SlotResponse() {
    }

    public SlotResponse(Long slotId, Long doctorUserId, Instant startAt, Instant endAt, Boolean isBookable, Instant createdAt) {
        this.slotId = slotId;
        this.doctorUserId = doctorUserId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isBookable = isBookable;
        this.createdAt = createdAt;
    }

    @Schema(description = "Slot unique identifier", example = "1")
    private Long slotId;

    @Schema(description = "Doctor user identifier", example = "1")
    private Long doctorUserId;

    @Schema(description = "Slot start timestamp (UTC)", example = "2026-10-20T09:00:00Z")
    private Instant startAt;

    @Schema(description = "Slot end timestamp (UTC)", example = "2026-10-20T09:30:00Z")
    private Instant endAt;

    @Schema(description = "Whether slot is bookable", example = "true")
    private Boolean isBookable;

    @Schema(description = "Slot creation timestamp", example = "2026-10-19T10:00:00Z")
    private Instant createdAt;

    public static SlotResponse fromEntity(Slot slot) {
        if (slot == null) {
            return null;
        }
        return new SlotResponse(
                slot.getSlotId(),
                slot.getDoctorProfile() != null ? slot.getDoctorProfile().getUserId() : null,
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getIsBookable(),
                slot.getCreatedAt()
        );
    }
}

