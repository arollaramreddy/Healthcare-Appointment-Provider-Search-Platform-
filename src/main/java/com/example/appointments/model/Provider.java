package com.example.appointments.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "providers", indexes = {
        @Index(name = "idx_provider_specialty", columnList = "specialty"),
        @Index(name = "idx_provider_location", columnList = "city,state")
})
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialty;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(nullable = false, unique = true, length = 30)
    private String npi;

    @Column(length = 2_000)
    private String bio;

    @Column(nullable = false)
    private boolean acceptingNewPatients = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_insurances", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "insurance", nullable = false)
    private Set<String> acceptedInsurances = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_symptoms", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "symptom", nullable = false)
    private Set<String> treatedSymptoms = new LinkedHashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected Provider() {}

    public Provider(String name, String specialty, String city, String state, String npi) {
        this.name = name;
        this.specialty = specialty;
        this.city = city;
        this.state = state;
        this.npi = npi;
    }

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getNpi() { return npi; }
    public void setNpi(String npi) { this.npi = npi; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public boolean isAcceptingNewPatients() { return acceptingNewPatients; }
    public void setAcceptingNewPatients(boolean acceptingNewPatients) { this.acceptingNewPatients = acceptingNewPatients; }
    public Set<String> getAcceptedInsurances() { return acceptedInsurances; }
    public void setAcceptedInsurances(Set<String> acceptedInsurances) { this.acceptedInsurances = acceptedInsurances; }
    public Set<String> getTreatedSymptoms() { return treatedSymptoms; }
    public void setTreatedSymptoms(Set<String> treatedSymptoms) { this.treatedSymptoms = treatedSymptoms; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
