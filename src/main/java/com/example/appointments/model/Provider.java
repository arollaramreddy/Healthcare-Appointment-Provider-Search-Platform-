package com.example.appointments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Provider {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String specialty;
    private String location;

    public Provider() {}

    public Provider(String name, String specialty, String location) {
        this.name = name;
        this.specialty = specialty;
        this.location = location;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
