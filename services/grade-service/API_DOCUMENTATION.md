# Grades Service - API Documentation

## 📖 Complete API Reference

### Base URL
```
http://localhost:8084/api/v1/grades
```

### Authentication
Currently, the Grades Service does not require authentication. In production, JWT tokens should be validated through the API Gateway.

---

## 🔧 Grade Management Endpoints

### 1. Create Grade
**Endpoint:** `POST /api/v1/grades`

**Description:** Create a new grade entry for a student in a course.

**Request Body:**
```json
{
  "student_id": 1,
  "course_id": 101,
  "grade_value": 18.5,
  "grade_type": "EXAM",
  "date": "2024-12-07"
}
```

**Parameters:**
| Parameter | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| student_id | integer | Yes | Student ID | > 0 |
| course_id | integer | Yes | Course ID | > 0 |
| grade_value | number | Yes | Grade value | 0 ≤ value ≤ 20 |
| grade_type | string | Yes | Type of grade | EXAM, HOMEWORK, PROJECT, MIDTERM, FINAL |
| date | string (date) | Yes | Grade date | Cannot be in future |

**Response (201 Created):**
```json
{
  "message": "Grade created successfully",
  "data": {
    "id": 1,
    "grade": {
      "id": 1,
      "student_id": 1,
      "course_id": 101,
      "grade_value": 18.5,
      "grade_type": "EXAM",
      "date": "2024-12-07",
      "created_at": "2024-12-07T10:30:00",
      "updated_at": "2024-12-07T10:30:00"
    }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input or duplicate grade
- `422 Unprocessable Entity` - Validation error

**Example:**
```bash
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1,
    "course_id": 101,
    "grade_value": 18.5,
    "grade_type": "EXAM",
    "date": "2024-12-07"
  }'
```

---

### 2. List All Grades
**Endpoint:** `GET /api/v1/grades`

**Description:** Retrieve all grades with optional filtering and pagination.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| skip | integer | 0 | Number of records to skip |
| limit | integer | 100 | Number of records to return (max 1000) |
| student_id | integer | null | Filter by student ID |
| course_id | integer | null | Filter by course ID |
| grade_type | string | null | Filter by grade type |

**Response (200 OK):**
```json
{
  "message": "Grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1,
        "course_id": 101,
        "grade_value": 18.5,
        "grade_type": "EXAM",
        "date": "2024-12-07",
        "created_at": "2024-12-07T10:30:00",
        "updated_at": "2024-12-07T10:30:00"
      }
    ],
    "total": 1,
    "skip": 0,
    "limit": 100
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Examples:**
```bash
# Get all grades
curl http://localhost:8084/api/v1/grades

# Get grades for student 1
curl http://localhost:8084/api/v1/grades?student_id=1

# Get grades for course 101
curl http://localhost:8084/api/v1/grades?course_id=101

# Get EXAM grades with pagination
curl http://localhost:8084/api/v1/grades?grade_type=EXAM&skip=0&limit=50
```

---

### 3. Get Single Grade
**Endpoint:** `GET /api/v1/grades/{id}`

**Description:** Retrieve a specific grade by ID.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | Grade ID |

**Response (200 OK):**
```json
{
  "message": "Grade retrieved successfully",
  "data": {
    "grade": {
      "id": 1,
      "student_id": 1,
      "course_id": 101,
      "grade_value": 18.5,
      "grade_type": "EXAM",
      "date": "2024-12-07",
      "created_at": "2024-12-07T10:30:00",
      "updated_at": "2024-12-07T10:30:00"
    }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Error Responses:**
- `404 Not Found` - Grade not found

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/1
```

---

### 4. Update Grade
**Endpoint:** `PUT /api/v1/grades/{id}`

**Description:** Update an existing grade entry.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | Grade ID |

**Request Body (all fields optional):**
```json
{
  "grade_value": 19.0,
  "grade_type": "EXAM",
  "date": "2024-12-07"
}
```

**Response (200 OK):**
```json
{
  "message": "Grade updated successfully",
  "data": {
    "grade": {
      "id": 1,
      "student_id": 1,
      "course_id": 101,
      "grade_value": 19.0,
      "grade_type": "EXAM",
      "date": "2024-12-07",
      "created_at": "2024-12-07T10:30:00",
      "updated_at": "2024-12-07T10:35:00"
    }
  },
  "timestamp": "2024-12-07T10:35:00"
}
```

**Error Responses:**
- `404 Not Found` - Grade not found
- `400 Bad Request` - Invalid input or duplicate grade

