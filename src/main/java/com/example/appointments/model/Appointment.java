package com.example.appointments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue
    private Long id;
    private Long patientId;
    private Long providerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public Appointment() {}

    public Appointment(Long patientId, Long providerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.patientId = patientId;
        this.providerId = providerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "SCHEDULED";
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public Long getProviderId() { return providerId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
