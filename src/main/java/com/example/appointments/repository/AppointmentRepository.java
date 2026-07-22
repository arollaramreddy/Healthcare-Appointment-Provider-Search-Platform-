package com.example.appointments.repository;

import com.example.appointments.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByProviderIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long providerId, LocalDateTime end, LocalDateTime start);
}