**Example:**
```bash
curl -X PUT http://localhost:8084/api/v1/grades/1 \
  -H "Content-Type: application/json" \
  -d '{
    "grade_value": 19.0
  }'
```

---

### 5. Delete Grade
**Endpoint:** `DELETE /api/v1/grades/{id}`

**Description:** Remove a grade entry.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | integer | Grade ID |

**Response (200 OK):**
```json
{
  "message": "Grade deleted successfully",
  "data": {
    "id": 1
  },
  "timestamp": "2024-12-07T10:40:00"
}
```

**Error Responses:**
- `404 Not Found` - Grade not found

**Example:**
```bash
curl -X DELETE http://localhost:8084/api/v1/grades/1
```

---

## 👤 Student-Specific Endpoints

### 6. Get Student Grades
**Endpoint:** `GET /api/v1/grades/student/{student_id}`

**Description:** Retrieve all grades for a specific student.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| student_id | integer | Student ID |

**Response (200 OK):**
```json
{
  "message": "Student grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1,
        "course_id": 101,
        "grade_value": 18.5,
        "grade_type": "EXAM",
        "date": "2024-12-07",
        "created_at": "2024-12-07T10:30:00",
        "updated_at": "2024-12-07T10:30:00"
      },
      {
        "id": 2,
        "student_id": 1,
        "course_id": 102,
        "grade_value": 17.0,
        "grade_type": "MIDTERM",
        "date": "2024-12-05",
        "created_at": "2024-12-05T14:20:00",
        "updated_at": "2024-12-05T14:20:00"
      }
    ]
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/student/1
```

---

### 7. Calculate Student GPA
**Endpoint:** `GET /api/v1/grades/student/{student_id}/gpa`

**Description:** Calculate the GPA for a specific student using weighted average.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| student_id | integer | Student ID |

**Response (200 OK):**
```json
{
  "message": "GPA calculated successfully",
  "data": {
    "student_id": 1,
    "gpa": 3.75,
    "total_grades": 5,
    "average_grade": 18.5
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**GPA Calculation:**
- Formula: GPA = SUM(normalized_grade × weight) / SUM(weight)
- Normalized grade: grade_value / 5 (converts 0-20 to 0-4 scale)
- Weights: EXAM (40%), MIDTERM (20%), FINAL (30%), HOMEWORK (5%), PROJECT (5%)

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/student/1/gpa
```

---

### 8. Generate Academic Transcript
**Endpoint:** `GET /api/v1/grades/student/{student_id}/transcript`

**Description:** Generate a comprehensive academic transcript for a student.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| student_id | integer | Student ID |

**Response (200 OK):**
```json
{
  "message": "Transcript generated successfully",
  "data": {
    "student_id": 1,
    "gpa": 3.75,
    "total_credits": 5,
    "academic_status": "ACTIVE",
    "passed_courses": 5,
    "failed_courses": 0,
    "grades": [
      {
        "course_id": 101,
        "grade_value": 18.5,
        "grade_type": "EXAM",
        "date": "2024-12-07"
      },
      {
        "course_id": 102,
        "grade_value": 17.0,
        "grade_type": "MIDTERM",
        "date": "2024-12-05"
      }
    ],
    "generated_at": "2024-12-07T10:30:00"
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Academic Status:**
- ACTIVE: GPA ≥ 2.0 and failed_courses ≤ 2
- PROBATION: 1.0 ≤ GPA < 2.0 or failed_courses > 2
- SUSPENDED: GPA < 1.0 or failed_courses > 4

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/student/1/transcript
```

---

## 📚 Course-Specific Endpoints

### 9. Get Course Grades
**Endpoint:** `GET /api/v1/grades/course/{course_id}`

**Description:** Retrieve all grades for a specific course.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| course_id | integer | Course ID |

**Response (200 OK):**
```json
{
  "message": "Course grades retrieved successfully",
  "data": {
    "grades": [
      {
        "id": 1,
        "student_id": 1,
        "course_id": 101,
        "grade_value": 18.5,
        "grade_type": "EXAM",
        "date": "2024-12-07",
        "created_at": "2024-12-07T10:30:00",
        "updated_at": "2024-12-07T10:30:00"
      }
    ]
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/course/101
```

---

### 10. Get Course Statistics
**Endpoint:** `GET /api/v1/grades/course/{course_id}/statistics`

**Description:** Get statistical analysis of grades for a course.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| course_id | integer | Course ID |

