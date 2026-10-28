package com.mediconnect.backend.feature.prescriptionitem.exception;

public class PrescriptionItemNotFoundException extends RuntimeException {
    public PrescriptionItemNotFoundException(String message) {
        super(message);
    }
}

