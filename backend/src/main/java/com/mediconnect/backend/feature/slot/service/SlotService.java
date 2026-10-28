package com.mediconnect.backend.feature.slot.service;

import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.slot.dto.CreateSlotRequest;
import com.mediconnect.backend.feature.slot.dto.SlotResponse;
import com.mediconnect.backend.feature.slot.dto.UpdateSlotRequest;
import com.mediconnect.backend.feature.slot.entity.Slot;
import com.mediconnect.backend.feature.slot.exception.SlotNotFoundException;
import com.mediconnect.backend.feature.slot.repository.SlotRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public SlotService(SlotRepository slotRepository, DoctorProfileRepository doctorProfileRepository) {
        this.slotRepository = slotRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Transactional
    public SlotResponse createSlot(CreateSlotRequest request) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(request.getDoctorUserId())
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + request.getDoctorUserId()));

        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException("Slot end time must be after start time");
        }

        Slot slot = new Slot();
        slot.setDoctorProfile(doctorProfile);
        slot.setStartAt(request.getStartAt());
        slot.setEndAt(request.getEndAt());
        slot.setIsBookable(request.getIsBookable() != null ? request.getIsBookable() : true);
        slot.setCreatedAt(Instant.now());

        Slot saved = slotRepository.save(slot);
        return SlotResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlotById(Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId));
        return SlotResponse.fromEntity(slot);
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getSlots(Long doctorUserId, Boolean isBookable) {
        List<Slot> slots;
        if (doctorUserId != null && isBookable != null) {
            slots = slotRepository.findByDoctorProfile_UserIdAndIsBookable(doctorUserId, isBookable);
        } else if (doctorUserId != null) {
            slots = slotRepository.findByDoctorProfile_UserId(doctorUserId);
        } else if (isBookable != null) {
            slots = slotRepository.findByIsBookable(isBookable);
        } else {
            slots = slotRepository.findAll();
        }
        return slots.stream().map(SlotResponse::fromEntity).toList();
    }

    @Transactional
    public SlotResponse updateSlot(Long slotId, UpdateSlotRequest request) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId));

        if (request.getStartAt() != null) {
            slot.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            slot.setEndAt(request.getEndAt());
        }
        if (request.getIsBookable() != null) {
            slot.setIsBookable(request.getIsBookable());
        }

        if (slot.getEndAt().isBefore(slot.getStartAt())) {
            throw new IllegalArgumentException("Slot end time must be after start time");
        }

        Slot updated = slotRepository.save(slot);
        return SlotResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteSlot(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new SlotNotFoundException("Slot not found with id: " + slotId);
        }
        slotRepository.deleteById(slotId);
    }
}

