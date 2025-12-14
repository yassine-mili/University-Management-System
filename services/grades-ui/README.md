# Grades Service Testing UI

A modern, simple React-based UI for testing the Grades Service API.

## Features

- **View Grades**: Display all grades for a student
- **Add Grades**: Create new grade records with course ID, grade value, weight, and exam date
- **Transcript View**: See academic transcript with GPA and academic status
- **Statistics**: View grade distribution and statistics
- **Delete Grades**: Remove individual grade records
- **Responsive Design**: Works on desktop and mobile devices

## Prerequisites

- Node.js 14+ and npm
- Grades Service running on `http://localhost:8084`

## Installation

```bash
npm install
```

## Running the Application

```bash
npm start
```

The application will open at `http://localhost:3000`

## Usage

1. **Enter Student ID**: Type a student ID in the input field at the top
2. **Load Grades**: Click "Load Grades" to fetch all grades for that student
3. **Navigate Tabs**:
   - **Grades**: View all grades in a table format
   - **Add Grade**: Create a new grade record
   - **Transcript**: View academic transcript with GPA
   - **Statistics**: See grade distribution and statistics

## API Endpoints Used

- `GET /grades/student/{student_id}` - Get all grades for a student
- `GET /grades/student/{student_id}/transcript` - Get academic transcript
- `GET /grades/student/{student_id}/statistics` - Get grade statistics
- `POST /grades/` - Create a new grade
- `DELETE /grades/{grade_id}` - Delete a grade

## Building for Production

```bash
npm run build
```

The build folder will contain the optimized production build.

## Technologies Used

- React 18
- Axios (HTTP client)
- CSS3 with Flexbox and Grid
- Modern UI/UX design

## Notes

- The UI assumes the Grades Service is running on `http://localhost:8084`
- CORS must be enabled on the Grades Service for the UI to work
- Grade values should be between 0-20
- Passing grade is 10 or above
