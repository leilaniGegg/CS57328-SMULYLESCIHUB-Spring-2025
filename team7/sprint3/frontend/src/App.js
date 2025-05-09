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
    const [sortOrder, setSortOrder] = useState('newest');

    const [applicantInputs, setApplicantInputs] = useState({});
    const [showApplicants, setShowApplicants] = useState({});
    const [applicantsData, setApplicantsData] = useState({});

    // --- State for Authentication and Registration ---
    const [user, setUser] = useState(null); // Stores logged-in user: { id, name, role }
    const [loginName, setLoginName] = useState('');
    const [loginPassword, setLoginPassword] = useState('');
    const [loginError, setLoginError] = useState('');

    const [registerName, setRegisterName] = useState('');
    const [registerPassword, setRegisterPassword] = useState('');
    const [registerRole, setRegisterRole] = useState('student'); // Default to student
    const [registerError, setRegisterError] = useState('');
    const [showRegisterForm, setShowRegisterForm] = useState(false); // Toggle between login and register forms


    // Base URLs for backend APIs
    const API_BASE_URL = 'http://localhost:8080/api';
    const JOBS_API_URL = `${API_BASE_URL}/jobs`;
    const AUTH_API_URL = `${API_BASE_URL}/auth`;


    // Fetch jobs on initial load
    useEffect(() => {
        fetchJobs();
    }, []);

    // Include user ID in headers for authenticated requests
    const getAuthHeaders = () => {
        // --- Debug Log: Check user state inside header function ---
        console.log('User state inside getAuthHeaders:', user);
        // --- End Debug Log ---
        if (user && user.id) {
            return {
                'X-User-Id': user.id,
                'Content-Type': 'application/json' // Default content type
            };
        }
        return {}; // Return empty object if no user is logged in
    };

    // Include user ID in headers for authenticated file upload requests
    const getAuthMultipartHeaders = () => {
        // --- Debug Log: Check user state inside header function ---
        console.log('User state inside getAuthMultipartHeaders:', user);
        // --- End Debug Log ---
        if (user && user.id) {
            return {
                'X-User-Id': user.id,
                'Content-Type': 'multipart/form-data' // Content type for file uploads
            };
        }
        return {}; // Return empty object if no user is logged in
    };


    const fetchJobs = () => {
        // No auth header needed for fetching all jobs (accessible by anyone)
        axios.get(JOBS_API_URL)
            .then(response => {
                const jobsWithDefaultStatus = response.data.map(job => ({
                    ...job,
                    status: job.status || 'open'
                }));
                setJobs(jobsWithDefaultStatus);
            })
            .catch(error => console.error("There was an error fetching the jobs!", error));
    };

    const createJob = () => {
        // Check if user is logged in and is an employer
        if (!user || user.role !== 'employer') {
            alert("You must be logged in as an employer to create jobs.");
            return;
        }

        if (!jobTitle || !jobStatus || !jobDescription || !jobCompany) {
            alert("Please fill in all job details.");
            return;
        }

        axios.post(JOBS_API_URL, {
            title: jobTitle,
            status: jobStatus,
            description: jobDescription,
            company: jobCompany,
            // employerId will be set by the backend based on the authenticated user
        }, { headers: getAuthHeaders() }) // Include auth headers
            .then(response => {
                setJobs([...jobs, {...response.data, status: response.data.status || 'open'}]);
                setJobTitle('');
                setJobStatus('');
                setJobDescription('');
                setJobCompany('');
                alert("Job created successfully!"); // User feedback
            }).catch(error => {
            console.error("There was an error creating the job!", error);
            alert("Failed to create job. " + (error.response?.data?.message || error.message)); // Show backend error if available
        });
    };

    const toggleStatus = (id, currentStatus) => {
        // Check if user is logged in and is an employer
        if (!user || user.role !== 'employer') {
            alert("You must be logged in as an employer to change job status.");
            return;
        }

        const newStatus = currentStatus === 'open' ? 'closed' : 'open';
        axios.put(`${JOBS_API_URL}/${id}/status`, {status: newStatus}, { headers: getAuthHeaders() }) // Include auth headers
            .then(response => {
                setJobs(jobs.map(job =>
                    job.id === id ? {...job, status: response.data.status} : job
                ));
                alert("Job status updated successfully!"); // User feedback
            }).catch(error => {
            console.error("There was an error updating the job status!", error);
            alert("Failed to update job status. " + (error.response?.data?.message || error.message));
        });
    };

    const deleteJob = (id) => {
        // Check if user is logged in and is an employer
        if (!user || user.role !== 'employer') {
            alert("You must be logged in as an employer to delete jobs.");
            return;
        }

        if (window.confirm("Are you sure you want to delete this job?")) {
            axios.delete(`${JOBS_API_URL}/${id}`, { headers: getAuthHeaders() }) // Include auth headers
                .then(() => {
                    setJobs(jobs.filter(job => job.id !== id));
                    alert("Job deleted successfully!"); // User feedback
                }).catch(error => {
                console.error("There was an error deleting the job!", error);
                alert("Failed to delete job. " + (error.response?.data?.message || error.message));
            });
        }
    };

    const applyToJob = (jobId) => {
        // Check if user is logged in and is a student
        if (!user || user.role !== 'student') {
            alert("You must be logged in as a student to apply for jobs.");
            return;
        }

        const input = applicantInputs[jobId];

        if (!input?.name || !input?.resumeFile) {
            console.log("Application requires both name and resume file.");
            alert("Please enter your name and attach a resume."); // User feedback
            return;
        }

        const formData = new FormData();
        formData.append("name", input.name);
        formData.append("resume", input.resumeFile);

        // --- Debug Log: Check headers before sending ---
        console.log("Applying to job:", jobId);
        console.log("User state (before headers):", user); // Keep this log
        console.log("Auth Headers for Apply (generated):", getAuthMultipartHeaders()); // Keep this log
        // --- End Debug Log ---


        axios.post(`${JOBS_API_URL}/${jobId}/apply`, formData, {
            headers: getAuthMultipartHeaders() // Use multipart headers for file upload
        }).then(() => {
            alert("Application submitted successfully!"); // User feedback
            // Clear application inputs for this job after successful submission
            setApplicantInputs(prev => ({
                ...prev,
                [jobId]: { name: '', resumeFile: null }
            }));

        }).catch(err => {
            console.error("Error applying to job", err);
            alert("Failed to submit application. " + (err.response?.data?.message || err.message)); // Show backend error if available
        });
    };

    const fetchApplicants = (jobId) => {
        // Check if user is logged in and is an employer
        if (!user || user.role !== 'employer') {
            alert("You must be logged in as an employer to view applicants.");
            setShowApplicants(prev => ({ ...prev, [jobId]: false })); // Hide if not authorized
            return;
        }

        // Always fetch applicants when toggling to show, to get the latest list
        axios.get(`${JOBS_API_URL}/${jobId}/applicants`, { headers: getAuthHeaders() }) // Include auth headers
            .then(res => {
                setApplicantsData(prev => ({ ...prev, [jobId]: res.data }));
            }).catch(err => {
            console.error("Error fetching applicants", err);
            alert("Failed to fetch applicants. " + (err.response?.data?.message || err.message));
            setShowApplicants(prev => ({ ...prev, [jobId]: false })); // Hide on error
        });
    };

    // --- Authentication Logic ---
    const handleLogin = () => {
        setLoginError(''); // Clear previous errors
        axios.post(`${AUTH_API_URL}/login`, {
            name: loginName,
            password: loginPassword
        }).then(response => {
            // Assuming backend returns user object { id, name, role } on success
            setUser(response.data);
            // --- Debug Log: Check user state after login ---
            console.log("User logged in:", response.data);
            // --- End Debug Log ---
            setLoginName('');
            setLoginPassword('');
            setLoginError(''); // Clear error on success
            alert(`Logged in as ${response.data.name} (${response.data.role})`); // User feedback
        }).catch(error => {
            console.error("Login error", error);
            setLoginError(error.response?.data?.message || "Login failed. Please check your credentials."); // Show backend error or default
        });
    };

    const handleLogout = () => {
        setUser(null);
        setLoginName('');
        setLoginPassword('');
        setLoginError('');
        // Clear registration form fields and errors on logout
        setRegisterName('');
        setRegisterPassword('');
        setRegisterRole('student');
        setRegisterError('');
        setShowRegisterForm(false); // Hide register form on logout
        // Clear any role-specific state like showApplicants/applicantsData if necessary
        setShowApplicants({});
        setApplicantsData({});
        alert("Logged out"); // User feedback
    };

    // --- Registration Logic ---
    const handleRegister = () => {
        setRegisterError(''); // Clear previous errors
        // Basic frontend validation
        if (!registerName || !registerPassword || !registerRole) {
            setRegisterError("Please fill in all fields.");
            return;
        }

        axios.post(`${AUTH_API_URL}/register`, {
            name: registerName,
            password: registerPassword,
            role: registerRole
        }).then(response => {
            alert(`Account created successfully for ${response.data.name} (${response.data.role}). You can now log in.`); // User feedback
            // Optionally log in the user automatically after registration
            // setUser(response.data);
            // setLoginName(registerName); // Pre-fill login form
            // setLoginPassword(''); // Don't pre-fill password for security
            // setShowRegisterForm(false); // Switch back to login form

            // Clear registration form
            setRegisterName('');
            setRegisterPassword('');
            setRegisterRole('student');
            setRegisterError('');
            setShowRegisterForm(false); // Switch back to login form
        }).catch(error => {
            console.error("Registration error", error);
            setRegisterError(error.response?.data?.message || "Registration failed. Please try again."); // Show backend error or default
        });
    };


    // Filter jobs based on search keyword
    const filteredJobs = jobs.filter(job => {
        const keyword = searchKeyword.toLowerCase();
        return (
            (job.title?.toLowerCase() || '').includes(keyword) ||
            (job.company?.toLowerCase() || '').includes(keyword) ||
            (job.description?.toLowerCase() || '').includes(keyword)
        );
    });

    // Sort filtered jobs
    const sortedJobs = [...filteredJobs].sort((a, b) => {
        const dateA = a.datePosted ? new Date(a.datePosted) : new Date(0);
        const dateB = b.datePosted ? new Date(b.datePosted) : new Date(0);

        if (sortOrder === 'newest') {
            return dateB.getTime() - dateA.getTime();
        } else {
            return dateA.getTime() - dateB.getTime();
        }
    });

    return (
        <div className="App">
            <h1>Job Management</h1>

            {/* --- Authentication Section (Login/Register/Logout) --- */}
            <div className="auth-section">
                {user ? (
                    // Logged-in state
                    <div>
                        Logged in as <strong>{user.name}</strong> ({user.role})
                        <button onClick={handleLogout} style={{ marginLeft: '10px' }}>Logout</button>
                    </div>
                ) : (
                    // Logged-out state (Show Login or Register form)
                    <div>
                        {showRegisterForm ? (
                            // Registration Form
                            <div>
                                <h2>Register Account</h2>
                                <input
                                    type="text"
                                    placeholder="Name"
                                    value={registerName}
                                    onChange={e => setRegisterName(e.target.value)}
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={registerPassword}
                                    onChange={e => setRegisterPassword(e.target.value)}
                                />
                                <select value={registerRole} onChange={e => setRegisterRole(e.target.value)}>
                                    <option value="student">Student</option>
                                    <option value="employer">Employer</option>
                                </select>
                                <button onClick={handleRegister}>Create Account</button>
                                {registerError && <p style={{ color: 'red' }}>{registerError}</p>}
                                <p>Already have an account? <button onClick={() => setShowRegisterForm(false)}>Login here</button></p>
                            </div>
                        ) : (
                            // Login Form
                            <div>
                                <h2>Login</h2>
                                <input
                                    type="text"
                                    placeholder="Name"
                                    value={loginName}
                                    onChange={e => setLoginName(e.target.value)}
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={loginPassword}
                                    onChange={e => setLoginPassword(e.target.value)}
                                />
                                <button onClick={handleLogin}>Login</button>
                                {loginError && <p style={{ color: 'red' }}>{loginError}</p>}
                                <p>Don't have an account? <button onClick={() => setShowRegisterForm(true)}>Register here</button></p>
                            </div>
                        )}
                    </div>
                )}
            </div>
            {/* --- End Authentication Section --- */}

            {/* --- Job Creation Form (Only for Employers) --- */}
            {user && user.role === 'employer' && (
                <div className="create-form">
                    <h2>Create New Job</h2>
                    <input type="text" placeholder="Job Title" value={jobTitle}
                           onChange={e => setJobTitle(e.target.value)}/>
                    <input type="text" placeholder="Job Status (e.g., open, closed)" value={jobStatus}
                           onChange={e => setJobStatus(e.target.value)}/>
                    <input type="text" placeholder="Job Description" value={jobDescription}
                           onChange={e => setJobDescription(e.target.value)}/>
                    <input type="text" placeholder="Company Name" value={jobCompany}
                           onChange={e => setJobCompany(e.target.value)}/>
                    <button onClick={createJob}>Create Job</button>
                </div>
            )}
            {/* --- End Job Creation Form --- */}


            {/* Search and Sort (Visible to all) */}
            <div className="controls">
                <h2>Job Listings</h2>
                <input type="text" placeholder="Search jobs..." value={searchKeyword}
                       onChange={e => setSearchKeyword(e.target.value)}/>
                <select value={sortOrder} onChange={e => setSortOrder(e.target.value)}>
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                </select>
            </div>


            {/* Job List (Visible to all) */}
            <ul className="job-list">
                {sortedJobs.map(job => (
                    <li key={job.id} className="job-card">
                        <h3>{job.title}</h3>
                        <p><strong>Status:</strong> <span
                            className={`status-tag ${job.status?.toLowerCase()}`}>{job.status || 'Unknown'}</span></p>
                        <p><strong>Company:</strong> {job.company}</p>
                        <p><strong>Description:</strong> {job.description}</p>
                        {job.datePosted && (
                            <p><strong>Posted:</strong> {new Date(job.datePosted).toLocaleString()}</p>
                        )}

                        {/* --- Application Form (Only for Students) --- */}
                        {user && user.role === 'student' && (
                            <div className="apply-form">
                                <h4>Apply to this job</h4>
                                <input
                                    type="text"
                                    placeholder="Your Name"
                                    value={applicantInputs[job.id]?.name || ''}
                                    onChange={e =>
                                        setApplicantInputs(prev => ({
                                            ...prev,
                                            [job.id]: { ...prev[job.id], name: e.target.value }
                                        }))
                                    }
                                />
                                <input
                                    type="file"
                                    onChange={e =>
                                        setApplicantInputs(prev => ({
                                            ...prev,
                                            [job.id]: { ...prev[job.id], resumeFile: e.target.files?.[0] || null }
                                        }))
                                    }
                                />
                                <button
                                    onClick={() => applyToJob(job.id)}
                                    disabled={!applicantInputs[job.id]?.name || !applicantInputs[job.id]?.resumeFile}
                                >
                                    Submit Application
                                </button>
                            </div>
                        )}
                        {/* --- End Application Form --- */}


                        {/* --- Applicant List Toggle and Display (Only for Employers) --- */}
                        {user && user.role === 'employer' && (
                            <button onClick={() => {
                                const toggle = !showApplicants[job.id];
                                setShowApplicants(prev => ({ ...prev, [job.id]: toggle }));
                                // Always fetch applicants when toggling to show, to get the latest list
                                if (toggle) {
                                    fetchApplicants(job.id);
                                }
                            }}>
                                {showApplicants[job.id] ? "Hide Applicants" : "View Applicants"}
                            </button>
                        )}


                        {showApplicants[job.id] && user && user.role === 'employer' && (
                            <div className="applicant-list">
                                <h4>Applicants:</h4>
                                {!applicantsData[job.id] ? (
                                    <p>Loading applicants...</p>
                                ) : applicantsData[job.id].length === 0 ? (
                                    <p>No applicants yet.</p>
                                ) : (
                                    <ul>
                                        {applicantsData[job.id].map((app, idx) => (
                                            <li key={idx}>
                                                <strong>{app.name}</strong>
                                                {app.resumeFileName && (
                                                    <span> - {app.resumeFileName}</span>
                                                )}
                                                {app.resumeFileName && (
                                                    <>
                                                        {' '}
                                                        <a
                                                            href={`http://localhost:8080/api/jobs/resumes/${app.resumeFileName}`}
                                                            target="_blank"
                                                            rel="noopener noreferrer"
                                                            download
                                                        >
                                                            (Download)
                                                        </a>
                                                    </>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        )}
                        {/* --- End Applicant List --- */}


                        {/* --- Job Actions (Toggle Status, Delete - Only for Employers) --- */}
                        {user && user.role === 'employer' && (
                            <div className="job-actions">
                                <button onClick={() => toggleStatus(job.id, job.status)}>Toggle Status</button>
                                <button onClick={() => deleteJob(job.id)}>Delete</button>
                            </div>
                        )}
                        {/* --- End Job Actions --- */}

                    </li>
                ))}
                {sortedJobs.length === 0 && jobs.length > 0 && <p>No jobs found matching your search.</p>}
                {sortedJobs.length === 0 && jobs.length === 0 && <p>No jobs available. Create one above!</p>}

            </ul>
        </div>
    );
}

export default App;
