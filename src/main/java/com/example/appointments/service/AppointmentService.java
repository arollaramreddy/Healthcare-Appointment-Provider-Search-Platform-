package com.example.appointments.service;

import com.example.appointments.dto.AppointmentRequest;
import com.example.appointments.dto.AppointmentResponse;
import com.example.appointments.dto.RescheduleRequest;
import com.example.appointments.exception.ConflictException;
import com.example.appointments.exception.ResourceNotFoundException;
import com.example.appointments.model.*;
import com.example.appointments.repository.AppointmentRepository;
import com.example.appointments.repository.PatientRepository;
import com.example.appointments.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class AppointmentService {
    private static final EnumSet<AppointmentStatus> ACTIVE =
            EnumSet.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final Clock clock;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                              ProviderRepository providerRepository, Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.providerRepository = providerRepository;
        this.clock = clock;
    }

    @Transactional
    public AppointmentResponse schedule(AppointmentRequest request) {
        validateTimes(request.startTime(), request.endTime());
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + request.patientId() + " was not found"));
        Provider provider = lockProvider(request.providerId());
        ensureAvailable(provider.getId(), request.startTime(), request.endTime(), null);
        Appointment appointment = new Appointment(patient, provider, request.startTime(), request.endTime(),
                request.reason().trim());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse reschedule(Long id, RescheduleRequest request) {
        validateTimes(request.startTime(), request.endTime());
        Appointment appointment = find(id);
        ensureMutable(appointment);
        lockProvider(appointment.getProvider().getId());
        ensureAvailable(appointment.getProvider().getId(), request.startTime(), request.endTime(), id);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(Long id, String reason) {
        Appointment appointment = find(id);
        ensureMutable(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason.trim());
        return AppointmentResponse.from(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id) {
        return AppointmentResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> forPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient " + patientId + " was not found");
        }
        return appointmentRepository.findByPatientIdOrderByStartTimeDesc(patientId).stream()
                .map(AppointmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> forProvider(Long providerId, LocalDateTime from, LocalDateTime to) {
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Provider " + providerId + " was not found");
        }
        if (!from.isBefore(to)) throw new IllegalArgumentException("from must be before to");
        return appointmentRepository.findByProviderIdAndStartTimeBetweenOrderByStartTime(providerId, from, to)
                .stream().map(AppointmentResponse::from).toList();
    }

    private Appointment find(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " was not found"));
    }

    private Provider lockProvider(Long id) {
        Provider provider = providerRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider " + id + " was not found"));
        if (!provider.isAcceptingNewPatients()) {
            throw new ConflictException("Provider is not accepting new patients");
        }
        return provider;
    }

    private void ensureAvailable(Long providerId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        if (appointmentRepository.countConflicts(providerId, start, end, ACTIVE, excludeId) > 0) {
            throw new ConflictException("Provider is unavailable during the requested time");
        }
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) throw new IllegalArgumentException("startTime must be before endTime");
        if (!start.isAfter(LocalDateTime.now(clock))) throw new IllegalArgumentException("startTime must be in the future");
        if (start.plusHours(8).isBefore(end)) throw new IllegalArgumentException("appointments cannot exceed 8 hours");
    }

    private void ensureMutable(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ConflictException("A " + appointment.getStatus().name().toLowerCase() + " appointment cannot be changed");
        }
    }
}
