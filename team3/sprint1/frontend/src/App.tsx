import React, {JSX} from "react";
import EmployeeList from "./components/EmployeeList";
import RAJobForm from "./components/RAJobForm"; // Ensure correct path

function App(): JSX.Element {
    return (
        <div>
            <h1>RA Job Form</h1>
            <RAJobForm/>
        </div>
    );
}

export default App;
