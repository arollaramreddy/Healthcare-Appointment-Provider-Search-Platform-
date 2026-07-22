package com.example.appointments.controller;

import com.example.appointments.dto.ProviderRequest;
import com.example.appointments.dto.ProviderResponse;
import com.example.appointments.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {
    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> create(@Valid @RequestBody ProviderRequest request) {
        ProviderResponse provider = providerService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(provider.id()).toUri();
        return ResponseEntity.created(location).body(provider);
    }

    @GetMapping("/{id}")
    public ProviderResponse get(@PathVariable Long id) {
        return providerService.get(id);
    }

    @PutMapping("/{id}")
    public ProviderResponse update(@PathVariable Long id, @Valid @RequestBody ProviderRequest request) {
        return providerService.update(id, request);
    }

    @GetMapping
    public List<ProviderResponse> search(@RequestParam(required = false) String specialty,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) Boolean acceptingNewPatients) {
        return providerService.search(specialty, city, state, acceptingNewPatients);
    }
}
