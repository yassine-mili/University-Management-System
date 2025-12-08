# 🎓 Grades Service

A comprehensive REST API for managing student grades, calculating GPAs, and generating academic transcripts using FastAPI and PostgreSQL.

## 📋 Overview

The Grades Service is a microservice that handles:
- Grade entry and management (CRUD operations)
- GPA calculation with weighted averages
- Academic transcript generation
- Grade statistics and analytics
- Academic status determination (ACTIVE, PROBATION, SUSPENDED)

**Technology Stack:**
- **Framework:** FastAPI 0.104.1
- **Database:** PostgreSQL 14
- **ORM:** SQLAlchemy 2.0
- **Server:** Uvicorn
- **Documentation:** Automatic Swagger/OpenAPI at `/docs`

## 🚀 Quick Start

### Prerequisites
- Python 3.11+
- PostgreSQL 14+
- Docker & Docker Compose (optional)

### Local Development Setup

1. **Create virtual environment:**
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Set up environment variables:**
   ```bash
   # Create .env file (not in git)
   DATABASE_URL=postgresql://user:password@localhost:5435/grades_db
   PORT=8084
   ENVIRONMENT=development
   ```

4. **Run the service:**
   ```bash
   python -m uvicorn app.main:app --host 0.0.0.0 --port 8084 --reload
   ```

5. **Access the API:**
   - Swagger UI: http://localhost:8084/docs
   - ReDoc: http://localhost:8084/redoc
   - API: http://localhost:8084/api/v1/grades

### Docker Setup

```bash
# Build and run with Docker Compose
cd docker
docker compose up -d grades_service

# View logs
docker compose logs -f grades_service

# Stop service
docker compose down
```

## 📊 Database Schema

### Grades Table
```sql
CREATE TABLE grades (
  id SERIAL PRIMARY KEY,
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  grade_value FLOAT NOT NULL CHECK (grade_value BETWEEN 0 AND 20),
  grade_type VARCHAR(50) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(student_id, course_id, grade_type)
);
```

### Grade Calculation Rules Table
```sql
CREATE TABLE grade_calculation_rules (
  id SERIAL PRIMARY KEY,
  grade_type VARCHAR(50) UNIQUE NOT NULL,
  weight FLOAT NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Academic Records Table
```sql
CREATE TABLE academic_records (
  id SERIAL PRIMARY KEY,
  student_id INT UNIQUE NOT NULL,
  gpa FLOAT DEFAULT 0.0,
  total_credits INT DEFAULT 0,
  passed_courses INT DEFAULT 0,
  failed_courses INT DEFAULT 0,
  academic_status VARCHAR(50) DEFAULT 'ACTIVE',
  last_updated TIMESTAMP DEFAULT NOW(),
  created_at TIMESTAMP DEFAULT NOW()
);
```

## 📚 API Endpoints

### Grade Management

#### Create Grade
```http
POST /api/v1/grades
Content-Type: application/json

{
  "student_id": 1,
  "course_id": 101,
  "grade_value": 18.5,
  "grade_type": "EXAM",
  "date": "2024-12-07"
}
```

**Response (201):**
```json
{
  "message": "Grade created successfully",
  "data": {
    "id": 1,
    "grade": { ... }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

#### List All Grades
```http
GET /api/v1/grades?skip=0&limit=100&student_id=1&course_id=101&grade_type=EXAM
```

#### Get Single Grade
```http
GET /api/v1/grades/{id}
```

#### Update Grade
```http
PUT /api/v1/grades/{id}
Content-Type: application/json

{
  "grade_value": 19.0,
  "grade_type": "EXAM"
}
```

#### Delete Grade
```http
DELETE /api/v1/grades/{id}
```

### Student-Specific Endpoints

#### Get Student Grades
```http
GET /api/v1/grades/student/{student_id}
```

#### Calculate Student GPA
```http
GET /api/v1/grades/student/{student_id}/gpa
```

**Response:**
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

#### Generate Academic Transcript
```http
GET /api/v1/grades/student/{student_id}/transcript
```

**Response:**
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
      }
    ],
    "generated_at": "2024-12-07T10:30:00"
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

### Course-Specific Endpoints

#### Get Course Grades
```http
GET /api/v1/grades/course/{course_id}
```

#### Get Course Statistics
```http
GET /api/v1/grades/course/{course_id}/statistics
```

**Response:**
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

### Statistics Endpoints

#### Get Student Grade Statistics
```http
GET /api/v1/grades/statistics/student/{student_id}
```

#### Get Grade Distribution
```http
GET /api/v1/grades/statistics/distribution/{student_id}
```

### Health Check
```http
GET /api/v1/grades/health
```

