# Courses Service - University Management System

## Overview

The Courses Service is a **SOAP Web Service** built with **JAX-WS** that manages all course-related operations including course management, scheduling, student enrollment, and teacher assignments.

## Technology Stack

- **Java 17**
- **JAX-WS 4.0.1** - SOAP Web Service framework
- **Hibernate 6.2.13** - ORM for database operations
- **PostgreSQL 14** - Relational database
- **Jetty 11** - Embedded HTTP server
- **Maven** - Build tool
- **Docker** - Containerization

## Features

### 1. Course Management (CRUD)

- ✅ Create new courses with validation
- ✅ Retrieve course details by ID or code
- ✅ Update course information
- ✅ Delete courses (with enrollment checks)
- ✅ List all courses with filtering (by semester, department)
- ✅ Find available courses (with capacity check)

### 2. Schedule Management

- ✅ Add course schedules (day, time, room)
- ✅ Get schedules for specific courses
- ✅ Schedule conflict detection
- ✅ Room availability validation
- ✅ Delete schedules

### 3. Student Enrollment

- ✅ Enroll students in courses
- ✅ Drop courses
- ✅ Get student's enrolled courses
- ✅ Get course enrollments (roster)
- ✅ Capacity management (prevent over-enrollment)
- ✅ Enrollment status tracking (ENROLLED, DROPPED, COMPLETED, WITHDRAWN)

### 4. Teacher Assignment

- ✅ Assign teachers to courses
- ✅ Define teacher roles (Primary Instructor, Co-Instructor, TA)
- ✅ Get courses taught by a teacher
- ✅ Active status management

## Database Schema

### Tables

1. **courses** - Course information

   - id, code, name, description, credits, semester, capacity, enrolled, department, level, prerequisites, active

2. **schedules** - Course schedules

   - id, course_id, day_of_week, start_time, end_time, room, building, schedule_type

3. **student_courses** - Student enrollments

   - id, student_id, course_id, enrollment_status, enrolled_at, dropped_at, grade, grade_letter

4. **teacher_courses** - Teacher assignments
   - id, teacher_id, course_id, role, assigned_at, active

## SOAP Operations

### Course Operations

```xml
<!-- Create Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:createCourse>
         <code>CS101</code>
         <name>Introduction to Programming</name>
         <description>Learn programming basics</description>
         <credits>3</credits>
         <semester>Fall 2024</semester>
         <capacity>30</capacity>
         <department>Computer Science</department>
         <level>Undergraduate</level>
      </cour:createCourse>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getCourse>
         <courseId>1</courseId>
      </cour:getCourse>
   </soapenv:Body>
</soapenv:Envelope>

<!-- List Courses -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:listCourses/>
   </soapenv:Body>
</soapenv:Envelope>
```

### Schedule Operations

```xml
<!-- Add Schedule -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:addSchedule>
         <courseId>1</courseId>
         <dayOfWeek>MONDAY</dayOfWeek>
         <startTime>09:00</startTime>
         <endTime>10:30</endTime>
         <room>A101</room>
         <building>Main Building</building>
         <scheduleType>Lecture</scheduleType>
      </cour:addSchedule>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Course Schedule -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getScheduleByCourse>
         <courseId>1</courseId>
      </cour:getScheduleByCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

### Enrollment Operations

```xml
<!-- Enroll Student -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:enrollStudent>
         <studentId>123</studentId>
         <courseId>1</courseId>
      </cour:enrollStudent>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Student Courses -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getStudentCourses>
         <studentId>123</studentId>
      </cour:getStudentCourses>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Drop Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:dropCourse>
         <studentId>123</studentId>
         <courseId>1</courseId>
      </cour:dropCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

## Running the Service

### Prerequisites

- Java 17 or higher
- Maven 3.9.5 or higher
- PostgreSQL 14
- Docker (optional)

### Local Development

1. **Start PostgreSQL Database**

```bash
docker run -d \
  --name courses_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=courses_db \
  -p 5434:5432 \
  postgres:14-alpine
```

2. **Build the Project**

```bash
cd services/courses
mvn clean package
```

3. **Run the Service**

```bash
# Using Maven
mvn exec:java

# Or using the JAR
java -jar target/courses-service-jar-with-dependencies.jar

# Or using the run script
# Windows
run.bat

# Linux/macOS
chmod +x run.sh
./run.sh
```

4. **Verify Service is Running**

```bash
# Access WSDL
curl http://localhost:8083/CourseService?wsdl

# Health check
# Use SoapUI or Postman to call the health() operation
```

### Docker Deployment

```bash
# Build Docker image
docker build -t courses-service .

# Run container
docker run -d \
  --name courses_service \
  -p 8083:8083 \
  -e DB_URL=jdbc:postgresql://courses_db:5432/courses_db \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  courses-service
```

### Docker Compose

```bash
cd docker
docker-compose up -d courses_service
```

