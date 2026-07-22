package com.example.appointments.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record SearchPage<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public static <T> SearchPage<T> from(Page<T> result) {
        return new SearchPage<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
