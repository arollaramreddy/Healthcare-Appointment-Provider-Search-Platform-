package com.example.appointments.service;

import com.example.appointments.model.Appointment;
import com.example.appointments.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    private AppointmentRepository repo;
    private AppointmentService svc;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(AppointmentRepository.class);
        svc = new AppointmentService(repo);
    }

    @Test
    void scheduleSucceedsWhenNoConflict() {
        Appointment a = new Appointment(1L, 2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        when(repo.findByProviderIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(repo.save(a)).thenReturn(a);
        Appointment saved = svc.schedule(a);
        assertEquals(a, saved);
        verify(repo).save(a);
    }

    @Test
    void scheduleFailsOnConflict() {
        Appointment a = new Appointment(1L, 2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        when(repo.findByProviderIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(anyLong(), any(), any()))
                .thenReturn(List.of(new Appointment()));
        assertThrows(IllegalStateException.class, () -> svc.schedule(a));
        verify(repo, never()).save(any());
    }
}