**Response (200 OK):**
```json
{
  "message": "Course statistics retrieved successfully",
  "data": {
    "course_id": 101,
    "total_students": 30,
    "average_grade": 16.5,
    "highest_grade": 20,
    "lowest_grade": 8,
    "passed_count": 28,
    "failed_count": 2
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/course/101/statistics
```

---

## 📊 Statistics Endpoints

### 11. Get Student Grade Statistics
**Endpoint:** `GET /api/v1/grades/statistics/student/{student_id}`

**Description:** Get statistical analysis of a student's grades.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| student_id | integer | Student ID |

**Response (200 OK):**
```json
{
  "message": "Student statistics retrieved successfully",
  "data": {
    "average_grade": 18.5,
    "highest_grade": 20,
    "lowest_grade": 15,
    "total_grades": 5,
    "grade_distribution": {
      "EXAM": {
        "count": 2,
        "average": 18.75
      },
      "MIDTERM": {
        "count": 1,
        "average": 17.0
      },
      "HOMEWORK": {
        "count": 2,
        "average": 19.0
      }
    }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/statistics/student/1
```

---

### 12. Get Grade Distribution
**Endpoint:** `GET /api/v1/grades/statistics/distribution/{student_id}`

**Description:** Get grade distribution grouped by grade type for a student.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| student_id | integer | Student ID |

**Response (200 OK):**
```json
{
  "message": "Grade distribution retrieved successfully",
  "data": {
    "distribution": {
      "EXAM": [18.5, 19.0],
      "MIDTERM": [17.0],
      "HOMEWORK": [19.5, 18.5],
      "PROJECT": [20.0],
      "FINAL": [18.0]
    }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/statistics/distribution/1
```

---

## 🏥 Health Check Endpoints

### 13. Service Health Check
**Endpoint:** `GET /api/v1/grades/health`

**Description:** Check if the grades service is running.

**Response (200 OK):**
```json
{
  "message": "Grades service is healthy",
  "data": {
    "status": "ok"
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

**Example:**
```bash
curl http://localhost:8084/api/v1/grades/health
```

---

### 14. Root Health Check
**Endpoint:** `GET /health`

**Description:** Check if the service is running (root level).

**Response (200 OK):**
```json
{
  "status": "healthy",
  "service": "grades-service",
  "version": "1.0.0"
}
```

**Example:**
```bash
curl http://localhost:8084/health
```

---

## 🔍 Grade Types Reference

| Type | Description | Typical Weight |
|------|-------------|-----------------|
| EXAM | Regular exam grades | 40% |
| HOMEWORK | Homework assignment grades | 5% |
| PROJECT | Project work grades | 5% |
| MIDTERM | Midterm exam grades | 20% |
| FINAL | Final exam grades | 30% |

---

## 📋 Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input or duplicate grade |
| 404 | Not Found | Resource not found |
| 422 | Unprocessable Entity | Validation error |
| 500 | Internal Server Error | Server error |

---

## 🔐 Data Validation Rules

### Grade Value
- **Range:** 0 to 20
- **Type:** Float
- **Required:** Yes
- **Validation:** Database constraint + Pydantic validation

### Date
- **Format:** YYYY-MM-DD
- **Constraint:** Cannot be in the future
- **Required:** Yes

### Student ID & Course ID
- **Type:** Integer
- **Constraint:** Must be positive (> 0)
- **Required:** Yes

### Grade Type
- **Allowed Values:** EXAM, HOMEWORK, PROJECT, MIDTERM, FINAL
- **Type:** String
- **Required:** Yes

---

## 📝 Request/Response Examples

### Complete Workflow Example

1. **Create a grade:**
```bash
curl -X POST http://localhost:8084/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "student_id": 1,
    "course_id": 101,
    "grade_value": 18.5,
    "grade_type": "EXAM",
    "date": "2024-12-07"
  }'
```

2. **Get student's GPA:**
```bash
curl http://localhost:8084/api/v1/grades/student/1/gpa
```

3. **Generate transcript:**
```bash
curl http://localhost:8084/api/v1/grades/student/1/transcript
```

4. **Get course statistics:**
```bash
curl http://localhost:8084/api/v1/grades/course/101/statistics
```

---

## 🔗 Related Services

- **Student Service:** http://localhost:8082/api/v1/students
- **Courses Service:** http://localhost:8083 (SOAP)
- **API Gateway:** http://localhost:8080

---

**Last Updated:** December 7, 2024  
**API Version:** 1.0.0  
**Status:** Production Ready
