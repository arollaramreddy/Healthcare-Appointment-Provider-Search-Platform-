import React, { useState } from "react";

export default function ProviderSearch() {
  const [specialty, setSpecialty] = useState("");
  const [providers, setProviders] = useState([]);

  async function search() {
    const q = new URLSearchParams();
    if (specialty) q.set('specialty', specialty);
    const res = await fetch(`http://localhost:8080/providers/search?${q.toString()}`);
    const data = await res.json();
    setProviders(data);
  }

  return (
    <div>
      <input placeholder="Specialty" value={specialty} onChange={e=>setSpecialty(e.target.value)} />
      <button onClick={search}>Search</button>
      <ul>
        {providers.map(p => (
          <li key={p.id}>{p.name} — {p.specialty} — {p.location}</li>
        ))}
      </ul>
    </div>
  );
}
