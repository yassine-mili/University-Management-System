# Courses Service - SOAP Testing Guide

## Overview

This guide provides comprehensive testing procedures for the Courses Service SOAP Web Service.

## Prerequisites

- Courses Service running on `http://localhost:8083`
- SOAP testing tool: SoapUI, Postman, or curl
- PostgreSQL database accessible

## WSDL Location

```
http://localhost:8083/CourseService?wsdl
```

## Testing Tools

### Option 1: SoapUI (Recommended)

1. **Create New SOAP Project**

   - File → New SOAP Project
   - Project Name: "University Courses Service"
   - Initial WSDL: `http://localhost:8083/CourseService?wsdl`
   - Click OK

2. **Import WSDL**

   - SoapUI will automatically generate requests for all operations
   - Expand the project tree to see all operations

3. **Execute Requests**
   - Double-click any operation
   - Fill in parameters
   - Click green "Play" button

### Option 2: Postman

1. **Create New Request**

   - Method: POST
   - URL: `http://localhost:8083/CourseService`
   - Headers: `Content-Type: text/xml`

2. **Set Request Body**

   - Select "raw" and "XML"
   - Paste SOAP envelope from examples below

3. **Send Request**
   - Click "Send"
   - View response in response panel

### Option 3: curl (Command Line)

```bash
curl -X POST \
  http://localhost:8083/CourseService \
  -H 'Content-Type: text/xml' \
  -d @request.xml
```

## Test Scenarios

### 1. Health Check

**Purpose**: Verify service is running

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:health/>
   </soapenv:Body>
</soapenv:Envelope>
```

**Expected Response**:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:healthResponse xmlns:ns2="http://courses.universite.com/">
         <return>Courses Service is running</return>
      </ns2:healthResponse>
   </soap:Body>
</soap:Envelope>
```

### 2. Create Course

**Purpose**: Add a new course to the system

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:createCourse>
         <code>CS101</code>
         <name>Introduction to Programming</name>
         <description>Learn fundamental programming concepts using Java</description>
         <credits>3</credits>
         <semester>Fall 2024</semester>
         <capacity>30</capacity>
         <department>Computer Science</department>
         <level>Undergraduate</level>
      </cour:createCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

**Expected Response**:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:createCourseResponse xmlns:ns2="http://courses.universite.com/">
         <return>
            <id>1</id>
            <code>CS101</code>
            <name>Introduction to Programming</name>
            <description>Learn fundamental programming concepts using Java</description>
            <credits>3</credits>
            <semester>Fall 2024</semester>
            <capacity>30</capacity>
            <enrolled>0</enrolled>
            <department>Computer Science</department>
            <level>Undergraduate</level>
            <active>true</active>
            <availableSeats>30</availableSeats>
         </return>
      </ns2:createCourseResponse>
   </soap:Body>
</soap:Envelope>
```

### 3. Get Course by ID

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getCourse>
         <courseId>1</courseId>
      </cour:getCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

### 4. Get Course by Code

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getCourseByCode>
         <code>CS101</code>
      </cour:getCourseByCode>
   </soapenv:Body>
</soapenv:Envelope>
```

### 5. List All Courses

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:listCourses/>
   </soapenv:Body>
</soapenv:Envelope>
```

### 6. List Courses by Semester

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:listCoursesBySemester>
         <semester>Fall 2024</semester>
      </cour:listCoursesBySemester>
   </soapenv:Body>
</soapenv:Envelope>
```

### 7. List Courses by Department

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:listCoursesByDepartment>
         <department>Computer Science</department>
      </cour:listCoursesByDepartment>
   </soapenv:Body>
</soapenv:Envelope>
```

### 8. List Available Courses

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:listAvailableCourses/>
   </soapenv:Body>
</soapenv:Envelope>
```

### 9. Update Course

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:updateCourse>
         <courseId>1</courseId>
         <name>Advanced Programming</name>
         <description>Updated course description</description>
         <credits>4</credits>
         <capacity>40</capacity>
      </cour:updateCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

### 10. Add Schedule

**SOAP Request**:

```xml
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
```

**Valid Days**: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

**Time Format**: HH:mm (24-hour format)

### 11. Get Schedule by Course

**SOAP Request**:

