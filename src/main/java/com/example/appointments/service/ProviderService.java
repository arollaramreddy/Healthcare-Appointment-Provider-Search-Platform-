package com.example.appointments.service;

import com.example.appointments.model.Provider;
import com.example.appointments.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {
    private final ProviderRepository repo;

    public ProviderService(ProviderRepository repo) { this.repo = repo; }

    public Provider save(Provider p) { return repo.save(p); }

    public List<Provider> searchBySpecialty(String specialty) {
        return repo.findBySpecialtyContainingIgnoreCase(specialty);
    }

    public List<Provider> searchByLocation(String location) {
        return repo.findByLocationContainingIgnoreCase(location);
    }
}
