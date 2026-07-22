package com.example.appointments;

import com.example.appointments.search.ProviderSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentApiIntegrationTest {
    @Autowired MockMvc mvc;
    @MockBean ProviderSearchRepository providerSearchRepository;

    @Test
    void completesIntakeProviderAndBookingFlow() throws Exception {
        mvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ava","lastName":"Patel","dateOfBirth":"1990-04-04",
                                 "email":"ava.patel@example.com","phone":"602-555-0100","intakeNotes":"New patient"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Jordan Lee","specialty":"Neurology","city":"Phoenix","state":"AZ",
                                 "npi":"1234567890","bio":"Migraine specialist","acceptingNewPatients":true,
                                 "acceptedInsurances":["Aetna"],"treatedSymptoms":["migraine","dizziness"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        String booking = """
                {"patientId":1,"providerId":1,"startTime":"2030-08-10T09:00:00",
                 "endTime":"2030-08-10T10:00:00","reason":"Recurring migraines"}
                """;
        mvc.perform(post("/api/v1/appointments").contentType(MediaType.APPLICATION_JSON).content(booking))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        mvc.perform(post("/api/v1/appointments").contentType(MediaType.APPLICATION_JSON).content(booking))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Provider is unavailable during the requested time"));

        mvc.perform(patch("/api/v1/appointments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Symptoms resolved\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void returnsStructuredValidationErrors() throws Exception {
        mvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());
    }
}
