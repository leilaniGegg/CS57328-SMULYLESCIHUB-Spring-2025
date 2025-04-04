import React, { useState } from "react";

// Wrapper that centers the form container
const wrapperStyle: React.CSSProperties = {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#010a2d", // Deep navy background like NASA
    padding: "20px",
};

const formContainerStyle: React.CSSProperties = {
    backgroundColor: "#d6f8ff", // NASA navy
    color: "#010a2d",
    padding: "40px",
    borderRadius: "16px",
    width: "100%",
    maxWidth: "700px",
    boxShadow: "0 12px 30px rgba(0, 0, 0, 0.3)",
    fontFamily: "Helvetica, Arial, sans-serif",
};

const labelStyle: React.CSSProperties = {
    display: "block",
    marginBottom: "6px",
    fontWeight: 600,
    fontSize: "14px",
    color: "#010a2d",
};

const inputStyle: React.CSSProperties = {
    width: "100%",
    padding: "12px 5px",
    marginBottom: "24px",
    borderRadius: "8px",
    border: "1px solid #ccc",
    fontSize: "14px",
};

const buttonStyle: React.CSSProperties = {
    padding: "12px 24px",
    backgroundColor: "#ff6f00", // NASA accent
    color: "#ffffff",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    cursor: "pointer",
    fontWeight: "bold",
};

const RAJobForm: React.FC = () => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [status, setStatus] = useState("");
    const [updateTime, setUpdateTime] = useState("");
    const [department, setDepartment] = useState("");
    const [location, setLocation] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [timeCommitment, setTimeCommitment] = useState("");
    const [paid, setPaid] = useState(false);
    const [stipendAmount, setStipendAmount] = useState("");
    const [preferredMajors, setPreferredMajors] = useState("");
    const [skillsRequired, setSkillsRequired] = useState("");

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        const jobData = {
            title,
            description,
            status,
            updateTime: `${updateTime}:00`,
            department,
            location,
            startDate,
            endDate,
            timeCommitment,
            paid,
            stipendAmount: paid ? Number(stipendAmount) : 0,
            preferredMajors: preferredMajors.split(",").map((s) => s.trim()),
            skillsRequired: skillsRequired.split(",").map((s) => s.trim()),
        };

        try {
            const response = await fetch("http://localhost:8080/api/jobs", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(jobData),
            });

            if (response.ok) {
                alert("RA Job submitted successfully!");
                setTitle("");
                setDescription("");
                setStatus("");
                setUpdateTime("");
                setDepartment("");
                setLocation("");
                setStartDate("");
                setEndDate("");
                setTimeCommitment("");
                setPaid(false);
                setStipendAmount("");
                setPreferredMajors("");
                setSkillsRequired("");
            } else {
                alert("Failed to submit job.");
            }
        } catch (error) {
            console.error("Error submitting job:", error);
        }
    };

    return (
        <div style={wrapperStyle}>
            <form onSubmit={handleSubmit} style={formContainerStyle}>
                <h2 style={{ textAlign: "center", marginBottom: "30px", fontWeight: "bold", fontSize: "24px" }}>
                    Post RA Job Opening
                </h2>

                <label style={labelStyle}>Title</label>
                <input style={inputStyle} type="text" value={title} onChange={(e) => setTitle(e.target.value)} required />

                <label style={labelStyle}>Description</label>
                <textarea style={{ ...inputStyle, height: "100px" }} value={description} onChange={(e) => setDescription(e.target.value)} required />

                <label style={labelStyle}>Status</label>
                <select style={inputStyle} value={status} onChange={(e) => setStatus(e.target.value)} required>
                    <option value="">Select Status</option>
                    <option value="OPEN">Open</option>
                    <option value="CLOSED">Closed</option>
                </select>

                <label style={labelStyle}>Update Time</label>
                <input style={inputStyle} type="time" value={updateTime} onChange={(e) => setUpdateTime(e.target.value)} required />

                <label style={labelStyle}>Department</label>
                <input style={inputStyle} type="text" value={department} onChange={(e) => setDepartment(e.target.value)} />

                <label style={labelStyle}>Location</label>
                <input style={inputStyle} type="text" value={location} onChange={(e) => setLocation(e.target.value)} />

                <label style={labelStyle}>Start Date</label>
                <input style={inputStyle} type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />

                <label style={labelStyle}>End Date</label>
                <input style={inputStyle} type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />

                <label style={labelStyle}>Time Commitment (e.g. 10 hrs/week)</label>
                <input style={inputStyle} type="text" value={timeCommitment} onChange={(e) => setTimeCommitment(e.target.value)} />

                <label style={labelStyle}>Paid Position?</label>
                <div style={{ marginBottom: "20px" }}>
                    <input type="checkbox" checked={paid} onChange={(e) => setPaid(e.target.checked)} />
                </div>

                {paid && (
                    <>
                        <label style={labelStyle}>Stipend Amount ($)</label>
                        <input style={inputStyle} type="number" value={stipendAmount} onChange={(e) => setStipendAmount(e.target.value)} />
                    </>
                )}

                <label style={labelStyle}>Preferred Majors (comma-separated)</label>
                <input style={inputStyle} type="text" value={preferredMajors} onChange={(e) => setPreferredMajors(e.target.value)} />

                <label style={labelStyle}>Skills Required (comma-separated)</label>
                <input style={inputStyle} type="text" value={skillsRequired} onChange={(e) => setSkillsRequired(e.target.value)} />

                <div style={{ textAlign: "center" }}>
                    <button type="submit" style={buttonStyle}>Submit</button>
                </div>
            </form>
        </div>
    );
};

export default RAJobForm;
