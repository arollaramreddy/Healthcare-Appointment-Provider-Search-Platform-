package com.example.appointments.dto;

import com.example.appointments.model.Patient;

import java.time.LocalDate;

public record PatientResponse(Long id, String firstName, String lastName, LocalDate dateOfBirth,
                              String email, String phone, String intakeNotes) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getDateOfBirth(), patient.getEmail(), patient.getPhone(), patient.getIntakeNotes());
    }
}
