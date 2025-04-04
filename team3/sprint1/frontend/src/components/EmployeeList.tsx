import React, {useEffect, useState} from "react";
import {getEmployees, addEmployee, deleteEmployee} from "../services/api";
import Employee from "./Employee";

const EmployeeList: React.FC = () => {
    const [employees, setEmployees] = useState([]);

    useEffect((): void => {
        fetchEmployees().then();
    }, []);

    const fetchEmployees = async (): Promise<void> => {
        const response = await getEmployees();
        setEmployees(response.data);
    };

    const handleAddEmployee = async (): Promise<void> => {
        const newEmployee: Employee = {name: "John Doe", salary: 50000};
        await addEmployee(newEmployee);
        await fetchEmployees();
    };

    const handleDelete = async (id: number): Promise<void> => {
        await deleteEmployee(id);
        await fetchEmployees();
    };

    return (
        <div>
            <h2>Employee List</h2>
            <button onClick={handleAddEmployee}>Add Employee</button>
            <ul>
                {employees.map((employee: Employee) => (
                    <li key={employee.id}>
                        {employee.name} - ${employee.salary}
                        <button onClick={() => employee.id && handleDelete(employee.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default EmployeeList;