## Configuration

### Database Configuration

Located in `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5434/courses_db"/>
<property name="jakarta.persistence.jdbc.user" value="postgres"/>
<property name="jakarta.persistence.jdbc.password" value="postgres"/>
```

### Environment Variables

- `DB_URL` - Database JDBC URL
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password
- `PORT` - Service port (default: 8083)

## Testing

### Using SoapUI

1. Create new SOAP project
2. Enter WSDL URL: `http://localhost:8083/CourseService?wsdl`
3. SoapUI will generate all operations
4. Test each operation with sample data

### Using Postman

1. Create new request
2. Select POST method
3. URL: `http://localhost:8083/CourseService`
4. Body: Raw XML (application/xml)
5. Paste SOAP envelope from examples above

### Using curl

```bash
curl -X POST \
  http://localhost:8083/CourseService \
  -H 'Content-Type: text/xml' \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cour="http://courses.universite.com/">
        <soapenv:Header/>
        <soapenv:Body>
          <cour:health/>
        </soapenv:Body>
      </soapenv:Envelope>'
```

## Business Rules

### Course Capacity

- Minimum: 10 students
- Maximum: 100 students
- Enrollment blocked when course is full

### Schedule Conflicts

- Cannot double-book rooms at the same time
- Validates day, time, and room conflicts

### Prerequisites

- Stores prerequisite course IDs as comma-separated string
- Validation can be extended for enrollment checks

### Enrollment Status

- **ENROLLED** - Active enrollment
- **DROPPED** - Student dropped the course
- **COMPLETED** - Course completed with grade
- **WITHDRAWN** - Student withdrew from course

## Project Structure

```
courses/
├── src/main/java/com/universite/courses/
│   ├── CoursesServiceApplication.java      # Main application
│   ├── entity/
│   │   ├── Course.java                     # Course entity
│   │   ├── Schedule.java                   # Schedule entity
│   │   ├── StudentCourse.java              # Enrollment entity
│   │   └── TeacherCourse.java              # Teacher assignment entity
│   ├── repository/
│   │   ├── CourseRepository.java           # Course data access
│   │   ├── ScheduleRepository.java         # Schedule data access
│   │   ├── StudentCourseRepository.java    # Enrollment data access
│   │   └── TeacherCourseRepository.java    # Teacher assignment data access
│   ├── service/
│   │   ├── ICourseService.java             # SOAP interface
│   │   └── CourseServiceImpl.java          # SOAP implementation
│   ├── dto/
│   │   ├── CourseDTO.java                  # Course data transfer object
│   │   ├── ScheduleDTO.java                # Schedule DTO
│   │   ├── EnrollmentDTO.java              # Enrollment DTO
│   │   └── ServiceResponse.java            # Generic response
│   └── util/
│       ├── EntityMapper.java               # Entity-DTO mapper
│       └── DatabaseManager.java            # Database connection manager
├── src/main/resources/
│   ├── META-INF/persistence.xml            # JPA configuration
│   ├── application.properties              # App configuration
│   └── logback.xml                         # Logging configuration
├── Dockerfile                              # Docker build
├── pom.xml                                 # Maven configuration
├── run.bat                                 # Windows run script
└── run.sh                                  # Linux run script
```

## API Endpoints

### WSDL

`http://localhost:8083/CourseService?wsdl`

### SOAP Endpoint

`http://localhost:8083/CourseService`

## Error Handling

All operations throw SOAP faults for errors:

- Invalid input parameters
- Course not found
- Enrollment capacity reached
- Schedule conflicts
- Database errors

## Logging

Logs are written to:

- Console (STDOUT)
- File: `logs/courses-service.log`
- Rolling policy: Daily with 30-day retention

## Performance

- Connection pooling via HikariCP
- Batch operations enabled
- Lazy loading for relationships
- Indexes on frequently queried columns

## Security Notes

⚠️ This is a demonstration service. For production:

- Add WS-Security for authentication
- Implement SSL/TLS
- Add input validation and sanitization
- Implement rate limiting
- Add audit logging

## Integration with API Gateway

The service is accessed through the API Gateway at:
`http://localhost:8080/api/courses/**`

The gateway will forward SOAP requests to this service.

## Troubleshooting

### Service won't start

- Check if port 8083 is available
- Verify PostgreSQL is running
- Check database connection settings

### Database connection errors

- Verify PostgreSQL is accessible
- Check credentials in persistence.xml
- Ensure database `courses_db` exists

### WSDL not accessible

- Service may still be starting (wait 10-15 seconds)
- Check logs for startup errors
- Verify Jetty server started successfully

## Next Steps

1. ✅ Courses Service implemented (SOAP)
2. 🔄 Test with SoapUI/Postman
3. 📊 Integrate with API Gateway
4. 🚀 Deploy to production
5. 📝 Create API documentation

## License

University Management System - Educational Project
