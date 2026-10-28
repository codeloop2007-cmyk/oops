package com.mediconnect.backend.feature.appointment.service;

import com.mediconnect.backend.feature.appointment.dto.AppointmentResponse;
import com.mediconnect.backend.feature.appointment.dto.CreateAppointmentRequest;
import com.mediconnect.backend.feature.appointment.dto.UpdateAppointmentRequest;
import com.mediconnect.backend.feature.appointment.entity.Appointment;
import com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException;
import com.mediconnect.backend.feature.appointment.exception.SlotNotAvailableException;
import com.mediconnect.backend.feature.appointment.repository.AppointmentRepository;
import com.mediconnect.backend.feature.slot.entity.Slot;
import com.mediconnect.backend.feature.slot.exception.SlotNotFoundException;
import com.mediconnect.backend.feature.slot.repository.SlotRepository;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            SlotRepository slotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        User patient = userRepository.findById(request.getPatientUserId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found with id: " + request.getPatientUserId()));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + request.getSlotId()));

        if (Boolean.FALSE.equals(slot.getIsBookable())) {
            throw new SlotNotAvailableException("Slot with id " + request.getSlotId() + " is already booked or not available");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setSlot(slot);
        appointment.setStatus("CONFIRMED");
        appointment.setConsultationMode(request.getConsultationMode().trim());
        appointment.setReason(request.getReason() != null ? request.getReason().trim() : null);
        appointment.setBookedAt(Instant.now());

        slot.setIsBookable(false);
        slotRepository.save(slot);

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));
        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointments(Long patientUserId, Long doctorUserId, String status) {
        List<Appointment> appointments;
        if (patientUserId != null && status != null) {
            appointments = appointmentRepository.findByPatient_UserIdAndStatusIgnoreCase(patientUserId, status);
        } else if (doctorUserId != null && status != null) {
            appointments = appointmentRepository.findBySlot_DoctorProfile_UserIdAndStatusIgnoreCase(doctorUserId, status);
        } else if (patientUserId != null) {
            appointments = appointmentRepository.findByPatient_UserId(patientUserId);
        } else if (doctorUserId != null) {
            appointments = appointmentRepository.findBySlot_DoctorProfile_UserId(doctorUserId);
        } else if (status != null) {
            appointments = appointmentRepository.findByStatusIgnoreCase(status);
        } else {
            appointments = appointmentRepository.findAll();
        }
        return appointments.stream().map(AppointmentResponse::fromEntity).toList();
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, UpdateAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            appointment.setStatus(request.getStatus().trim().toUpperCase());
            if ("CANCELLED".equalsIgnoreCase(request.getStatus().trim()) && appointment.getCancelledAt() == null) {
                appointment.setCancelledAt(Instant.now());
                if (appointment.getSlot() != null) {
                    appointment.getSlot().setIsBookable(true);
                    slotRepository.save(appointment.getSlot());
                }
            }
        }
        if (request.getConsultationMode() != null && !request.getConsultationMode().isBlank()) {
            appointment.setConsultationMode(request.getConsultationMode().trim());
        }
        if (request.getReason() != null) {
            appointment.setReason(request.getReason().trim());
        }

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        appointment.setStatus("CANCELLED");
        appointment.setCancelledAt(Instant.now());

        if (appointment.getSlot() != null) {
            appointment.getSlot().setIsBookable(true);
            slotRepository.save(appointment.getSlot());
        }

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        if (appointment.getSlot() != null) {
            appointment.getSlot().setIsBookable(true);
            slotRepository.save(appointment.getSlot());
        }

        appointmentRepository.delete(appointment);
    }
}

