package com.example.appointments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProviderRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 100) String specialty,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String state,
        @NotBlank @Pattern(regexp = "\\d{10}", message = "NPI must contain exactly 10 digits") String npi,
        @Size(max = 2_000) String bio,
        Boolean acceptingNewPatients,
        Set<@NotBlank String> acceptedInsurances,
        Set<@NotBlank String> treatedSymptoms
) {}
