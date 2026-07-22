package com.example.appointments.search;

import com.example.appointments.model.Provider;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Document(indexName = "providers", createIndex = true)
public class ProviderDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String specialty;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String state;

    @Field(type = FieldType.Text)
    private String bio;

    @Field(type = FieldType.Text)
    private Set<String> treatedSymptoms;

    @Field(type = FieldType.Keyword)
    private Set<String> acceptedInsurances;

    @Field(type = FieldType.Boolean)
    private boolean acceptingNewPatients;

    protected ProviderDocument() {}

    public ProviderDocument(Long id, String name, String specialty, String city, String state, String bio,
                            Set<String> treatedSymptoms, Set<String> acceptedInsurances,
                            boolean acceptingNewPatients) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.city = city;
        this.state = state;
        this.bio = bio;
        this.treatedSymptoms = treatedSymptoms;
        this.acceptedInsurances = acceptedInsurances;
        this.acceptingNewPatients = acceptingNewPatients;
    }

    public static ProviderDocument from(Provider provider) {
        return new ProviderDocument(provider.getId(), provider.getName(), provider.getSpecialty(),
                provider.getCity(), provider.getState(), provider.getBio(), Set.copyOf(provider.getTreatedSymptoms()),
                Set.copyOf(provider.getAcceptedInsurances()), provider.isAcceptingNewPatients());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getBio() { return bio; }
    public Set<String> getTreatedSymptoms() { return treatedSymptoms; }
    public Set<String> getAcceptedInsurances() { return acceptedInsurances; }
    public boolean isAcceptingNewPatients() { return acceptingNewPatients; }
}
