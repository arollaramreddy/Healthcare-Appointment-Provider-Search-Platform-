package com.example.appointments.dto;

import com.example.appointments.model.Provider;

import java.util.Set;

public record ProviderResponse(
        Long id,
        String name,
        String specialty,
        String city,
        String state,
        String npi,
        String bio,
        boolean acceptingNewPatients,
        Set<String> acceptedInsurances,
        Set<String> treatedSymptoms
) {
    public static ProviderResponse from(Provider provider) {
        return new ProviderResponse(provider.getId(), provider.getName(), provider.getSpecialty(),
                provider.getCity(), provider.getState(), provider.getNpi(), provider.getBio(),
                provider.isAcceptingNewPatients(), Set.copyOf(provider.getAcceptedInsurances()),
                Set.copyOf(provider.getTreatedSymptoms()));
    }
}
