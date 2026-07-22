import React, { useState } from "react";
import { api } from "../api";

export default function AppointmentForm({ patient, provider }) {
  const [start, setStart] = useState("");
  const [reason, setReason] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const disabled = !patient || !provider;

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");
    setMessage("");
    const startTime = new Date(start);
    const endTime = new Date(startTime.getTime() + 30 * 60 * 1000);
    const local = date => new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 19);
    try {
      const result = await api("/appointments", { method: "POST", body: JSON.stringify({
        patientId: patient.id, providerId: provider.id,
        startTime: local(startTime), endTime: local(endTime), reason
      })});
      setMessage(`Appointment #${result.id} is scheduled with ${result.providerName}.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <article className={`card ${disabled ? "disabled" : ""}`}>
      <div className="card-heading"><span>03</span><div><h2>Book a visit</h2><p>Appointments are reserved for 30 minutes.</p></div></div>
      {provider && <div className="selection"><strong>{provider.name}</strong><span>{provider.specialty}</span></div>}
      <form onSubmit={submit}>
        <label>Date and time<input required disabled={disabled} type="datetime-local" value={start} onChange={e => setStart(e.target.value)}/></label>
        <label>Reason for visit<textarea required disabled={disabled} maxLength="500" value={reason} onChange={e => setReason(e.target.value)}/></label>
        {error && <p className="error" role="alert">{error}</p>}
        {message && <p className="success" role="status">{message}</p>}
        <button className="primary" disabled={disabled || busy}>{busy ? "Booking…" : "Book appointment"}</button>
      </form>
    </article>
  );
}
