# Sprint 1: Team 7 Job Management

## Description

In this sprint, we created a basic job management system built with a React frontend and a Spring Boot backend. It allows users to create, update, and delete job listings.

## Tech Stack

- **Frontend**: React, Axios
- **Backend**: Spring Boot, Java
- **Database**: [cshub, MySQL]
- **Build Tools**: Gradle, npm

## Features

- Create, update, and delete job listings
- View a list of job postings
- API endpoints to manage job data



## Steps to Run

### Backend

1. Navigate to the backend directory:
    ```bash
    cd backend
    ```
2. Build the project with Gradle:
    ```bash
    ./gradlew build
    ```
4. Run the Spring Boot application:
    ```bash
    ./gradlew bootRun
    ```
   The backend should now be running on `http://localhost:8080`.

### Frontend

1. Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

2. Start the React application:
    ```bash
    npm start
    ```
   The frontend should now be running on `http://localhost:3000`.

## API Endpoints

### Get all jobs
- **URL**: `/api/jobs`
- **Method**: `GET`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "title": "Software Engineer",
      "status": "Open"
    },
    ...
  ]
