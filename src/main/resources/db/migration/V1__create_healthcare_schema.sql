CREATE TABLE providers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    npi VARCHAR(30) NOT NULL UNIQUE,
    bio VARCHAR(2000),
    accepting_new_patients BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_provider_specialty ON providers (specialty);
CREATE INDEX idx_provider_location ON providers (city, state);

CREATE TABLE provider_insurances (
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    insurance VARCHAR(255) NOT NULL,
    PRIMARY KEY (provider_id, insurance)
);

CREATE TABLE provider_symptoms (
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    symptom VARCHAR(255) NOT NULL,
    PRIMARY KEY (provider_id, symptom)
);

CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    intake_notes VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX idx_patient_email ON patients (email);

CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    provider_id BIGINT NOT NULL REFERENCES providers(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_appointment_times CHECK (end_time > start_time)
);

CREATE INDEX idx_appointment_provider_time ON appointments (provider_id, start_time, end_time);
CREATE INDEX idx_appointment_patient ON appointments (patient_id);
