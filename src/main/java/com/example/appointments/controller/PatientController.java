package com.example.appointments.controller;

import com.example.appointments.dto.PatientRequest;
import com.example.appointments.dto.PatientResponse;
import com.example.appointments.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> intake(@Valid @RequestBody PatientRequest request) {
        PatientResponse patient = patientService.intake(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(patient.id()).toUri();
        return ResponseEntity.created(location).body(patient);
    }

    @GetMapping("/{id}")
    public PatientResponse get(@PathVariable Long id) {
        return patientService.get(id);
    }
}
