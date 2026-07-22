import React, { useState } from "react";
import { api } from "../api";

const initial = { firstName: "", lastName: "", dateOfBirth: "", email: "", phone: "", intakeNotes: "" };

export default function PatientIntake({ patient, onComplete }) {
  const [form, setForm] = useState(initial);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");
    try {
      onComplete(await api("/patients", { method: "POST", body: JSON.stringify(form) }));
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  if (patient) return (
    <article className="card complete-card">
      <span className="check">✓</span>
      <div><h2>Patient ready</h2><p>{patient.firstName} {patient.lastName}</p></div>
      <button className="text-button" onClick={() => onComplete(null)}>Change</button>
    </article>
  );

  return (
    <article className="card">
      <div className="card-heading"><span>01</span><div><h2>Patient intake</h2><p>Create a secure patient profile.</p></div></div>
      <form onSubmit={submit}>
        <div className="two-col">
          <label>First name<input required value={form.firstName} onChange={e => setForm({...form, firstName:e.target.value})}/></label>
          <label>Last name<input required value={form.lastName} onChange={e => setForm({...form, lastName:e.target.value})}/></label>
        </div>
        <label>Date of birth<input required type="date" value={form.dateOfBirth} onChange={e => setForm({...form, dateOfBirth:e.target.value})}/></label>
        <label>Email<input required type="email" value={form.email} onChange={e => setForm({...form, email:e.target.value})}/></label>
        <label>Phone<input required value={form.phone} onChange={e => setForm({...form, phone:e.target.value})}/></label>
        <label>Anything we should know?<textarea maxLength="500" value={form.intakeNotes} onChange={e => setForm({...form, intakeNotes:e.target.value})}/></label>
        {error && <p className="error" role="alert">{error}</p>}
        <button className="primary" disabled={busy}>{busy ? "Saving…" : "Continue to search"}</button>
      </form>
    </article>
  );
}
