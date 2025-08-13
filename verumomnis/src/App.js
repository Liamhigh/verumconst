import React from 'react';

function App() {
  return (
    <div style={{ textAlign: 'center', padding: '2rem' }}>
      <img
        src="/verum_logo.png"
        alt="Verum Omnis Logo"
        style={{ width: '160px', marginBottom: '1.5rem' }}
      />
      <h1>Verum Omnis</h1>
      <h2>Global Forensic AI — Upload. Verify. Protect.</h2>
      <p style={{ maxWidth: '500px', margin: '1rem auto' }}>
        This platform verifies documents, images, videos, and voice files
        using 9 integrated AI systems. It does not store your data. Results
        are returned instantly and never saved.
      </p>

      <label htmlFor="userType"><strong>Who are you?</strong></label>
      <select id="userType" style={{ margin: '1rem' }}>
        <option value="">Select user type</option>
        <option value="private">Private Person</option>
        <option value="institution">Institution (Trial)</option>
        <option value="company">Company (Trial — Licensing fees apply)</option>
      </select>

      <div style={{ marginTop: '2rem' }}>
        <h3>Upload a document for verification</h3>
        <input type="file" />
        <br /><br />
        <button>Verify File</button>
      </div>
    </div>
  );
}

export default App;
