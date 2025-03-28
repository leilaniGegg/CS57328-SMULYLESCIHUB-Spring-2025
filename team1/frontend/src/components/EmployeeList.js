import React, { useEffect, useState } from "react";
import { getEmployees, addEmployee, deleteEmployee } from "../services/api";

const EmployeeList = () => {
   const [employees, setEmployees] = useState([]);

   useEffect(() => {
       fetchEmployees();
   }, []);

   const fetchEmployees = async () => {
       const response = await getEmployees();
       setEmployees(response.data);
   };

   const handleAddEmployee = async () => {
       const newEmployee = { name: "John Doe", salary: 50000 };
       await addEmployee(newEmployee);
       fetchEmployees();
   };

   const handleDelete = async (id) => {
       await deleteEmployee(id);
       fetchEmployees();
   };

   return (
       <div>
           <h2>Employee List</h2>
           <button onClick={handleAddEmployee}>Add Employee</button>
           <ul>
               {employees.map((employee) => (
                   <li key={employee.id}>
                       {employee.name} - ${employee.salary}
                       <button onClick={() => handleDelete(employee.id)}>Delete</button>
                   </li>
               ))}
           </ul>
       </div>
   );
};

export default EmployeeList;

