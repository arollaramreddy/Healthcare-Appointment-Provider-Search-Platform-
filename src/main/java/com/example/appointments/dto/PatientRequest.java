package com.example.appointments.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Pattern(regexp = "^[+()0-9 .-]{7,30}$") String phone,
        @Size(max = 500) String intakeNotes
) {}
