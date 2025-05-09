# Sprint 3: Team 7 Job Management

## Description

In this sprint, we significantly enhanced the job management system by introducing a user authentication and authorization system. Users can now register and log in as either a **Student** or an **Employer**. This enables role-based access to features: Employers can create and manage job listings and view applicants, while Students can apply for jobs. Both user types can view the general job listings. We also implemented functionality for uploading and downloading applicant resumes and added backend test cases.

## Tech Stack

* **Frontend**: React, Axios, CSS

* **Backend**: Spring Boot, Java

* **Database**: \[cshub, MySQL\]

* **Build Tools**: Gradle, npm

* **Testing**: JUnit 5, Spring Boot Test

## New Features

* **User Authentication**: Users can register and log in with distinct accounts.

* **User Roles**: Accounts have roles: "student" or "employer".

* **Role-Based Access Control**:

    * **Employers**: Can create new job listings, update job status, delete their own jobs, and view applicants for their jobs.

    * **Students**: Can apply for job listings.

    * **All Users (Logged in or Out)**: Can view the general list of available job listings.

* **Resume Upload**: Students can upload a resume file when applying for a job.

* **Resume Download**: Employers can download the uploaded resume files for applicants to their jobs.

* **Backend Test Cases**: Added JUnit 5 tests to verify backend API functionality and authorization rules.

## Steps to Run

### Backend

1.  Navigate to the backend directory:

    ```bash
    cd backend

    ```

2.  Build the project with Gradle:

    ```bash
    ./gradlew build

    ```

3.  Run the Spring Boot application:

    ```bash
    ./gradlew bootRun

    ```

    The backend should now be running on `http://localhost:8080`. Initial "Employer User" and "Student User" accounts are created in-memory on startup.

### Frontend

1.  Navigate to the frontend directory:

    ```bash
    cd frontend

    ```

2.  Start the React application:

    ```bash
    npm start

    ```

    The frontend should now be running on `http://localhost:3000`.

## Running Tests

To run the backend unit and integration tests, navigate to the backend directory in your terminal and execute:

```bash
./gradlew test
```


## API Endpoints

The backend provides the following API endpoints. Some endpoints now require authentication via the `X-User-Id` header and enforce role-based access.

### Authentication Endpoints

* **Register User**
    * **URL**: `/api/auth/register`
    * **Method**: `POST`
    * **Description**: Registers a new user account.
    * **Request Body**:
        ```json
        {
          "name": "newusername",
          "password": "userpassword",
          "role": "student"
        }
        ```
    * **Response**: `201 Created` on success, `400 Bad Request` for invalid data, `409 Conflict` if username exists.

* **Login User**
    * **URL**: `/api/auth/login`
    * **Method**: `POST`
    * **Description**: Logs in an existing user.
    * **Request Body**:
        ```json
        {
          "name": "username",
          "password": "userpassword"
        }
        ```
    * **Response**: `200 OK` with user info (`id`, `name`, `role`) on success, `401 Unauthorized` for incorrect credentials or user not found.

### Job Endpoints

* **Get All Jobs**
    * **URL**: `/api/jobs`
    * **Method**: `GET`
    * **Description**: Returns a list of all job postings. Accessible by anyone.
    * **Response**: `200 OK` with a list of jobs.

* **Create Job**
    * **URL**: `/api/jobs`
    * **Method**: `POST`
    * **Description**: Creates a new job posting. **Requires Employer role.**
    * **Headers**: `X-User-Id: [Employer User ID]`
    * **Request Body**:
        ```json
        {
          "title": "New Job Title",
          "status": "open",
          "description": "Job description...",
          "company": "Company Name"
        }
        ```
    * **Response**: `201 Created` on success, `401 Unauthorized` if not logged in, `403 Forbidden` if not an employer.

* **Update Job Status**
    * **URL**: `/api/jobs/{id}/status`
    * **Method**: `PUT`
    * **Description**: Updates the status of a specific job. **Requires Employer role and ownership of the job.**
    * **Headers**: `X-User-Id: [Employer User ID]`
    * **URL Parameters**: `id` (Job ID)
    * **Request Body**:
        ```json
        {
          "status": "closed"
        }
        ```
    * **Response**: `200 OK` with updated job, `401 Unauthorized` if not logged in, `403 Forbidden` if not the job owner, `404 Not Found` if job doesn't exist.

* **Delete Job**
    * **URL**: `/api/jobs/{id}`
    * **Method**: `DELETE`
    * **Description**: Deletes a specific job posting. **Requires Employer role and ownership of the job.**
    * **Headers**: `X-User-Id: [Employer User ID]`
    * **URL Parameters**: `id` (Job ID)
    * **Response**: `204 No Content` on successful deletion, `401 Unauthorized` if not logged in, `403 Forbidden` if not the job owner, `404 Not Found` if job doesn't exist.

* **Apply to Job**
    * **URL**: `/api/jobs/{jobId}/apply`
    * **Method**: `POST`
    * **Description**: Submits an application for a job, including a resume file. **Requires Student role.**
    * **Headers**: `X-User-Id: [Student User ID]`, `Content-Type: multipart/form-data`
    * **URL Parameters**: `jobId` (Job ID)
    * **Request Body**: `multipart/form-data` with parts:
        * `name`: Applicant's name (String)
        * `resume`: Resume file (File)
    * **Response**: `201 Created` with application details on success, `400 Bad Request` for missing data, `401 Unauthorized` if not logged in, `403 Forbidden` if not a student, `404 Not Found` if job doesn't exist, `500 Internal Server Error` for file storage issues.

* **View Applicants for a Job**
    * **URL**: `/api/jobs/{id}/applicants`
    * **Method**: `GET`
    * **Description**: Retrieves the list of applicants for a specific job. **Requires Employer role and ownership of the job.**
    * **Headers**: `X-User-Id: [Employer User ID]`
    * **URL Parameters**: `id` (Job ID)
    * **Response**: `200 OK` with a list of applicants (each with `name` and `resumeFileName`), `401 Unauthorized` if not logged in, `403 Forbidden` if not the job owner, `404 Not Found` if job doesn't exist.

* **Download Resume**
    * **URL**: `/api/jobs/resumes/{filename}`
    * **Method**: `GET`
    * **Description**: Downloads a specific resume file. Accessible by anyone who has the filename (consider adding auth for this in a real app).
    * **URL Parameters**: `filename` (Stored filename of the resume)
    * **Response**: `200 OK` with the file content, `404 Not Found` if the file doesn't exist or is not accessible.