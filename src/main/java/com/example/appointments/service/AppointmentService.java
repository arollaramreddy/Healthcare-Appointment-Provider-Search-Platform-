package com.example.appointments.service;

import com.example.appointments.model.Appointment;
import com.example.appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) { this.repo = repo; }

    public Appointment schedule(Appointment appt) {
        // simple conflict check: provider cannot have overlapping appointments
        List<Appointment> conflicts = repo.findByProviderIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                appt.getProviderId(), appt.getEndTime(), appt.getStartTime());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Provider not available at requested time");
        }
        return repo.save(appt);
    }
}
