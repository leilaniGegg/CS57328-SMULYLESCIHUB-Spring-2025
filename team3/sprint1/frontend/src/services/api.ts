import axios from "axios";
import Employee from "../components/Employee";

const API_URL: string = "http://localhost:8080/api/employees";  // Ensure this is correct

export const getEmployees = async () => {
    return await axios.get(API_URL);
};

export const addEmployee = async (employee: Employee) => {
    return await axios.post(API_URL, employee, {
        headers: {"Content-Type": "application/json"}, // Ensure correct headers
    });
};

export const deleteEmployee = async (id: number): Promise<void> => {
    return await axios.delete(`${API_URL}/${id}`);
};
