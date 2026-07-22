package com.example.appointments.dto;

import com.example.appointments.search.ProviderDocument;

import java.util.Set;

public record ProviderSearchResult(Long id, String name, String specialty, String city, String state,
                                   String bio, Set<String> treatedSymptoms, Set<String> acceptedInsurances,
                                   boolean acceptingNewPatients) {
    public static ProviderSearchResult from(ProviderDocument document) {
        return new ProviderSearchResult(document.getId(), document.getName(), document.getSpecialty(),
                document.getCity(), document.getState(), document.getBio(), document.getTreatedSymptoms(),
                document.getAcceptedInsurances(), document.isAcceptingNewPatients());
    }
}
