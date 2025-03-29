// frontend/src/components/StudentFilterClasses.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const StudentFilterClasses = () => {
    const [courseNumber, setCourseNumber] = useState('');
    const [courseName, setCourseName] = useState('');
    const [skill, setSkill] = useState('');               // new filter input for skill
    const [instructorName, setInstructorName] = useState(''); // new filter input for instructor name
    const [standing, setStanding] = useState('');           // new filter input for standing
    const [jobPostings, setJobPostings] = useState([]);

    const fetchJobPostings = async () => {
        try {
            let url = 'http://localhost:8081/api/jobpostings?';
            if (courseNumber) {
                url += `courseNumber=${courseNumber}&`;
            }
            if (courseName) {
                url += `courseName=${courseName}&`;
            }
            if (skill) {
                url += `skill=${skill}&`;
            }
            if (instructorName) {
                url += `instructorName=${instructorName}&`;
            }
            if (standing) {
                url += `standing=${standing}&`;
            }
            const response = await axios.get(url);
            setJobPostings(response.data);
        } catch (error) {
            console.error(error);
            alert('Error fetching job postings.');
        }
    };

    useEffect(() => {
        fetchJobPostings();
    }, []);

    const handleFilter = () => {
        fetchJobPostings();
    };

    return (
        <div>
            <h2>Filter TA Job Postings</h2>
            <div>
                <label>Course Number:</label>
                <input
                    type="text"
                    value={courseNumber}
                    onChange={(e) => setCourseNumber(e.target.value)}
                />
            </div>
            <div>
                <label>Course Name:</label>
                <input
                    type="text"
                    value={courseName}
                    onChange={(e) => setCourseName(e.target.value)}
                />
            </div>
            <div>
                <label>Skill:</label>
                <input
                    type="text"
                    value={skill}
                    onChange={(e) => setSkill(e.target.value)}
                />
            </div>
            <div>
                <label>Instructor Name:</label>
                <input
                    type="text"
                    value={instructorName}
                    onChange={(e) => setInstructorName(e.target.value)}
                />
            </div>
            <div>
                <label>Standing (Freshman, Sophmore, Junior, Senior, Graduate):</label>
                <input
                    type="text"
                    value={standing}
                    onChange={(e) => setStanding(e.target.value)}
                />
            </div>
            <button onClick={handleFilter}>Filter</button>

            <div>
                <h3>Available TA Job Postings:</h3>
                <ul>
                    {jobPostings.map(posting => (
                        <li key={posting.jobid}>
                            {posting.course.courseNumber} - {posting.course.courseName} : {posting.jobDetails}
                            <br />
                            Faculty: {posting.facultyName} (Email: {posting.facultyEmail})
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default StudentFilterClasses;
