package com.example.appointments.controller;

import com.example.appointments.dto.ProviderRequest;
import com.example.appointments.dto.ProviderResponse;
import com.example.appointments.dto.ProviderSearchResult;
import com.example.appointments.dto.SearchPage;
import com.example.appointments.service.ProviderService;
import com.example.appointments.service.ProviderSearchService;
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
    private final ProviderSearchService providerSearchService;

    public ProviderController(ProviderService providerService, ProviderSearchService providerSearchService) {
        this.providerService = providerService;
        this.providerSearchService = providerSearchService;
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

    @GetMapping("/search")
    public SearchPage<ProviderSearchResult> symptomSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (query == null || query.isBlank()) throw new IllegalArgumentException("query is required");
        if (page < 0) throw new IllegalArgumentException("page cannot be negative");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100");
        return providerSearchService.search(query.trim(), page, size);
    }

    @PostMapping("/search/reindex")
    public java.util.Map<String, Long> reindex() {
        return java.util.Map.of("indexedProviders", providerSearchService.rebuildIndex());
    }
}
