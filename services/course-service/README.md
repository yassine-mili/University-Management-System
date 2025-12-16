# Courses Service - JAX-WS SOAP Web Service

## Overview

This is a SOAP web service built with JAX-WS for managing university courses, schedules, and student enrollments.

## Technology Stack

- **Java 17**
- **JAX-WS 4.0** (Jakarta XML Web Services)
- **PostgreSQL 15** (Database)
- **HikariCP** (Connection Pooling)
- **Maven** (Build Tool)
- **Docker** (Containerization)

## Features

### Course Management

- Create, read, update, and delete courses
- List all courses or filter by semester
- Track course capacity and enrollment count
- Manage prerequisites

### Schedule Management

- Add course schedules (day, time, room)
- Automatic schedule conflict detection
- View schedules by course

### Enrollment Management

- Enroll students in courses
- Unenroll students
- View student courses
- View course enrollments

### Business Logic Validations

- **Capacity Management**: Prevents enrollment when course is full
- **Schedule Conflict Detection**: Validates room availability
- **Prerequisites Validation**: Ensures students meet prerequisites
- **Enrollment Period Checks**: Validates enrollment dates
- **Duplicate Enrollment Prevention**: Prevents double enrollment

## Database Schema

### Tables

1. **courses**: Course information
2. **schedules**: Course schedules
3. **student_courses**: Student enrollments
4. **teacher_courses**: Teacher assignments

## SOAP Operations

### Course Operations

```xml
<!-- Create Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:createCourse>
         <code>CS301</code>
         <name>Advanced Algorithms</name>
         <description>Advanced algorithm design and analysis</description>
         <credits>4</credits>
         <semester>Fall 2024</semester>
         <capacity>35</capacity>
         <prerequisites>CS201</prerequisites>
         <enrollmentStartDate>2024-08-01 00:00:00</enrollmentStartDate>
         <enrollmentEndDate>2024-09-15 23:59:59</enrollmentEndDate>
      </cou:createCourse>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:getCourse>
         <id>1</id>
      </cou:getCourse>
   </soapenv:Body>
</soapenv:Envelope>

<!-- List All Courses -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:listCourses/>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Update Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:updateCourse>
         <id>1</id>
         <name>Introduction to Programming - Updated</name>
         <description>Updated description</description>
         <credits>3</credits>
         <semester>Fall 2024</semester>
         <capacity>45</capacity>
         <prerequisites></prerequisites>
         <enrollmentStartDate>2024-08-01 00:00:00</enrollmentStartDate>
         <enrollmentEndDate>2024-09-20 23:59:59</enrollmentEndDate>
         <isActive>true</isActive>
      </cou:updateCourse>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Delete Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:deleteCourse>
         <id>5</id>
      </cou:deleteCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

### Schedule Operations

```xml
<!-- Add Schedule -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:addSchedule>
         <courseId>1</courseId>
         <dayOfWeek>Monday</dayOfWeek>
         <startTime>09:00:00</startTime>
         <endTime>10:30:00</endTime>
         <room>A101</room>
         <building>Computer Science Building</building>
      </cou:addSchedule>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Schedule By Course -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:getScheduleByCourse>
         <courseId>1</courseId>
      </cou:getScheduleByCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

### Enrollment Operations

```xml
<!-- Enroll Student -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:enrollStudent>
         <studentId>1001</studentId>
         <courseId>1</courseId>
      </cou:enrollStudent>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Student Courses -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:getStudentCourses>
         <studentId>1001</studentId>
      </cou:getStudentCourses>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Unenroll Student -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:unenrollStudent>
         <studentId>1001</studentId>
         <courseId>1</courseId>
      </cou:unenrollStudent>
   </soapenv:Body>
</soapenv:Envelope>

<!-- Get Course With Schedules -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cou="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cou:getCourseWithSchedules>
         <id>1</id>
      </cou:getCourseWithSchedules>
   </soapenv:Body>
</soapenv:Envelope>
```

## Running the Service

### Local Development

```bash
# Build the project
mvn clean package

# Run the service
java -jar target/courses-service-jar-with-dependencies.jar
```

### Using Docker

```bash
# Build Docker image
docker build -t courses-service:latest .

# Run with Docker
docker run -p 8083:8083 -e ENVIRONMENT=docker courses-service:latest
```

### Using Docker Compose

```bash
# From the project root
cd docker
docker-compose up courses-service
```

### Docker Compose (recommended)

These are the exact commands used during development and testing (rebuild + start):

```bash
cd "C:/Users/R I B/Desktop/cv projects/University-Management-System/docker"
docker-compose down courses_service || true
docker-compose build --no-cache courses_service
docker-compose up -d courses_db courses_service
docker-compose logs -f courses_service
```

Allow ~10-20s for the service to start and for the database pool to initialize. The service publishes at `http://localhost:8083/CourseService` and the WSDL is available at `http://localhost:8083/CourseService?wsdl`.

### Quick SOAP Examples (curl)

Create a sample course (CS301) and then list courses to verify persistence. The XML payloads are available under `services/course-service/test-soap-requests`.

```bash
# Create CS301 (uses test-soap-requests/04-create-course.xml)
curl -v -X POST http://localhost:8083/CourseService \
   -H "Content-Type: text/xml" \
   -H "SOAPAction: createCourse" \
   -d @services/course-service/test-soap-requests/04-create-course.xml

# List courses (verify CS301 appears)
curl -v -X POST http://localhost:8083/CourseService \
   -H "Content-Type: text/xml" \
   -H "SOAPAction: listCourses" \
   -d @services/course-service/test-soap-requests/02-list-courses.xml
```

## Service Endpoints

- **Service URL**: `http://localhost:8083/CourseService`
- **WSDL URL**: `http://localhost:8083/CourseService?wsdl`

## Testing with SoapUI or Postman

1. **Import WSDL**: Use the WSDL URL to import service definition
2. **Select Operation**: Choose from available operations
3. **Fill Parameters**: Provide required parameters
4. **Send Request**: Execute the SOAP request
5. **View Response**: Check the SOAP response

## Environment Variables

- `ENVIRONMENT`: Set to `docker` when running in Docker
- `JAVA_OPTS`: JVM options (default: `-Xms256m -Xmx512m`)

## Database Configuration

### Local

```properties
db.url=jdbc:postgresql://localhost:5432/courses_db
db.username=postgres
db.password=postgres
```

### Docker

```properties
db.docker.url=jdbc:postgresql://courses-db:5432/courses_db
```

## Error Handling

The service provides detailed error messages for:

- Course not found
- Duplicate course codes
- Capacity exceeded
- Schedule conflicts
- Prerequisites not met
- Enrollment period violations
- Database errors

## Logging

Logs are written to:

- Console (STDOUT)
- File: `logs/courses-service.log`

Log levels:

- `DEBUG`: com.universite.courses
- `INFO`: Root logger

## Sample Data

The service includes sample data:

- 4 sample courses (CS101, CS201, MATH101, ENG101)
- 8 sample schedules
- Ready for immediate testing

## API Gateway Integration

The service is designed to work with the API Gateway on port 8080:

- Gateway route: `/api/courses/**`
- Direct access: Port 8083
- WSDL accessible at: `/CourseService?wsdl`
