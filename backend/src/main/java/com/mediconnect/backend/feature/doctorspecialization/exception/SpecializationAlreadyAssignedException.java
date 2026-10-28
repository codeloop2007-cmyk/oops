package com.mediconnect.backend.feature.doctorspecialization.exception;

public class SpecializationAlreadyAssignedException extends RuntimeException {

    public SpecializationAlreadyAssignedException(String message) {
        super(message);
    }
}

