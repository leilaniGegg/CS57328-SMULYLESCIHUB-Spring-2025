// frontend/src/App.js
import React, { useState } from 'react';
import FacultyPostJob from './components/FacultyPostJob';
import StudentFilterClasses from './components/StudentFilterClasses';

function App() {
    const [selected, setSelected] = useState('');

    return (
        <div>
            <h1>SMU TA Module</h1>
            <div>
                <button onClick={() => setSelected('faculty')}>
                    Faculty: Post TA Job
                </button>
                <button onClick={() => setSelected('student')}>
                    Student: Filter Classes
                </button>
            </div>
            <hr />
            {selected === 'faculty' && <FacultyPostJob />}
            {selected === 'student' && <StudentFilterClasses />}
        </div>
    );
}

export default App;
