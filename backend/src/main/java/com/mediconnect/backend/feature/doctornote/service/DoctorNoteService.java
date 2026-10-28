package com.mediconnect.backend.feature.doctornote.service;

import com.mediconnect.backend.feature.appointment.entity.Appointment;
import com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException;
import com.mediconnect.backend.feature.appointment.repository.AppointmentRepository;
import com.mediconnect.backend.feature.doctornote.dto.CreateDoctorNoteRequest;
import com.mediconnect.backend.feature.doctornote.dto.DoctorNoteResponse;
import com.mediconnect.backend.feature.doctornote.dto.UpdateDoctorNoteRequest;
import com.mediconnect.backend.feature.doctornote.entity.DoctorNote;
import com.mediconnect.backend.feature.doctornote.exception.DoctorNoteNotFoundException;
import com.mediconnect.backend.feature.doctornote.repository.DoctorNoteRepository;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.repository.DoctorProfileRepository;
import com.mediconnect.backend.feature.user.entity.User;
import com.mediconnect.backend.feature.user.exception.UserNotFoundException;
import com.mediconnect.backend.feature.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorNoteService {

    private final DoctorNoteRepository doctorNoteRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorNoteService(
            DoctorNoteRepository doctorNoteRepository,
            DoctorProfileRepository doctorProfileRepository,
            UserRepository userRepository,
            AppointmentRepository appointmentRepository) {
        this.doctorNoteRepository = doctorNoteRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public DoctorNoteResponse createNote(CreateDoctorNoteRequest request) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(request.getDoctorUserId())
                .orElseThrow(() -> new DoctorProfileNotFoundException("Doctor profile not found with id: " + request.getDoctorUserId()));

        User patient = userRepository.findById(request.getPatientUserId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found with id: " + request.getPatientUserId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + request.getAppointmentId()));
        }

        DoctorNote note = new DoctorNote();
        note.setDoctorProfile(doctorProfile);
        note.setPatient(patient);
        note.setAppointment(appointment);
        note.setNoteContent(request.getNoteContent().trim());
        Instant now = Instant.now();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        DoctorNote saved = doctorNoteRepository.save(note);
        return DoctorNoteResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DoctorNoteResponse getNoteById(Long noteId) {
        DoctorNote note = doctorNoteRepository.findById(noteId)
                .orElseThrow(() -> new DoctorNoteNotFoundException("Doctor note not found with id: " + noteId));
        return DoctorNoteResponse.fromEntity(note);
    }

    @Transactional(readOnly = true)
    public List<DoctorNoteResponse> getNotes(Long doctorUserId, Long patientUserId, Long appointmentId) {
        List<DoctorNote> notes;
        if (appointmentId != null) {
            notes = doctorNoteRepository.findByAppointment_AppointmentId(appointmentId);
        } else if (doctorUserId != null && patientUserId != null) {
            notes = doctorNoteRepository.findByDoctorProfile_UserIdAndPatient_UserId(doctorUserId, patientUserId);
        } else if (doctorUserId != null) {
            notes = doctorNoteRepository.findByDoctorProfile_UserId(doctorUserId);
        } else if (patientUserId != null) {
            notes = doctorNoteRepository.findByPatient_UserId(patientUserId);
        } else {
            notes = doctorNoteRepository.findAll();
        }
        return notes.stream().map(DoctorNoteResponse::fromEntity).toList();
    }

    @Transactional
    public DoctorNoteResponse updateNote(Long noteId, UpdateDoctorNoteRequest request) {
        DoctorNote note = doctorNoteRepository.findById(noteId)
                .orElseThrow(() -> new DoctorNoteNotFoundException("Doctor note not found with id: " + noteId));

        note.setNoteContent(request.getNoteContent().trim());
        note.setUpdatedAt(Instant.now());

        DoctorNote updated = doctorNoteRepository.save(note);
        return DoctorNoteResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        if (!doctorNoteRepository.existsById(noteId)) {
            throw new DoctorNoteNotFoundException("Doctor note not found with id: " + noteId);
        }
        doctorNoteRepository.deleteById(noteId);
    }
}

