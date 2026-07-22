package com.example.appointments.controller;

import com.example.appointments.model.Provider;
import com.example.appointments.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderService svc;

    public ProviderController(ProviderService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Provider> create(@RequestBody Provider p) {
        return ResponseEntity.ok(svc.save(p));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Provider>> search(@RequestParam(required = false) String specialty,
                                                 @RequestParam(required = false) String location) {
        if (specialty != null) return ResponseEntity.ok(svc.searchBySpecialty(specialty));
        if (location != null) return ResponseEntity.ok(svc.searchByLocation(location));
        return ResponseEntity.ok(List.of());
    }
}
