package com.example.appointments.repository;

import com.example.appointments.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AppointmentRepositoryTest {
    @Autowired AppointmentRepository appointments;
    @Autowired PatientRepository patients;
    @Autowired ProviderRepository providers;

    @Test
    void detectsOverlapButAllowsAdjacentSlot() {
        Patient patient = patients.save(new Patient("Maya", "Singh", LocalDate.of(1988, 2, 12),
                "maya@example.com", "602-555-0101"));
        Provider provider = providers.save(new Provider("Dr. Lee", "Neurology", "Phoenix", "AZ", "1234567890"));
        LocalDateTime start = LocalDateTime.of(2027, 1, 10, 9, 0);
        appointments.saveAndFlush(new Appointment(patient, provider, start, start.plusHours(1), "Headache"));
        var active = EnumSet.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

        assertEquals(1, appointments.countConflicts(provider.getId(), start.plusMinutes(30),
                start.plusHours(2), active, null));
        assertEquals(0, appointments.countConflicts(provider.getId(), start.plusHours(1),
                start.plusHours(2), active, null));
    }
}
