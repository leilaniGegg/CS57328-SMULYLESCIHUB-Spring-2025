// frontend/src/services/api.js
import axios from "axios";

const JOB_POSTING_API = "http://localhost:8080/api/jobpostings";
const COURSE_API = "http://localhost:8080/api/courses";

export const postJob = async (jobData) => {
    return await axios.post(JOB_POSTING_API, jobData, {
        headers: { "Content-Type": "application/json" },
    });
};

export const getCourses = async (filter) => {
    let url = COURSE_API;
    if(filter) {
        url += `?courseName=${filter}`;
    }
    return await axios.get(url);
};
