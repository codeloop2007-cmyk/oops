package com.mediconnect.backend.slot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookSlotRequest {

    @NotNull(message = "bookedUserId is required")
    private Long bookedUserId;

    @NotBlank(message = "liveKitData is required")
    private String liveKitData;
}
