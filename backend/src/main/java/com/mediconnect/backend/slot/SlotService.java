package com.mediconnect.backend.slot;

import com.mediconnect.backend.doctor.DoctorRepository;
import com.mediconnect.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotService {

    private final SlotRepository slotRepo;
    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;

    public SlotService(SlotRepository slotRepo, DoctorRepository doctorRepo, UserRepository userRepo) {
        this.slotRepo = slotRepo;
        this.doctorRepo = doctorRepo;
        this.userRepo = userRepo;
    }

    public List<SlotResponse> getAll() {
        return slotRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SlotResponse getById(Long id) {
        return toResponse(findSlot(id));
    }

    public List<SlotResponse> getByDoctorId(Long doctorId) {
        ensureDoctorExists(doctorId);
        return slotRepo.findByDoctorId(doctorId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SlotResponse> getAvailableByDoctorId(Long doctorId) {
        ensureDoctorExists(doctorId);
        return slotRepo.findByDoctorIdAndBookedFalse(doctorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SlotResponse create(SlotRequest req) {
        ensureDoctorExists(req.getDoctorId());
        Slot slot = new Slot();
        slot.setTime(req.getTime());
        slot.setDoctorId(req.getDoctorId());
        slot.setBooked(false);
        slot.setBookedUserId(null);
        slot.setLiveKitData(null);
        return toResponse(slotRepo.save(slot));
    }

    public SlotResponse update(Long id, SlotRequest req) {
        Slot slot = findSlot(id);
        if (slot.isBooked()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update a booked slot");
        }
        ensureDoctorExists(req.getDoctorId());
        slot.setTime(req.getTime());
        slot.setDoctorId(req.getDoctorId());
        return toResponse(slotRepo.save(slot));
    }

    public void delete(Long id) {
        Slot slot = findSlot(id);
        if (slot.isBooked()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete a booked slot");
        }
        slotRepo.deleteById(id);
    }

    public SlotResponse book(Long id, BookSlotRequest req) {
        Slot slot = findSlot(id);
        if (slot.isBooked()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot already booked");
        }
        if (!userRepo.existsById(req.getBookedUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + req.getBookedUserId());
        }
        slot.setBooked(true);
        slot.setBookedUserId(req.getBookedUserId());
        slot.setLiveKitData(req.getLiveKitData());
        return toResponse(slotRepo.save(slot));
    }

    private Slot findSlot(Long id) {
        return slotRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found: " + id));
    }

    private void ensureDoctorExists(Long doctorId) {
        if (!doctorRepo.existsById(doctorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found: " + doctorId);
        }
    }

    private SlotResponse toResponse(Slot s) {
        return new SlotResponse(
                s.getSlotId(),
                s.getTime(),
                s.getDoctorId(),
                s.isBooked(),
                s.getBookedUserId(),
                s.getLiveKitData());
    }
}
