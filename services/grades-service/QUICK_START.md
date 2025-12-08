# Grades Service - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Option 1: Docker Compose (Recommended)

```bash
# Navigate to docker directory
cd docker

# Start the grades service with its database
docker compose up -d grades_service

# Check logs
docker compose logs -f grades_service

# Access the API
# Swagger UI: http://localhost:8084/docs
# API: http://localhost:8084/api/v1/grades
```

### Option 2: Local Development

```bash
# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Set environment variables
export DATABASE_URL="postgresql://user:password@localhost:5435/grades_db"
export PORT=8084
export ENVIRONMENT=development

# Run the service
python -m uvicorn app.main:app --host 0.0.0.0 --port 8084 --reload
```

---

## 📚 First API Call

### Create a Grade

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

### Get Student GPA

```bash
curl http://localhost:8084/api/v1/grades/student/1/gpa
```

### Generate Transcript

```bash
curl http://localhost:8084/api/v1/grades/student/1/transcript
```

---

## 🔗 Access Points

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8084/docs |
| ReDoc | http://localhost:8084/redoc |
| API Base | http://localhost:8084/api/v1/grades |
| Health Check | http://localhost:8084/health |

---

## 📊 Grade Types

Use these values for `grade_type`:
- `EXAM` - Regular exam
- `MIDTERM` - Midterm exam
- `FINAL` - Final exam
- `HOMEWORK` - Homework assignment
- `PROJECT` - Project work

---

## 🧪 Run Tests

```bash
bash test-api.sh
```

This runs 27 comprehensive tests covering all endpoints.

---

## 🔍 Common Endpoints

### List All Grades
```bash
curl http://localhost:8084/api/v1/grades
```

### Get Grades for Student
```bash
curl http://localhost:8084/api/v1/grades/student/1
```

### Get Grades for Course
```bash
curl http://localhost:8084/api/v1/grades/course/101
```

### Get Course Statistics
```bash
curl http://localhost:8084/api/v1/grades/course/101/statistics
```

### Get Student Statistics
```bash
curl http://localhost:8084/api/v1/grades/statistics/student/1
```

---

## 🛠️ Troubleshooting

### Service won't start
```bash
# Check logs
docker compose logs grades_service

# Rebuild
docker compose build --no-cache grades_service
docker compose up -d grades_service
```

### Database connection error
```bash
# Verify database is running
docker compose ps

# Check database logs
docker compose logs grades_db
```

### Port already in use
```bash
# Kill process on port 8084
lsof -ti:8084 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8084   # Windows
```

---

## 📖 Full Documentation

- **README.md** - Complete user guide
- **API_DOCUMENTATION.md** - Detailed API reference
- **IMPLEMENTATION_SUMMARY.md** - Technical implementation details

---

## ✅ Verification

After starting the service, verify it's working:

```bash
# Health check
curl http://localhost:8084/health

# Access Swagger
open http://localhost:8084/docs
```

---

**Ready to go!** 🎉

For more details, see the full documentation in README.md
