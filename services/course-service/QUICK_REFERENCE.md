# Courses Service - Quick Reference

## Service Information

- **Port**: 8083
- **Service URL**: http://localhost:8083/CourseService
- **WSDL**: http://localhost:8083/CourseService?wsdl
- **Technology**: JAX-WS SOAP (Java 17)
- **Database**: PostgreSQL (Port 5434)

## Quick Start Commands

### Start Service (Docker Compose - Recommended)

```bash
cd docker
docker-compose up courses_service courses_db
```

### Build and Run Locally

```bash
cd services/courses
mvn clean package
java -jar target/courses-service-jar-with-dependencies.jar
```

### Test WSDL

```bash
curl http://localhost:8083/CourseService?wsdl
```

### Run All Tests

```bash
# PowerShell
.\build.ps1 -Command test-all

# Bash
./build.sh test-all
```

## SOAP Operations Reference

### Course Operations

| Operation               | Description              | Parameters                                                                                                          |
| ----------------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------------- |
| `createCourse`          | Create new course        | code, name, description, credits, semester, capacity, prerequisites, enrollmentStartDate, enrollmentEndDate         |
| `getCourse`             | Get course by ID         | id                                                                                                                  |
| `getCourseByCode`       | Get course by code       | code                                                                                                                |
| `updateCourse`          | Update course            | id, name, description, credits, semester, capacity, prerequisites, enrollmentStartDate, enrollmentEndDate, isActive |
| `deleteCourse`          | Delete course            | id                                                                                                                  |
| `listCourses`           | List all courses         | -                                                                                                                   |
| `listCoursesBySemester` | List courses by semester | semester                                                                                                            |

### Schedule Operations

| Operation             | Description              | Parameters                                              |
| --------------------- | ------------------------ | ------------------------------------------------------- |
| `addSchedule`         | Add course schedule      | courseId, dayOfWeek, startTime, endTime, room, building |
| `getScheduleByCourse` | Get schedules for course | courseId                                                |
| `deleteSchedule`      | Delete schedule          | id                                                      |

### Enrollment Operations

| Operation                | Description               | Parameters          |
| ------------------------ | ------------------------- | ------------------- |
| `enrollStudent`          | Enroll student in course  | studentId, courseId |
| `unenrollStudent`        | Remove enrollment         | studentId, courseId |
| `getStudentCourses`      | Get student's courses     | studentId           |
| `getCourseEnrollments`   | Get course enrollments    | courseId            |
| `getCourseWithSchedules` | Get course with schedules | id                  |

### Utility

| Operation        | Description             | Parameters |
| ---------------- | ----------------------- | ---------- |
| `getServiceInfo` | Get service information | -          |

## Sample Requests

### List All Courses

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:listCourses/>
   </soapenv:Body>
</soapenv:Envelope>'
```

### Get Course by ID

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:getCourse>
         <id>1</id>
      </cou:getCourse>
   </soapenv:Body>
</soapenv:Envelope>'
```

### Enroll Student

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:enrollStudent>
         <studentId>1001</studentId>
         <courseId>1</courseId>
      </cou:enrollStudent>
   </soapenv:Body>
</soapenv:Envelope>'
```

## Business Logic Validations

### Enrollment Validations

- ✅ Course must be active
- ✅ Enrollment period must be valid
- ✅ Course capacity not exceeded
- ✅ No duplicate enrollment
- ✅ Prerequisites must be met

### Schedule Validations

- ✅ No room/time conflicts
- ✅ End time must be after start time
- ✅ Course must exist

### Course Validations

- ✅ Unique course code
- ✅ Cannot delete with enrollments
- ✅ Valid capacity and credits

## Sample Data

**Pre-loaded Courses:**

1. **CS101** - Introduction to Programming (Capacity: 40)
2. **CS201** - Data Structures (Capacity: 35, Prereq: CS101)
3. **MATH101** - Calculus I (Capacity: 50)
4. **ENG101** - English Composition (Capacity: 30)

**Pre-loaded Schedules:**

- Each course has 2 schedules (different days)
- Times: Various (9:00-10:30, 11:00-12:30, 14:00-15:30)
- Rooms: A101, A102, M201, E101

## Database Schema

### courses

```sql
id, code, name, description, credits, semester, capacity,
enrolled_count, prerequisites, enrollment_start_date,
enrollment_end_date, is_active, created_at, updated_at
```

### schedules

```sql
id, course_id, day_of_week, start_time, end_time,
room, building, created_at
```

### student_courses

```sql
id, student_id, course_id, enrollment_date, status,
grade, created_at, updated_at
```

### teacher_courses

```sql
id, teacher_id, course_id, role, assigned_at
```

## Environment Variables

### Docker

- `ENVIRONMENT=docker` - Use Docker database connection
- `JAVA_OPTS` - JVM options (default: "-Xms256m -Xmx512m")

### Database (Configured in database.properties)

- `db.url` - Local database URL
- `db.docker.url` - Docker database URL
- `db.username` - Database username
- `db.password` - Database password

## Troubleshooting

### Service Won't Start

```bash
# Check if port 8083 is available
netstat -an | findstr :8083

# Check Docker logs
docker-compose logs courses_service

# Check application logs
cat services/courses/logs/courses-service.log
```

### WSDL Not Accessible

```bash
# Verify service is running
curl http://localhost:8083/CourseService?wsdl

# Check health
docker-compose ps courses_service
```

### Database Connection Issues

```bash
# Check database is running
docker-compose ps courses_db

# Check database logs
docker-compose logs courses_db

# Test database connection
docker-compose exec courses_db psql -U postgres -d courses_db
```

### Enrollment Fails

- Check course capacity: `SELECT * FROM courses WHERE id = ?;`
- Check prerequisites: `SELECT prerequisites FROM courses WHERE id = ?;`
- Check enrollment period: `SELECT enrollment_start_date, enrollment_end_date FROM courses WHERE id = ?;`
- Check existing enrollment: `SELECT * FROM student_courses WHERE student_id = ? AND course_id = ?;`

## Performance Tips

1. **Connection Pooling**: Already configured (10 max, 5 min idle)
2. **Database Indexes**: Already created on frequently queried columns
3. **Prepared Statements**: Used throughout DAO layer
4. **Logging**: Set to INFO in production, DEBUG in development

## API Gateway Integration

The service is accessible through the API Gateway at:

- **Gateway URL**: http://localhost:8080/api/courses/\*\*
- **Direct URL**: http://localhost:8083/CourseService

The gateway provides:

- Rate limiting
- Circuit breaker
- Request logging
- CORS handling

## Files Structure

```
courses/
├── src/main/java/           # Java source code
├── src/main/resources/      # Configuration files
├── test-soap-requests/      # Sample SOAP requests
├── pom.xml                  # Maven dependencies
├── Dockerfile               # Container definition
├── README.md                # Full documentation
├── TESTING.md               # Testing guide
├── IMPLEMENTATION_SUMMARY.md # Implementation details
├── build.sh                 # Build script (Bash)
└── build.ps1                # Build script (PowerShell)
```

## Support Resources

- **Full Documentation**: README.md
- **Testing Guide**: TESTING.md
- **Implementation Details**: IMPLEMENTATION_SUMMARY.md
- **WSDL**: http://localhost:8083/CourseService?wsdl
- **Sample Requests**: test-soap-requests/ directory

---

_Quick Reference - Version 1.0.0_
