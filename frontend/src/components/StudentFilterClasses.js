// frontend/src/components/StudentFilterClasses.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const StudentFilterClasses = () => {
    const [courseNumber, setCourseNumber] = useState('');
    const [courseName, setCourseName] = useState('');
    const [courses, setCourses] = useState([]);

    const fetchCourses = async () => {
        try {
            // Build query parameters
            let url = 'http://localhost:8081/api/courses?';
            if (courseNumber) {
                url += `courseNumber=${courseNumber}&`;
            }
            if (courseName) {
                url += `courseName=${courseName}&`;
            }
            // add skill/standing filters

            const response = await axios.get(url);
            setCourses(response.data);
        } catch (error) {
            console.error(error);
            alert('Error fetching courses.');
        }
    };

    useEffect(() => {
        // You could auto-fetch or let the user click a button
        fetchCourses();
        // eslint-disable-next-line
    }, []);

    const handleFilter = () => {
        fetchCourses();
    };

    return (
        <div>
            <h2>Filter Classes</h2>
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
            <button onClick={handleFilter}>Filter</button>

            <div>
                <h3>Available Courses:</h3>
                <ul>
                    {courses.map(course => (
                        <li key={course.courseNumber}>
                            {course.courseNumber} - {course.courseName} : {course.description}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default StudentFilterClasses;
