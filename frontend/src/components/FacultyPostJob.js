// frontend/src/components/FacultyPostJob.js
import React, { useState } from 'react';
import axios from 'axios';

const FacultyPostJob = () => {
    const [facultyName, setFacultyName] = useState('');
    const [courseNumber, setCourseNumber] = useState('');
    const [requiredCourses, setRequiredCourses] = useState('');
    const [skills, setSkills] = useState('');
    const [selectedStandings, setSelectedStandings] = useState([]);
    const [jobDetails, setJobDetails] = useState('');

    const standingsOptions = ['Freshman', 'Sophomore', 'Junior', 'Senior', 'Graduate'];

    const handleStandingChange = (e) => {
        const value = e.target.value;
        if (selectedStandings.includes(value)) {
            // remove it
            setSelectedStandings(selectedStandings.filter(s => s !== value));
        } else {
            // add it
            setSelectedStandings([...selectedStandings, value]);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const requiredCoursesArray = requiredCourses.split(',').map(str => ({
            courseNumber: str.trim()
        }));

        const jobData = {
            facultyName,
            course: { courseNumber },
            requiredCourses: requiredCoursesArray,
            skills,
            standings: selectedStandings,
            jobDetails,
            createdDate: new Date() // let backend convert if necessary
        };

        try {
            await axios.post('http://localhost:8081/api/jobpostings', jobData, {
                headers: { "Content-Type": "application/json" }
            });
            alert('Job posted successfully!');
        } catch (error) {
            console.error(error);
            alert('Error posting job.');
        }
    };

    return (
        <div>
            <h2>Post TA Job</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Faculty Name:</label><br />
                    <input
                        type="text"
                        value={facultyName}
                        onChange={(e) => setFacultyName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Course Number:</label><br />
                    <input
                        type="text"
                        value={courseNumber}
                        onChange={(e) => setCourseNumber(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Required Courses (comma-separated):</label><br />
                    <input
                        type="text"
                        value={requiredCourses}
                        onChange={(e) => setRequiredCourses(e.target.value)}
                        placeholder="e.g. CS3341, MATH1338"
                    />
                </div>
                <div>
                    <label>Skills (comma-separated or free text):</label><br />
                    <input
                        type="text"
                        value={skills}
                        onChange={(e) => setSkills(e.target.value)}
                    />
                </div>
                <div>
                    <label>Standing (select all that apply):</label><br />
                    {standingsOptions.map(opt => (
                        <div key={opt}>
                            <input
                                type="checkbox"
                                value={opt}
                                checked={selectedStandings.includes(opt)}
                                onChange={handleStandingChange}
                            />
                            {opt}
                        </div>
                    ))}
                </div>
                <div>
                    <label>Job Details:</label><br />
                    <textarea
                        value={jobDetails}
                        onChange={(e) => setJobDetails(e.target.value)}
                        required
                    />
                </div>
                <br />
                <button type="submit">Submit Job Posting</button>
            </form>
        </div>
    );
};

export default FacultyPostJob;
