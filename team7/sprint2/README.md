# Sprint 2: Team 7 Job Management

## Description

In this sprint, we enhanced the job management system by adding new features to improve job searching and job listing organization. Users can now search job listings by keyword, toggle job status between open and closed, and sort listings by the date posted. Additionally, we made visual improvements to the frontend for a more user-friendly and professional appearance.

## Tech Stack

- **Frontend**: React, Axios, CSS
- **Backend**: Spring Boot, Java
- **Database**: [cshub, MySQL]
- **Build Tools**: Gradle, npm

## New Features

- **Search by Keyword**: Users can search for jobs by title, company, or description.
- **Toggle Job Status**: Users can toggle a job's status between "Open" and "Closed".
- **Sort Jobs**: Users can sort job listings by date posted (newest to oldest, or oldest to newest).
- **Improved UI**: Enhanced the frontend with better spacing, alignment, and color schemes for a more polished look.

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

### Get All Jobs
- **URL**: `/api/jobs`
- **Method**: `GET`
- **Description**: This endpoint returns a list of all job postings, including job titles, statuses, and timestamps for when the jobs were posted.

- **Response**:
  ```json
  [
    {
      "id": 1,
      "title": "Software Engineer",
      "status": "Open",
      "datePosted": "2025-04-25T12:00:00"
    },
    ...
  ]
