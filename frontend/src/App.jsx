import React, { useState } from "react";
import PatientIntake from "./components/PatientIntake";
import ProviderSearch from "./components/ProviderSearch";
import AppointmentForm from "./components/AppointmentForm";
import "./styles.css";

export default function App() {
  const [patient, setPatient] = useState(null);
  const [provider, setProvider] = useState(null);

  return (
    <main className="shell">
      <header className="hero">
        <span className="eyebrow">CareConnect</span>
        <h1>Find the right care, then book it.</h1>
        <p>Tell us about the patient, search by symptoms, and reserve a visit in a few steps.</p>
      </header>

      <div className="steps" aria-label="Booking progress">
        <span className={patient ? "done" : "active"}>1. Patient</span>
        <span className={patient && !provider ? "active" : provider ? "done" : ""}>2. Provider</span>
        <span className={provider ? "active" : ""}>3. Appointment</span>
      </div>

      <section className="grid">
        <PatientIntake patient={patient} onComplete={setPatient} />
        <ProviderSearch disabled={!patient} selected={provider} onSelect={setProvider} />
        <AppointmentForm patient={patient} provider={provider} />
      </section>
    </main>
  );
}
