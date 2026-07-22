package com.example.appointments.service;

import com.example.appointments.dto.ProviderRequest;
import com.example.appointments.dto.ProviderResponse;
import com.example.appointments.exception.ConflictException;
import com.example.appointments.exception.ResourceNotFoundException;
import com.example.appointments.model.Provider;
import com.example.appointments.repository.ProviderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional
    public ProviderResponse create(ProviderRequest request) {
        if (providerRepository.existsByNpi(request.npi())) {
            throw new ConflictException("A provider with this NPI already exists");
        }
        Provider provider = new Provider(request.name().trim(), request.specialty().trim(), request.city().trim(),
                request.state().toUpperCase(), request.npi());
        apply(provider, request);
        return ProviderResponse.from(providerRepository.save(provider));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "providerProfiles", key = "#id")
    public ProviderResponse get(Long id) {
        return ProviderResponse.from(providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider " + id + " was not found")));
    }

    @Transactional
    @CacheEvict(cacheNames = "providerProfiles", key = "#id")
    public ProviderResponse update(Long id, ProviderRequest request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider " + id + " was not found"));
        if (!provider.getNpi().equals(request.npi()) && providerRepository.existsByNpi(request.npi())) {
            throw new ConflictException("A provider with this NPI already exists");
        }
        provider.setName(request.name().trim());
        provider.setSpecialty(request.specialty().trim());
        provider.setCity(request.city().trim());
        provider.setState(request.state().toUpperCase());
        provider.setNpi(request.npi());
        apply(provider, request);
        return ProviderResponse.from(provider);
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> search(String specialty, String city, String state, Boolean accepting) {
        return providerRepository.search(emptyToNull(specialty), emptyToNull(city), emptyToNull(state), accepting)
                .stream().map(ProviderResponse::from).toList();
    }

    private void apply(Provider provider, ProviderRequest request) {
        provider.setBio(request.bio());
        provider.setAcceptingNewPatients(request.acceptingNewPatients() == null || request.acceptingNewPatients());
        provider.setAcceptedInsurances(request.acceptedInsurances() == null
                ? new LinkedHashSet<>() : new LinkedHashSet<>(request.acceptedInsurances()));
        provider.setTreatedSymptoms(request.treatedSymptoms() == null
                ? new LinkedHashSet<>() : new LinkedHashSet<>(request.treatedSymptoms()));
    }

    private String emptyToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
