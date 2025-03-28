// File: frontend/src/App.js

import React, { useState } from 'react';
import axios from 'axios';

function App() {
  const [file, setFile] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleUpload = async () => {
    if (!file) {
      alert("Please select a file first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    setLoading(true);
    try {
      const response = await axios.post("http://localhost:8080/api/analyze", formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setAnalytics(response.data);
    } catch (error) {
      console.error("Upload failed:", error);
      alert("File analysis failed. Please try again.");
    }
    setLoading(false);
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-4 text-center">📊 Researcher Analytics</h1>

      <div className="mb-4">
        <input type="file" onChange={e => setFile(e.target.files[0])} />
        <button
          onClick={handleUpload}
          className="ml-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Upload & Analyze
        </button>
      </div>

      {loading && <p className="text-gray-600">Analyzing your document...</p>}

      {analytics && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-2">Top Keywords</h2>
          <ul className="list-disc list-inside mb-4">
            {analytics.keywords.map((word, idx) => (
              <li key={idx}>{word}</li>
            ))}
          </ul>

          <h2 className="text-xl font-semibold mb-2">Topic Summary</h2>
          <ul className="list-disc list-inside">
            {analytics.topics.map((topic, idx) => (
              <li key={idx}>
                {topic.label} – Score: {topic.score.toFixed(2)}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default App;
