# Sprint 1

## 🏁 Sprint Goal
Build the foundational backend and frontend components of the application. Create form for a user to create a RA Job posting; once all fields are filled in and the user submits the job, the RAJob object is created and saved.

## ✅ Features Implemented
- Posting an RA Job to database
- RA Job Form
- Connected MySQL for user data storage

## 👥 Team Members
- Nabeela Khan
- Kiersten Wener
- Hamna Tameez
- Miles Youngblood

## 🧪 How to Run/Test Our Code

### Prerequisites
🔧 Frontend (React)
- Node.js installed
- npm or yarn

☕ Backend (Spring Boot)
- Java 17+ installed
- Gradle (or use the included ./gradlew)
- MySQL

### Steps to Run
1. Clone the repository:
   ```bash
   https://github.com/NahedAbdelgaber/CS57328-SMULYLESCIHUB-Spring-2025.git
   cd CS57328-SMULYLESCIHUB-Spring-2025/sprint1

2. Make sure your application.properties in backend/src/main/resources is configured for your MySQL database

3. Start the Backend (Spring Boot)
    ```bash
    cd backend
   ./gradlew bootRun

4. Start the Frontend (React)
    ```bash
   cd ../frontend
   npm install
   npm start

Note:

- The frontend will run on http://localhost:3000
- Ensure the API URL (http://localhost:8080/api/...) matches the backend