```xml
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

### 12. Enroll Student

**SOAP Request**:

```xml
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
```

**Expected Response**:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:enrollStudentResponse xmlns:ns2="http://courses.universite.com/">
         <return>
            <id>1</id>
            <studentId>123</studentId>
            <courseId>1</courseId>
            <courseCode>CS101</courseCode>
            <courseName>Introduction to Programming</courseName>
            <enrollmentStatus>ENROLLED</enrollmentStatus>
            <enrolledAt>2024-12-08T...</enrolledAt>
         </return>
      </ns2:enrollStudentResponse>
   </soap:Body>
</soap:Envelope>
```

### 13. Get Student Courses

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getStudentCourses>
         <studentId>123</studentId>
      </cour:getStudentCourses>
   </soapenv:Body>
</soapenv:Envelope>
```

### 14. Get Course Enrollments (Roster)

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getCourseEnrollments>
         <courseId>1</courseId>
      </cour:getCourseEnrollments>
   </soapenv:Body>
</soapenv:Envelope>
```

### 15. Drop Course

**SOAP Request**:

```xml
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

### 16. Assign Teacher

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:assignTeacher>
         <teacherId>456</teacherId>
         <courseId>1</courseId>
         <role>Primary Instructor</role>
      </cour:assignTeacher>
   </soapenv:Body>
</soapenv:Envelope>
```

**Valid Roles**: Primary Instructor, Co-Instructor, Teaching Assistant

### 17. Get Teacher Courses

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:getTeacherCourses>
         <teacherId>456</teacherId>
      </cour:getTeacherCourses>
   </soapenv:Body>
</soapenv:Envelope>
```

### 18. Delete Schedule

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:deleteSchedule>
         <scheduleId>1</scheduleId>
      </cour:deleteSchedule>
   </soapenv:Body>
</soapenv:Envelope>
```

### 19. Delete Course

**SOAP Request**:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cour="http://courses.universite.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cour:deleteCourse>
         <courseId>1</courseId>
      </cour:deleteCourse>
   </soapenv:Body>
</soapenv:Envelope>
```

## Test Flow Example

### Complete Course Management Flow

```bash
# 1. Create a course
# 2. Add schedule for the course
# 3. Enroll student in the course
# 4. Assign teacher to the course
# 5. Get all information
# 6. Drop student from course
# 7. Delete schedule
# 8. Delete course (if no enrollments)
```

## Error Testing

### Test: Create Duplicate Course

**Expected**: SOAP Fault - Course already exists

### Test: Enroll in Full Course

1. Create course with capacity=1
2. Enroll 2 students
   **Expected**: Second enrollment fails with SOAP Fault

### Test: Schedule Conflict

1. Add schedule for Room A101, Monday 9-10
2. Try to add another schedule for Room A101, Monday 9:30-11
   **Expected**: SOAP Fault - Schedule conflict

### Test: Drop Non-enrolled Student

**Expected**: SOAP Fault - Enrollment not found

### Test: Invalid Course Code Format

**Expected**: SOAP Fault - Validation error

## Integration Testing

### Test with API Gateway

```bash
# Through API Gateway
curl -X POST \
  http://localhost:8080/api/courses/CourseService \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
  -H 'Content-Type: text/xml' \
  -d '<soapenv:Envelope>...</soapenv:Envelope>'
```

## Automated Testing Script

Save as `test-courses.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8083/CourseService"

echo "Testing Courses Service..."

# Test 1: Health Check
echo "1. Health Check"
curl -X POST $BASE_URL \
  -H 'Content-Type: text/xml' \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cour="http://courses.universite.com/"><soapenv:Body><cour:health/></soapenv:Body></soapenv:Envelope>'

echo ""
echo "2. Create Course"
# Add more tests...
```

## Performance Testing

### Load Test with JMeter

1. Create Thread Group with 100 users
2. Add SOAP Request Sampler
3. Configure WSDL URL
4. Run test and analyze results

## Troubleshooting

### SOAP Fault: Connection Refused

**Solution**: Verify service is running on port 8083

### SOAP Fault: Invalid XML

**Solution**: Check XML syntax, namespaces, and element names

### Empty Response

**Solution**: Check server logs for errors

### Database Errors

**Solution**: Verify PostgreSQL is running and accessible

## Success Criteria

✅ All CRUD operations working  
✅ Schedule conflict detection working  
✅ Enrollment capacity validation working  
✅ WSDL accessible and valid  
✅ Error responses are meaningful SOAP faults  
✅ Integration with API Gateway successful

## Next Steps

After successful testing:

1. Document test results
2. Create automated test suite
3. Integrate with CI/CD pipeline
4. Deploy to production environment
5. Monitor service performance
