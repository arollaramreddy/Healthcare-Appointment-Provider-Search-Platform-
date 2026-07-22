package com.example.appointments.dto;

import com.example.appointments.model.Appointment;
import com.example.appointments.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long providerId,
        String providerName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String reason,
        String cancellationReason
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(appointment.getId(), appointment.getPatient().getId(),
                appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
                appointment.getProvider().getId(), appointment.getProvider().getName(),
                appointment.getStartTime(), appointment.getEndTime(), appointment.getStatus(),
                appointment.getReason(), appointment.getCancellationReason());
    }
}
