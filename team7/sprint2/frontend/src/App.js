import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
    const [jobs, setJobs] = useState([]);
    const [jobTitle, setJobTitle] = useState('');
    const [jobStatus, setJobStatus] = useState('');
    const [jobDescription, setJobDescription] = useState('');
    const [jobCompany, setJobCompany] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [sortOrder, setSortOrder] = useState('newest'); // 'newest' or 'oldest'

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
            description: jobDescription,
            company: jobCompany,
        })
            .then(response => {
                setJobs([...jobs, response.data]);
                setJobTitle('');
                setJobStatus('');
                setJobDescription('');
                setJobCompany('');
            })
            .catch(error => {
                console.error("There was an error creating the job!", error);
            });
    };

    // Toggle the status of a job between 'Open' and 'Closed'
    const toggleStatus = (id, currentStatus) => {
        const newStatus = currentStatus === 'open' ? 'closed' : 'open';
        axios.put(`http://localhost:8080/api/jobs/${id}/status`, { status: newStatus })
            .then(response => {
                setJobs(jobs.map(job =>
                    job.id === id ? { ...job, status: response.data.status } : job
                ));
            })
            .catch(error => {
                console.error("There was an error updating the job status!", error);
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

    // Filter jobs based on search keyword
    const filteredJobs = jobs.filter(job => {
        const keyword = searchKeyword.toLowerCase();
        return (
            job.title.toLowerCase().includes(keyword) ||
            job.company.toLowerCase().includes(keyword) ||
            job.description.toLowerCase().includes(keyword)
        );
    });

    // Sort jobs based on date posted
    const sortedJobs = [...filteredJobs].sort((a, b) => {
        const dateA = new Date(a.datePosted);
        const dateB = new Date(b.datePosted);
        return sortOrder === 'newest' ? dateB - dateA : dateA - dateB;
    });

    return (
        <div className="App">
            <h1>Job Management</h1>

            {/* Create Job Form */}
            <div className="create-form">
                <input
                    type="text"
                    placeholder="Job Title"
                    value={jobTitle}
                    onChange={e => setJobTitle(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Job Status"
                    value={jobStatus}
                    onChange={e => setJobStatus(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Job Description"
                    value={jobDescription}
                    onChange={e => setJobDescription(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Company Name"
                    value={jobCompany}
                    onChange={e => setJobCompany(e.target.value)}
                />
                <button onClick={createJob}>Create Job</button>
            </div>

            {/* Search and Sort Controls */}
            <div className="controls">
                <input
                    type="text"
                    placeholder="Search jobs by title, company, or description..."
                    value={searchKeyword}
                    onChange={e => setSearchKeyword(e.target.value)}
                />
                <select
                    value={sortOrder}
                    onChange={e => setSortOrder(e.target.value)}
                >
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                </select>
            </div>

            <h2>All Jobs</h2>
            <ul
                style={{
                    textAlign: 'left',
                    listStyleType: 'none',
                    padding: 0,
                    margin: 0,             // make '0 auto' to center the job cards
                    maxWidth: '600px'
                }}
            >
                {sortedJobs.map(job => (
                    <li key={job.id} className="job-card">
                        <h3 className="job-title">{job.title}</h3>
                        <p>
                            <strong>Status:</strong>{' '}
                            <span className={`status-tag ${job.status.toLowerCase()}`}>
                {job.status}
              </span>
                        </p>
                        <p>
                            <strong>Company:</strong> {job.company}
                        </p>
                        <p>
                            <strong>Description:</strong> {job.description}
                        </p>
                        <p>
                            <strong>Posted On:</strong>{' '}
                            {new Date(job.datePosted).toLocaleString()}
                        </p>
                        <div className="job-actions">
                            <button onClick={() => toggleStatus(job.id, job.status)}>
                                Toggle Status
                            </button>
                            <button onClick={() => deleteJob(job.id)}>Delete</button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default App;
