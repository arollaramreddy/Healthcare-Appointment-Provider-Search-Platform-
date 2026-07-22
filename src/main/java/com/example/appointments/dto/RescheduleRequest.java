package com.example.appointments.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequest(@NotNull @Future LocalDateTime startTime,
                                @NotNull @Future LocalDateTime endTime) {}