## 🧮 GPA Calculation

The GPA is calculated using a weighted average formula:

```
GPA = SUM(normalized_grade × weight) / SUM(weight)
```

Where:
- **normalized_grade** = grade_value / 5 (converts 0-20 scale to 0-4 scale)
- **weight** = weight from grade_calculation_rules table

### Default Weights
- EXAM: 40%
- MIDTERM: 20%
- FINAL: 30%
- HOMEWORK: 5%
- PROJECT: 5%

### Academic Status
- **ACTIVE:** GPA ≥ 2.0 and failed_courses ≤ 2
- **PROBATION:** 1.0 ≤ GPA < 2.0 or failed_courses > 2
- **SUSPENDED:** GPA < 1.0 or failed_courses > 4

## 🔍 Grade Types

- **EXAM:** Regular exam grades
- **HOMEWORK:** Homework assignment grades
- **PROJECT:** Project work grades
- **MIDTERM:** Midterm exam grades
- **FINAL:** Final exam grades

## 📝 Data Validation

### Grade Value
- Range: 0 to 20
- Required: Yes
- Validation: Database constraint + Pydantic validation

### Date Validation
- Cannot be in the future
- Required: Yes

### Duplicate Prevention
- Unique constraint on (student_id, course_id, grade_type)
- Prevents duplicate grades of the same type for a student-course pair

## 🔄 Business Logic

### Grade Creation Flow
1. Validate input data (Pydantic schema)
2. Check for duplicate grades
3. Create grade entry in database
4. Automatically update academic record
5. Recalculate GPA and academic status

### Grade Update Flow
1. Validate input data
2. Check for duplicate grades (if grade_type changes)
3. Update grade entry
4. Automatically update academic record
5. Recalculate GPA and academic status

### Grade Deletion Flow
1. Verify grade exists
2. Delete grade entry
3. Automatically update academic record
4. Recalculate GPA and academic status

## 🛠️ Project Structure

```
grades-service/
├── app/
│   ├── __init__.py
│   ├── main.py                 # FastAPI application
│   ├── models.py               # SQLAlchemy models
│   ├── schemas.py              # Pydantic schemas
│   ├── database.py             # Database configuration
│   ├── crud.py                 # CRUD operations
│   ├── business_logic.py       # GPA calculation, transcripts
│   └── routers/
│       ├── __init__.py
│       └── grades.py           # Grade endpoints
├── requirements.txt            # Python dependencies
├── Dockerfile                  # Docker configuration
├── README.md                   # This file
└── .env                        # Environment variables (not in git)
```

## 🧪 Testing

### Test Grade Creation
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

### Test GPA Calculation
```bash
curl http://localhost:8084/api/v1/grades/student/1/gpa
```

### Test Transcript Generation
```bash
curl http://localhost:8084/api/v1/grades/student/1/transcript
```

### Test Health Check
```bash
curl http://localhost:8084/health
```

## 📊 Response Format

### Success Response
```json
{
  "message": "Operation successful",
  "data": { /* result data */ },
  "timestamp": "2024-12-07T10:30:00"
}
```

### Error Response
```json
{
  "detail": "Error description"
}
```

## 🔐 Security Considerations

- Input validation on all endpoints
- Database constraints for data integrity
- CORS enabled for cross-origin requests
- Unique constraints prevent duplicate data
- Check constraints ensure valid grade values

## 📈 Performance Optimization

- Database indexes on frequently queried columns (student_id, course_id)
- Connection pooling (pool_size=10, max_overflow=20)
- Query optimization with SQLAlchemy
- Efficient GPA calculation algorithm

## 🐛 Troubleshooting

### Database Connection Error
```
Error: could not connect to server: Connection refused
```
**Solution:** Ensure PostgreSQL is running and DATABASE_URL is correct

### Port Already in Use
```
Error: Address already in use
```
**Solution:** Change PORT in .env or kill process using port 8084

### Import Errors
```
ModuleNotFoundError: No module named 'fastapi'
```
**Solution:** Install dependencies: `pip install -r requirements.txt`

## 📚 Additional Resources

- [FastAPI Documentation](https://fastapi.tiangolo.com)
- [SQLAlchemy Documentation](https://docs.sqlalchemy.org)
- [PostgreSQL Documentation](https://www.postgresql.org/docs)
- [Pydantic Documentation](https://docs.pydantic.dev)

## 📝 License

This project is part of the University Management System SOA project.

## 👥 Authors

- Mili Yassine
- Battikh Youssef
- Ksouri Fahmi

---

**Last Updated:** December 7, 2024  
**Version:** 1.0.0  
**Status:** Production Ready
