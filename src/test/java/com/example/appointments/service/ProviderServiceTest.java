package com.example.appointments.service;

import com.example.appointments.dto.ProviderRequest;
import com.example.appointments.exception.ConflictException;
import com.example.appointments.model.Provider;
import com.example.appointments.repository.ProviderRepository;
import com.example.appointments.search.ProviderChangedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderServiceTest {
    private final ProviderRepository repository = mock(ProviderRepository.class);
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final ProviderService service = new ProviderService(repository, publisher);

    @Test
    void createsAndPublishesIndexEvent() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.create(request("1234567890"));

        assertEquals("Neurology", result.specialty());
        assertTrue(result.treatedSymptoms().contains("migraine"));
        verify(publisher).publishEvent(any(ProviderChangedEvent.class));
    }

    @Test
    void rejectsDuplicateNpi() {
        when(repository.existsByNpi("1234567890")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(request("1234567890")));
        verify(repository, never()).save(any(Provider.class));
    }

    private ProviderRequest request(String npi) {
        return new ProviderRequest("Dr. Lee", "Neurology", "Phoenix", "az", npi,
                "Headache specialist", true, Set.of("Aetna"), Set.of("migraine"));
    }
}
