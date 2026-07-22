import React, { useState } from "react";

export default function AppointmentForm() {
  const [patientId, setPatientId] = useState(1);
  const [providerId, setProviderId] = useState(1);
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [msg, setMsg] = useState("");

  async function submit(e) {
    e.preventDefault();
    const body = { patientId: Number(patientId), providerId: Number(providerId), startTime: start, endTime: end };
    const res = await fetch('http://localhost:8080/appointments', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(body)});
    if (res.ok) {
      const data = await res.json();
      setMsg('Scheduled: ' + data.id);
    } else {
      const text = await res.text();
      setMsg('Failed: ' + text);
    }
  }

  return (
    <form onSubmit={submit}>
      <div>
        <label>Patient Id: <input value={patientId} onChange={e=>setPatientId(e.target.value)} /></label>
      </div>
      <div>
        <label>Provider Id: <input value={providerId} onChange={e=>setProviderId(e.target.value)} /></label>
      </div>
      <div>
        <label>Start: <input type="datetime-local" value={start} onChange={e=>setStart(e.target.value)} /></label>
      </div>
      <div>
        <label>End: <input type="datetime-local" value={end} onChange={e=>setEnd(e.target.value)} /></label>
      </div>
      <button type="submit">Schedule</button>
      <div>{msg}</div>
    </form>
  );
}
