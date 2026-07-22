import React, { useState } from "react";
import { api } from "../api";

export default function ProviderSearch({ disabled, selected, onSelect }) {
  const [query, setQuery] = useState("");
  const [providers, setProviders] = useState([]);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function search(event) {
    event.preventDefault();
    setBusy(true);
    setError("");
    try {
      const result = await api(`/providers/search?query=${encodeURIComponent(query)}`);
      setProviders(result.content);
      if (!result.content.length) setError("No accepting providers matched that search.");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <article className={`card ${disabled ? "disabled" : ""}`}>
      <div className="card-heading"><span>02</span><div><h2>Find a provider</h2><p>Search symptoms or specialties.</p></div></div>
      <form className="search-row" onSubmit={search}>
        <input aria-label="Symptoms or specialty" required disabled={disabled} placeholder="e.g. migraine, rash, cardiology" value={query} onChange={e => setQuery(e.target.value)}/>
        <button className="primary compact" disabled={disabled || busy}>{busy ? "…" : "Search"}</button>
      </form>
      {error && <p className="error" role="alert">{error}</p>}
      <div className="results">
        {providers.map(provider => (
          <button type="button" className={`provider ${selected?.id === provider.id ? "selected" : ""}`} key={provider.id} onClick={() => onSelect(provider)}>
            <span className="avatar">{provider.name.split(" ").slice(-1)[0][0]}</span>
            <span><strong>{provider.name}</strong><small>{provider.specialty} · {provider.city}, {provider.state}</small></span>
            <span className="select-mark">{selected?.id === provider.id ? "✓" : "→"}</span>
          </button>
        ))}
      </div>
    </article>
  );
}
