package com.example.appointments.controller;

import com.example.appointments.model.Appointment;
import com.example.appointments.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    private final AppointmentService svc;

    public AppointmentController(AppointmentService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<?> schedule(@RequestBody Appointment appt) {
        try {
            Appointment saved = svc.schedule(appt);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
