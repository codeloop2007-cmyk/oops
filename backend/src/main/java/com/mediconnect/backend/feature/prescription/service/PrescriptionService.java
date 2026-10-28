package com.mediconnect.backend.feature.prescription.service;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException;
import com.mediconnect.backend.feature.appointment.repository.AppointmentRepository;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.prescription.dto.CreatePrescriptionRequest;
import com.mediconnect.backend.feature.prescription.dto.PrescriptionResponse;
import com.mediconnect.backend.feature.prescription.dto.UpdatePrescriptionRequest;
import com.mediconnect.backend.feature.prescription.entity.Prescription;
import com.mediconnect.backend.feature.prescription.exception.PrescriptionNotFoundException;
import com.mediconnect.backend.feature.prescription.repository.PrescriptionRepository;
import com.mediconnect.backend.feature.prescriptionitem.dto.CreatePrescriptionItemRequest;
import com.mediconnect.backend.feature.prescriptionitem.dto.PrescriptionItemResponse;
import com.mediconnect.backend.feature.prescriptionitem.dto.UpdatePrescriptionItemRequest;
import com.mediconnect.backend.feature.prescriptionitem.entity.PrescriptionItem;
import com.mediconnect.backend.feature.prescriptionitem.exception.PrescriptionItemNotFoundException;
import com.mediconnect.backend.feature.prescriptionitem.repository.PrescriptionItemRepository;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            UserRepository userRepository,
            DoctorProfileRepository doctorProfileRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
        User patient = userRepository.findById(request.getPatientUserId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found with id: " + request.getPatientUserId()));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorUserId())
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor not found with id: " + request.getDoctorUserId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + request.getAppointmentId()));
        }

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctorProfile(doctor);
        prescription.setAppointment(appointment);
        prescription.setStatus(request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().trim().toUpperCase()
                : "ACTIVE");
        prescription.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
        prescription.setIssuedAt(request.getIssuedAt() != null ? request.getIssuedAt() : Instant.now());
        prescription.setCreatedAt(Instant.now());

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreatePrescriptionItemRequest itemReq : request.getItems()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setMedicationName(itemReq.getMedicationName().trim());
                item.setStrength(itemReq.getStrength() != null ? itemReq.getStrength().trim() : null);
                item.setDosage(itemReq.getDosage() != null ? itemReq.getDosage().trim() : null);
                item.setFrequency(itemReq.getFrequency() != null ? itemReq.getFrequency().trim() : null);
                item.setDuration(itemReq.getDuration() != null ? itemReq.getDuration().trim() : null);
                item.setInstructions(itemReq.getInstructions() != null ? itemReq.getInstructions().trim() : null);
                prescription.addItem(item);
            }
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return PrescriptionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId));
        return PrescriptionResponse.fromEntity(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptions(Long patientUserId, Long doctorUserId, Long appointmentId, String status) {
        List<Prescription> list;
        if (appointmentId != null) {
            list = prescriptionRepository.findByAppointment_AppointmentId(appointmentId);
        } else if (patientUserId != null) {
            list = prescriptionRepository.findByPatient_UserId(patientUserId);
        } else if (doctorUserId != null) {
            list = prescriptionRepository.findByDoctorProfile_UserId(doctorUserId);
        } else if (status != null) {
            list = prescriptionRepository.findByStatusIgnoreCase(status);
        } else {
            list = prescriptionRepository.findAll();
        }
        return list.stream().map(PrescriptionResponse::fromEntity).toList();
    }

    @Transactional
    public PrescriptionResponse updatePrescription(Long prescriptionId, UpdatePrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            prescription.setStatus(request.getStatus().trim().toUpperCase());
        }
        if (request.getNotes() != null) {
            prescription.setNotes(request.getNotes().trim());
        }

        Prescription updated = prescriptionRepository.save(prescription);
        return PrescriptionResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePrescription(Long prescriptionId) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    @Transactional
    public PrescriptionItemResponse addItemToPrescription(Long prescriptionId, CreatePrescriptionItemRequest request) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId));

        PrescriptionItem item = new PrescriptionItem();
        item.setMedicationName(request.getMedicationName().trim());
        item.setStrength(request.getStrength() != null ? request.getStrength().trim() : null);
        item.setDosage(request.getDosage() != null ? request.getDosage().trim() : null);
        item.setFrequency(request.getFrequency() != null ? request.getFrequency().trim() : null);
        item.setDuration(request.getDuration() != null ? request.getDuration().trim() : null);
        item.setInstructions(request.getInstructions() != null ? request.getInstructions().trim() : null);
        prescription.addItem(item);

        PrescriptionItem saved = prescriptionItemRepository.save(item);
        return PrescriptionItemResponse.fromEntity(saved);
    }

    @Transactional
    public PrescriptionItemResponse updatePrescriptionItem(Long prescriptionId, Long itemId, UpdatePrescriptionItemRequest request) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId);
        }

        PrescriptionItem item = prescriptionItemRepository.findById(itemId)
                .orElseThrow(() -> new PrescriptionItemNotFoundException("Prescription item not found with id: " + itemId));

        if (!item.getPrescription().getPrescriptionId().equals(prescriptionId)) {
            throw new PrescriptionItemNotFoundException("Item id " + itemId + " does not belong to prescription id " + prescriptionId);
        }

        if (request.getMedicationName() != null && !request.getMedicationName().isBlank()) {
            item.setMedicationName(request.getMedicationName().trim());
        }
        if (request.getStrength() != null) {
            item.setStrength(request.getStrength().trim());
        }
        if (request.getDosage() != null) {
            item.setDosage(request.getDosage().trim());
        }
        if (request.getFrequency() != null) {
            item.setFrequency(request.getFrequency().trim());
        }
        if (request.getDuration() != null) {
            item.setDuration(request.getDuration().trim());
        }
        if (request.getInstructions() != null) {
            item.setInstructions(request.getInstructions().trim());
        }

        PrescriptionItem updated = prescriptionItemRepository.save(item);
        return PrescriptionItemResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePrescriptionItem(Long prescriptionId, Long itemId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId));

        PrescriptionItem item = prescriptionItemRepository.findById(itemId)
                .orElseThrow(() -> new PrescriptionItemNotFoundException("Prescription item not found with id: " + itemId));

        if (!item.getPrescription().getPrescriptionId().equals(prescriptionId)) {
            throw new PrescriptionItemNotFoundException("Item id " + itemId + " does not belong to prescription id " + prescriptionId);
        }

        prescription.removeItem(item);
        prescriptionItemRepository.delete(item);
    }
}

