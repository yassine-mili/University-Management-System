# Grades Service - API Testing Guide

Complete guide for testing all Grades Service endpoints.

## Quick Start

### Prerequisites

- Grades Service running on `http://localhost:8084`
- PostgreSQL database `grades_db` on port 5435
- Valid test data (student IDs, course IDs)

### Access Swagger UI

```
http://localhost:8084/docs
```

## Test Scenarios

### 1. Health Check

**Request:**

```bash
curl http://localhost:8084/health
```

**Expected Response:**

```json
{
  "message": "Grades service is healthy",
  "data": { "status": "ok" },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 2. Create Grade

**Endpoint:** `POST /api/v1/grades`

**Request:**

```bash
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 15.5,
    "grade_type": "FINAL",
    "date": "2024-12-08"
  }'
```

**Expected Response (201):**

```json
{
  "message": "Grade created successfully",
  "data": {
    "id": 1,
    "grade": {
      "id": 1,
      "student_id": 1001,
      "course_id": 101,
      "grade_value": 15.5,
      "grade_type": "FINAL",
      "date": "2024-12-08",
      "created_at": "2024-12-08T10:30:00",
      "updated_at": "2024-12-08T10:30:00"
    }
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

**Test Different Grade Types:**

```bash
# Midterm exam
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 14.0,
    "grade_type": "MIDTERM",
    "date": "2024-10-15"
  }'

# Homework
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 18.0,
    "grade_type": "HOMEWORK",
    "date": "2024-11-01"
  }'

# Project
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 16.5,
    "grade_type": "PROJECT",
    "date": "2024-11-20"
  }'
```

---

### 3. List All Grades

**Endpoint:** `GET /api/v1/grades`

**Request:**

```bash
# Get all grades
curl http://localhost:8084/api/v1/grades

# With pagination
curl "http://localhost:8084/api/v1/grades?skip=0&limit=10"

# Filter by student
curl "http://localhost:8084/api/v1/grades?student_id=1001"

# Filter by course
curl "http://localhost:8084/api/v1/grades?course_id=101"

# Filter by grade type
curl "http://localhost:8084/api/v1/grades?grade_type=FINAL"

# Combined filters
curl "http://localhost:8084/api/v1/grades?student_id=1001&course_id=101"
```

**Expected Response:**

```json
{
  "message": "Grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1001,
        "course_id": 101,
        "grade_value": 15.5,
        "grade_type": "FINAL",
        "date": "2024-12-08",
        "created_at": "2024-12-08T10:30:00",
        "updated_at": "2024-12-08T10:30:00"
      }
    ],
    "total": 1,
    "skip": 0,
    "limit": 100
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 4. Get Single Grade

**Endpoint:** `GET /api/v1/grades/{grade_id}`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/1
```

**Expected Response:**

```json
{
  "message": "Grade retrieved successfully",
  "data": {
    "grade": {
      "id": 1,
      "student_id": 1001,
      "course_id": 101,
      "grade_value": 15.5,
      "grade_type": "FINAL",
      "date": "2024-12-08",
      "created_at": "2024-12-08T10:30:00",
      "updated_at": "2024-12-08T10:30:00"
    }
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 5. Update Grade

**Endpoint:** `PUT /api/v1/grades/{grade_id}`

**Request:**

```bash
curl -X PUT http://localhost:8084/api/v1/grades/1 \
  -H "Content-Type: application/json" \
  -d '{
    "grade_value": 16.0
  }'
```

**Expected Response:**

```json
{
  "message": "Grade updated successfully",
  "data": {
    "grade": {
      "id": 1,
      "student_id": 1001,
      "course_id": 101,
      "grade_value": 16.0,
      "grade_type": "FINAL",
      "date": "2024-12-08",
      "created_at": "2024-12-08T10:30:00",
      "updated_at": "2024-12-08T10:35:00"
    }
  },
  "timestamp": "2024-12-08T10:35:00"
}
```

---

### 6. Delete Grade

**Endpoint:** `DELETE /api/v1/grades/{grade_id}`

**Request:**

```bash
curl -X DELETE http://localhost:8084/api/v1/grades/1
```

**Expected Response:**

```json
{
  "message": "Grade deleted successfully",
  "data": { "id": 1 },
  "timestamp": "2024-12-08T10:40:00"
}
```

---

### 7. Get Student Grades

**Endpoint:** `GET /api/v1/grades/student/{student_id}`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/student/1001
```

**Expected Response:**

```json
{
  "message": "Student grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1001,
        "course_id": 101,
        "grade_value": 15.5,
        "grade_type": "FINAL",
        "date": "2024-12-08"
      },
      {
        "id": 2,
        "student_id": 1001,
        "course_id": 102,
        "grade_value": 14.0,
        "grade_type": "MIDTERM",
        "date": "2024-11-15"
      }
    ]
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 8. Calculate Student GPA

**Endpoint:** `GET /api/v1/grades/student/{student_id}/gpa`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/student/1001/gpa
```

**Expected Response:**

```json
{
  "message": "GPA calculated successfully",
  "data": {
    "student_id": 1001,
    "gpa": 3.5,
    "total_grades": 15,
    "average_grade": 14.8
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 9. Calculate Student Average ⭐ NEW

**Endpoint:** `GET /api/v1/grades/student/{student_id}/average`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/student/1001/average
```

**Expected Response:**

```json
{
  "message": "Average calculated successfully",
  "data": {
    "student_id": 1001,
    "gpa": 3.5,
    "total_grades": 15,
    "average_grade": 14.8
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 10. Calculate Semester Average ⭐ NEW

**Endpoint:** `GET /api/v1/grades/student/{student_id}/semester/{semester}/average`

**Request:**

```bash
# Fall 2024
curl http://localhost:8084/api/v1/grades/student/1001/semester/2024-1/average

# Spring 2024
curl http://localhost:8084/api/v1/grades/student/1001/semester/2024-2/average
```

**Expected Response:**

```json
{
  "message": "Semester average calculated successfully",
  "data": {
    "student_id": 1001,
    "semester": "2024-1",
    "average_grade": 15.5,
    "total_grades": 12,
    "courses_count": 5
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 11. Generate Student Transcript

**Endpoint:** `GET /api/v1/grades/student/{student_id}/transcript`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/student/1001/transcript
```

**Expected Response:**

```json
{
  "message": "Transcript generated successfully",
  "data": {
    "student_id": 1001,
    "gpa": 3.5,
    "total_credits": 15,
    "academic_status": "ACTIVE",
    "passed_courses": 12,
    "failed_courses": 0,
    "grades": [
      {
        "course_id": 101,
        "grade_value": 15.5,
        "grade_type": "FINAL",
        "date": "2024-12-08"
      }
    ],
    "generated_at": "2024-12-08T10:30:00"
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 12. Get Course Grades

**Endpoint:** `GET /api/v1/grades/course/{course_id}`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/course/101
```

**Expected Response:**

```json
{
  "message": "Course grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1001,
        "course_id": 101,
        "grade_value": 15.5,
        "grade_type": "FINAL",
        "date": "2024-12-08"
      },
      {
        "id": 2,
        "student_id": 1002,
        "course_id": 101,
        "grade_value": 14.0,
        "grade_type": "FINAL",
        "date": "2024-12-08"
      }
    ]
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 13. Get Course Statistics

**Endpoint:** `GET /api/v1/grades/course/{course_id}/statistics`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/course/101/statistics
```

**Expected Response:**

```json
{
  "message": "Course statistics retrieved successfully",
  "data": {
    "course_id": 101,
    "total_students": 45,
    "average_grade": 14.5,
    "highest_grade": 19.5,
    "lowest_grade": 8.0,
    "passed_count": 40,
    "failed_count": 5
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 14. Get Student Statistics

**Endpoint:** `GET /api/v1/grades/statistics/student/{student_id}`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/statistics/student/1001
```

**Expected Response:**

```json
{
  "message": "Student statistics retrieved successfully",
  "data": {
    "average_grade": 15.2,
    "highest_grade": 19.0,
    "lowest_grade": 12.0,
    "total_grades": 15,
    "grade_distribution": {
      "EXAM": { "count": 5, "average": 15.5 },
      "MIDTERM": { "count": 5, "average": 14.8 },
      "FINAL": { "count": 5, "average": 15.6 }
    }
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

### 15. Get Grade Distribution

**Endpoint:** `GET /api/v1/grades/statistics/distribution/{student_id}`

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/statistics/distribution/1001
```

**Expected Response:**

```json
{
  "message": "Grade distribution retrieved successfully",
  "data": {
    "distribution": {
      "EXAM": [15.5, 16.0, 14.5],
      "MIDTERM": [14.0, 15.0, 13.5],
      "FINAL": [16.5, 17.0, 15.5],
      "HOMEWORK": [18.0, 17.5, 19.0],
      "PROJECT": [16.0, 15.5, 17.0]
    }
  },
  "timestamp": "2024-12-08T10:30:00"
}
```

---

## Error Testing

### 1. Invalid Grade Value (Out of Range)

**Request:**

```bash
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 25.0,
    "grade_type": "FINAL",
    "date": "2024-12-08"
  }'
```

**Expected Response (422):**

```json
{
  "detail": [
    {
      "type": "value_error",
      "loc": ["body", "grade_value"],
      "msg": "Value error, Grade value must be between 0 and 20"
    }
  ]
}
```

---

### 2. Duplicate Grade

**Request:**

```bash
# Create same grade twice
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 15.5,
    "grade_type": "FINAL",
    "date": "2024-12-08"
  }'
```

**Expected Response (400):**

```json
{
  "detail": "A grade of this type already exists for this student-course combination"
}
```

---

### 3. Future Date

**Request:**

```bash
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1001,
    "course_id": 101,
    "grade_value": 15.5,
    "grade_type": "FINAL",
    "date": "2025-12-31"
  }'
```

**Expected Response (422):**

```json
{
  "detail": [
    {
      "type": "value_error",
      "loc": ["body", "date"],
      "msg": "Value error, Date cannot be in the future"
    }
  ]
}
```

---

### 4. Grade Not Found

**Request:**

```bash
curl http://localhost:8084/api/v1/grades/9999
```

**Expected Response (404):**

```json
{
  "detail": "Grade not found"
}
```

---

## Complete Test Flow

### Create Complete Grade Set for Student

```bash
#!/bin/bash

STUDENT_ID=1001
COURSE_ID=101
BASE_URL="http://localhost:8084/api/v1/grades"

# Midterm
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{\"student_id\": $STUDENT_ID, \"course_id\": $COURSE_ID, \"grade_value\": 14.0, \"grade_type\": \"MIDTERM\", \"date\": \"2024-10-15\"}"

# Exam
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{\"student_id\": $STUDENT_ID, \"course_id\": $COURSE_ID, \"grade_value\": 15.5, \"grade_type\": \"EXAM\", \"date\": \"2024-11-10\"}"

# Homework
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{\"student_id\": $STUDENT_ID, \"course_id\": $COURSE_ID, \"grade_value\": 18.0, \"grade_type\": \"HOMEWORK\", \"date\": \"2024-11-01\"}"

# Project
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{\"student_id\": $STUDENT_ID, \"course_id\": $COURSE_ID, \"grade_value\": 16.5, \"grade_type\": \"PROJECT\", \"date\": \"2024-11-20\"}"

# Final
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{\"student_id\": $STUDENT_ID, \"course_id\": $COURSE_ID, \"grade_value\": 17.0, \"grade_type\": \"FINAL\", \"date\": \"2024-12-08\"}"

# Calculate GPA
curl "http://localhost:8084/api/v1/grades/student/$STUDENT_ID/gpa"

# Get transcript
curl "http://localhost:8084/api/v1/grades/student/$STUDENT_ID/transcript"
```

---

## Postman Collection

### Import into Postman

1. Create new collection: "Grades Service"
2. Set base URL variable: `{{BASE_URL}}` = `http://localhost:8084`
3. Import the following requests:

**Collection Structure:**

```
Grades Service/
├── Health/
│   └── Health Check
├── Grade CRUD/
│   ├── Create Grade
│   ├── List Grades
│   ├── Get Grade by ID
│   ├── Update Grade
│   └── Delete Grade
├── Student Operations/
│   ├── Get Student Grades
│   ├── Calculate GPA
│   ├── Calculate Average
│   ├── Calculate Semester Average
│   └── Generate Transcript
├── Course Operations/
│   ├── Get Course Grades
│   └── Get Course Statistics
└── Statistics/
    ├── Student Statistics
    └── Grade Distribution
```

---

## Success Criteria

✅ **All endpoints return 200/201 for valid requests**  
✅ **Proper validation errors (422) for invalid data**  
✅ **404 errors for non-existent resources**  
✅ **400 errors for business logic violations**  
✅ **GPA calculation is accurate (weighted average)**  
✅ **Transcript includes all grades and correct GPA**  
✅ **Academic status updates automatically**  
✅ **Duplicate prevention works correctly**  
✅ **Semester average calculation is accurate**  
✅ **Course statistics show correct aggregations**

---

## Troubleshooting

### Service won't start

```bash
# Check if port 8084 is available
netstat -ano | findstr :8084

# Check database connection
psql -h localhost -p 5435 -U user -d grades_db

# Check logs
docker-compose logs grades_service
```

### Database errors

```bash
# Verify database exists
docker exec -it grades_db psql -U user -d grades_db -c "\dt"

# Check tables
docker exec -it grades_db psql -U user -d grades_db -c "SELECT * FROM grades LIMIT 5;"
```

### API returns 500 errors

```bash
# Check application logs
docker-compose logs -f grades_service

# Verify environment variables
docker-compose exec grades_service env | grep DATABASE_URL
```

---

**Version:** 1.0.0  
**Last Updated:** December 8, 2024  
**Status:** Production Ready ✅
