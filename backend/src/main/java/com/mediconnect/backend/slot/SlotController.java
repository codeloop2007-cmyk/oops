package com.mediconnect.backend.slot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@Tag(name = "Slot", description = "Doctor availability slots and booking")
public class SlotController {

    private final SlotService service;

    public SlotController(SlotService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all slots")
    public List<SlotResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a slot by id")
    public SlotResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get all slots for a doctor")
    public List<SlotResponse> getByDoctorId(@PathVariable Long doctorId) {
        return service.getByDoctorId(doctorId);
    }

    @GetMapping("/doctor/{doctorId}/available")
    @Operation(summary = "Get unbooked slots for a doctor")
    public List<SlotResponse> getAvailableByDoctorId(@PathVariable Long doctorId) {
        return service.getAvailableByDoctorId(doctorId);
    }

    @PostMapping
    @Operation(summary = "Create an available slot for a doctor")
    public ResponseEntity<SlotResponse> create(@Valid @RequestBody SlotRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an unbooked slot")
    public SlotResponse update(@PathVariable Long id, @Valid @RequestBody SlotRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an unbooked slot")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/book")
    @Operation(summary = "Book a slot with user id and LiveKit data")
    public SlotResponse book(@PathVariable Long id, @Valid @RequestBody BookSlotRequest req) {
        return service.book(id, req);
    }
}
