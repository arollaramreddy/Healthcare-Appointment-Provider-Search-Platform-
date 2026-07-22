package com.example.appointments.service;

import com.example.appointments.dto.PatientRequest;
import com.example.appointments.dto.PatientResponse;
import com.example.appointments.exception.ConflictException;
import com.example.appointments.exception.ResourceNotFoundException;
import com.example.appointments.model.Patient;
import com.example.appointments.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientResponse intake(PatientRequest request) {
        String email = request.email().trim().toLowerCase();
        if (patientRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("A patient with this email already exists");
        }
        Patient patient = new Patient(request.firstName().trim(), request.lastName().trim(),
                request.dateOfBirth(), email, request.phone().trim());
        patient.setIntakeNotes(request.intakeNotes());
        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse get(Long id) {
        return PatientResponse.from(patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " was not found")));
    }
}
