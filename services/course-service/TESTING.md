# SOAP Testing Guide

## Using curl

### 1. Get Service Info

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getServiceInfo" \
  -d @test-soap-requests/01-service-info.xml
```

### 2. List All Courses

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: listCourses" \
  -d @test-soap-requests/02-list-courses.xml
```

### 3. Get Course by ID

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getCourse" \
  -d @test-soap-requests/03-get-course.xml
```

### 4. Create New Course

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: createCourse" \
  -d @test-soap-requests/04-create-course.xml
```

### 5. Get Course Schedules

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getScheduleByCourse" \
  -d @test-soap-requests/05-get-schedules.xml
```

### 6. Enroll Student

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: enrollStudent" \
  -d @test-soap-requests/06-enroll-student.xml
```

### 7. Get Student Courses

`````bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getStudentCourses" \
  -d @test-soap-requests/07-get-student-courses.xml
````markdown
# SOAP Testing Guide

## Using curl

### 1. Get Service Info

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getServiceInfo" \
  -d @test-soap-requests/01-service-info.xml
`````

### 2. List All Courses

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: listCourses" \
  -d @test-soap-requests/02-list-courses.xml
```

### 3. Get Course by ID

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getCourse" \
  -d @test-soap-requests/03-get-course.xml
```

### 4. Create New Course

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: createCourse" \
  -d @test-soap-requests/04-create-course.xml
```

### 5. Get Course Schedules

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getScheduleByCourse" \
  -d @test-soap-requests/05-get-schedules.xml
```

### 6. Enroll Student

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: enrollStudent" \
  -d @test-soap-requests/06-enroll-student.xml
```

### 7. Get Student Courses

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getStudentCourses" \
  -d @test-soap-requests/07-get-student-courses.xml
```

### 8. Get Course with Schedules

```bash
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: getCourseWithSchedules" \
  -d @test-soap-requests/08-get-course-with-schedules.xml
```

## Using PowerShell

### Get WSDL

```powershell
Invoke-WebRequest -Uri "http://localhost:8083/CourseService?wsdl" -Method Get
```

### Send SOAP Request

```powershell
$headers = @{
    "Content-Type" = "text/xml"
    "SOAPAction" = "listCourses"
}

$body = Get-Content -Path "test-soap-requests/02-list-courses.xml" -Raw

Invoke-WebRequest -Uri "http://localhost:8083/CourseService" `
    -Method POST `
    -Headers $headers `
    -Body $body
```

## Using Postman

1. Create a new request
2. Set method to POST
3. Set URL to: `http://localhost:8083/CourseService`
4. Go to Headers tab:
   - Add `Content-Type: text/xml`
   - Add `SOAPAction: [operation-name]`
5. Go to Body tab:
   - Select "raw"
   - Paste the SOAP XML from test files
6. Click Send

## Using SoapUI

1. File → New SOAP Project
2. Enter Project Name: "University Courses Service"
3. Enter Initial WSDL: `http://localhost:8083/CourseService?wsdl`
4. Click OK
5. Expand the project tree to see all operations
6. Double-click any operation to test
7. Fill in the request parameters
8. Click the green play button to execute

## Expected Response Format

### Success Response

```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:listCoursesResponse xmlns:ns2="http://courses.universite.com/">
         <return>
            <id>1</id>
            <code>CS101</code>
            <name>Introduction to Programming</name>
            <!-- ... more fields ... -->
         </return>
      </ns2:listCoursesResponse>
   </S:Body>
</S:Envelope>
```

### Error Response

```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault>
         <faultcode>S:Server</faultcode>
         <faultstring>Course not found with ID: 999</faultstring>
      </S:Fault>
   </S:Body>
</S:Envelope>
```

## Testing Workflow

1. **Start the service**:

   ```bash
   # From project root
   cd docker
   # Optional: tear down old service
   docker-compose down courses_service || true
   # Rebuild the service image (use --no-cache if you edited Java sources)
   docker-compose build --no-cache courses_service
   # Start DB and service
   docker-compose up -d courses_db courses_service
   # Follow logs to confirm published status
   docker-compose logs -f courses_service
   ```

2. **Verify WSDL is accessible**:

   ```bash
   curl http://localhost:8083/CourseService?wsdl
   ```

3. **Run test requests in order**:

   - 01: Service info (basic connectivity)
   - 02: List courses (view sample data)
   - 03: Get course (retrieve specific course)
   - 04: Create course (test insertion)
   - 05: Get schedules (test relationships)
   - 06: Enroll student (test business logic)
   - 07: Get student courses (verify enrollment)
   - 08: Get course with schedules (complex query)

4. **Test error scenarios**:
   - Try enrolling same student twice (duplicate prevention)
   - Try enrolling in full course (capacity validation)
   - Try enrolling without prerequisites (prerequisite validation)
   - Try creating course with duplicate code
   - Try accessing non-existent course

## Validation Tests

### Test Capacity Management

```xml
<!-- Enroll 40 students in CS101 (capacity: 40) -->
<!-- 41st enrollment should fail with capacity error -->
```

### Test Schedule Conflicts

```xml
<!-- Add schedule for Monday 9:00-10:30 in Room A101 -->
<!-- Try adding another schedule for same time/room should fail -->
```

### Test Prerequisites

```xml
<!-- Try enrolling student in CS201 without completing CS101 -->
<!-- Should fail with prerequisite error -->
```

### Test Enrollment Period

```xml
<!-- Try enrolling outside the enrollment dates -->
<!-- Should fail with enrollment period error -->
```

## Quick smoke test (create + list)

After the service is running, run:

```bash
# create course (uses prewritten XML in test-soap-requests)
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: createCourse" \
  -d @test-soap-requests/04-create-course.xml

# list courses to verify persistence
curl -X POST http://localhost:8083/CourseService \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: listCourses" \
  -d @test-soap-requests/02-list-courses.xml
```

```

```
