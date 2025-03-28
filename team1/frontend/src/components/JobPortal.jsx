import React, { useState } from 'react';

const JobPortal = () => {
    const [companyName, setCompanyName] = useState('');
    const [jobDescription, setJobDescription] = useState('');
    const [jobs, setJobs] = useState([]);
    const [showJobs, setShowJobs] = useState(false);

    const handleSubmit = () => {
        // Only add job if both fields are filled
        if (companyName.trim() && jobDescription.trim()) {
            const newJob = {
                id: Date.now(),
                companyName: companyName,
                jobDescription: jobDescription
            };

            setJobs([...jobs, newJob]);

            // Clear input fields
            setCompanyName('');
            setJobDescription('');
        }
    };

    const handleShowJobs = () => {
        setShowJobs(true);
    };

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Job Submission Portal</h1>

            <div className="mb-4">
                <label htmlFor="companyName" className="block mb-2 font-medium">
                    Company Name
                </label>
                <input
                    type="text"
                    id="companyName"
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <div className="mb-4">
                <label htmlFor="jobDescription" className="block mb-2 font-medium">
                    Job Description
                </label>
                <textarea
                    id="jobDescription"
                    value={jobDescription}
                    onChange={(e) => setJobDescription(e.target.value)}
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="4"
                />
            </div>

            <div className="flex space-x-4 mb-6">
                <button
                    onClick={handleSubmit}
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    Submit Job
                </button>

                <button
                    onClick={handleShowJobs}
                    className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-500"
                >
                    Return Available Jobs
                </button>
            </div>

            {showJobs && (
                <div className="mt-6">
                    <h2 className="text-xl font-bold mb-3">Available Jobs</h2>
                    {jobs.length === 0 ? (
                        <p>No jobs have been submitted yet.</p>
                    ) : (
                        <div className="space-y-4">
                            {jobs.map((job) => (
                                <div key={job.id} className="p-4 border rounded bg-gray-50">
                                    <h3 className="font-bold">{job.companyName}</h3>
                                    <p className="mt-2">{job.jobDescription}</p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default JobPortal;