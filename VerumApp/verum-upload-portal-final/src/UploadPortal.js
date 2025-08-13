import React from 'react';

function UploadPortal() {
  return (
    <div>
      <h1>Upload your documents</h1>
      <form>
        <input type="file" />
        <button type="submit">Upload</button>
      </form>
    </div>
  );
}

export default UploadPortal;