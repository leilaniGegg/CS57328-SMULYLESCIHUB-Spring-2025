import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [jobs, setJobs] = useState([]);
  const [jobTitle, setJobTitle] = useState('');
  const [jobStatus, setJobStatus] = useState('');

  // Fetch jobs from the backend
  useEffect(() => {
    axios.get('http://localhost:8080/api/jobs')
        .then(response => {
          setJobs(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the jobs!", error);
        });
  }, []);

  // Create a new job
  const createJob = () => {
    axios.post('http://localhost:8080/api/jobs', {
      title: jobTitle,
      status: jobStatus,
    })
        .then(response => {
          setJobs([...jobs, response.data]);
          setJobTitle('');
          setJobStatus('');
        })
        .catch(error => {
          console.error("There was an error creating the job!", error);
        });
  };

  // Update the status of a job
  const updateStatus = (id, status) => {
    axios.put(`http://localhost:8080/api/jobs/${id}/status`, { status })
        .then(response => {
          const updatedJobs = jobs.map(job =>
              job.id === id ? { ...job, status: response.data.status } : job
          );
          setJobs(updatedJobs);
        })
        .catch(error => {
          console.error("There was an error updating the job!", error);
        });
  };

  // Delete a job
  const deleteJob = (id) => {
    axios.delete(`http://localhost:8080/api/jobs/${id}`)
        .then(() => {
          setJobs(jobs.filter(job => job.id !== id));
        })
        .catch(error => {
          console.error("There was an error deleting the job!", error);
        });
  };

  return (
      <div className="App">
        <h1>Job Management</h1>

        {/* Create Job Form */}
        <div>
          <input
              type="text"
              placeholder="Job Title"
              value={jobTitle}
              onChange={(e) => setJobTitle(e.target.value)}
          />
          <input
              type="text"
              placeholder="Job Status"
              value={jobStatus}
              onChange={(e) => setJobStatus(e.target.value)}
          />
          <button onClick={createJob}>Create Job</button>
        </div>

        <h2>All Jobs</h2>
        <ul>
          {jobs.map(job => (
              <li key={job.id}>
                <strong>{job.title}</strong> - {job.status}
                <button onClick={() => updateStatus(job.id, 'closed')}>Mark as Closed</button>
                <button onClick={() => deleteJob(job.id)}>Delete</button>
              </li>
          ))}
        </ul>
      </div>
  );
}

export default App;
