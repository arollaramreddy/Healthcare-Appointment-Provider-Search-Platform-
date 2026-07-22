package com.example.appointments.service;

import com.example.appointments.dto.AppointmentRequest;
import com.example.appointments.dto.RescheduleRequest;
import com.example.appointments.exception.ConflictException;
import com.example.appointments.exception.ResourceNotFoundException;
import com.example.appointments.model.*;
import com.example.appointments.repository.AppointmentRepository;
import com.example.appointments.repository.PatientRepository;
import com.example.appointments.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 7, 21, 10, 0);
    private AppointmentRepository appointments;
    private PatientRepository patients;
    private ProviderRepository providers;
    private AppointmentService service;
    private Patient patient;
    private Provider provider;

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepository.class);
        patients = mock(PatientRepository.class);
        providers = mock(ProviderRepository.class);
        Clock clock = Clock.fixed(NOW.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new AppointmentService(appointments, patients, providers, clock);
        patient = new Patient("Ava", "Patel", LocalDate.of(1990, 4, 4), "ava@example.com", "602-555-0100");
        provider = new Provider("Dr. Lee", "Neurology", "Phoenix", "AZ", "1234567890");
    }

    @Test
    void schedulesWhenProviderIsAvailable() {
        AppointmentRequest request = requestAt(NOW.plusDays(1));
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        when(providers.findByIdForUpdate(2L)).thenReturn(Optional.of(provider));
        when(appointments.countConflicts(eq(2L), any(), any(), anyCollection(), isNull())).thenReturn(0L);
        when(appointments.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.schedule(request);

        assertEquals(AppointmentStatus.SCHEDULED, result.status());
        assertEquals("Migraine consultation", result.reason());
        verify(appointments).save(any(Appointment.class));
    }

    @Test
    void rejectsAnOverlappingAppointment() {
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        when(providers.findByIdForUpdate(2L)).thenReturn(Optional.of(provider));
        when(appointments.countConflicts(anyLong(), any(), any(), anyCollection(), isNull())).thenReturn(1L);

        assertThrows(ConflictException.class, () -> service.schedule(requestAt(NOW.plusDays(1))));
        verify(appointments, never()).save(any());
    }

    @Test
    void rejectsInvalidTimeRangeBeforeRepositoryAccess() {
        var request = new AppointmentRequest(1L, 2L, NOW.plusHours(2), NOW.plusHours(1), "Checkup");
        assertThrows(IllegalArgumentException.class, () -> service.schedule(request));
        verifyNoInteractions(patients, providers, appointments);
    }

    @Test
    void rejectsMissingPatient() {
        when(patients.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.schedule(requestAt(NOW.plusDays(1))));
    }

    @Test
    void cancelledAppointmentCannotBeRescheduled() {
        Appointment appointment = new Appointment(patient, provider, NOW.plusDays(1), NOW.plusDays(1).plusHours(1), "Visit");
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointments.findById(9L)).thenReturn(Optional.of(appointment));

        var request = new RescheduleRequest(NOW.plusDays(2), NOW.plusDays(2).plusHours(1));
        assertThrows(ConflictException.class, () -> service.reschedule(9L, request));
    }

    private AppointmentRequest requestAt(LocalDateTime start) {
        return new AppointmentRequest(1L, 2L, start, start.plusHours(1), "Migraine consultation");
    }
}
