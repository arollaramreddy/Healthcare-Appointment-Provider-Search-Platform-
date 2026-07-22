import React from "react";
import ProviderSearch from "./components/ProviderSearch";
import AppointmentForm from "./components/AppointmentForm";

export default function App() {
  return (
    <div style={{padding:20,fontFamily:'Arial'}}>
      <h2>Provider Search</h2>
      <ProviderSearch />
      <hr />
      <h2>Schedule Appointment</h2>
      <AppointmentForm />
    </div>
  );
}
