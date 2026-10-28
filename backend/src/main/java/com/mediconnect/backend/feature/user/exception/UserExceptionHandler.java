package com.mediconnect.backend.feature.user.exception;

import com.mediconnect.backend.feature.doctorprofile.exception.DoctorProfileNotFoundException;
import com.mediconnect.backend.feature.doctorprofile.exception.DuplicateLicenseNumberException;
import com.mediconnect.backend.feature.doctorspecialization.exception.SpecializationAlreadyAssignedException;
import com.mediconnect.backend.feature.doctorspecialization.exception.SpecializationNotAssignedException;
import com.mediconnect.backend.feature.specialization.exception.DuplicateSpecializationException;
import com.mediconnect.backend.feature.specialization.exception.SpecializationNotFoundException;
import com.mediconnect.backend.feature.user.dto.ErrorResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields",
                errors,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeaderException(MissingRequestHeaderException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required request header '" + ex.getHeaderName() + "' is missing",
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getName() + "' could not be converted to type "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type");
        if ("X-Client-User-Id".equalsIgnoreCase(ex.getName())) {
            message = "Header 'X-Client-User-Id' must be a valid UUID";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            DoctorProfileNotFoundException.class,
            SpecializationNotFoundException.class,
            SpecializationNotAssignedException.class,
            com.mediconnect.backend.feature.userlanguage.exception.UserLanguageNotFoundException.class,
            com.mediconnect.backend.feature.slot.exception.SlotNotFoundException.class,
            com.mediconnect.backend.feature.appointment.exception.AppointmentNotFoundException.class,
            com.mediconnect.backend.feature.videosession.exception.VideoSessionNotFoundException.class,
            com.mediconnect.backend.feature.review.exception.ReviewNotFoundException.class,
            com.mediconnect.backend.feature.doctornote.exception.DoctorNoteNotFoundException.class,
            com.mediconnect.backend.feature.prescription.exception.PrescriptionNotFoundException.class,
            com.mediconnect.backend.feature.prescriptionitem.exception.PrescriptionItemNotFoundException.class,
            com.mediconnect.backend.feature.doctorembedding.exception.DoctorEmbeddingNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({
            UserAlreadyRegisteredException.class,
            DuplicateResourceException.class,
            DuplicateLicenseNumberException.class,
            DuplicateSpecializationException.class,
            SpecializationAlreadyAssignedException.class,
            com.mediconnect.backend.feature.userlanguage.exception.UserLanguageAlreadyExistsException.class,
            com.mediconnect.backend.feature.appointment.exception.SlotNotAvailableException.class,
            com.mediconnect.backend.feature.videosession.exception.DuplicateVideoSessionException.class,
            com.mediconnect.backend.feature.review.exception.DuplicateReviewException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred: " + ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
