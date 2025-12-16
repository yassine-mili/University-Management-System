package com.universite.courses.controller;

import com.universite.courses.model.*;
import com.universite.courses.service.CourseServiceImpl;
import com.universite.courses.service.ICourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller that wraps SOAP service operations
 * Provides RESTful API for course management
 */
@RestController
@RequestMapping("")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CourseRestController {
    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);
    private final ICourseService courseService;

    public CourseRestController() {
        this.courseService = new CourseServiceImpl();
        logger.info("CourseRestController initialized");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "courses-service");
        return ResponseEntity.ok(health);
    }

    /**
     * GET /courses - List all courses
     */
    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
        try {
            logger.info("REST: Getting all courses");
            CourseList courseList = courseService.listCourses();
            return ResponseEntity.ok(courseList.getCourses());
        } catch (Exception e) {
            logger.error("Error getting all courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/{id} - Get course by ID
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            logger.info("REST: Getting course by id: {}", id);
            Course course = courseService.getCourse(id);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            logger.error("Error getting course by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/code/{code} - Get course by code
     */
    @GetMapping("/courses/code/{code}")
    public ResponseEntity<?> getCourseByCode(@PathVariable String code) {
        try {
            logger.info("REST: Getting course by code: {}", code);
            Course course = courseService.getCourseByCode(code);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            logger.error("Error getting course by code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/semester/{semester} - Get courses by semester
     */
    @GetMapping("/courses/semester/{semester}")
    public ResponseEntity<?> getCoursesBySemester(@PathVariable String semester) {
        try {
            logger.info("REST: Getting courses by semester: {}", semester);
            CourseList courseList = courseService.listCoursesBySemester(semester);
            return ResponseEntity.ok(courseList.getCourses());
        } catch (Exception e) {
            logger.error("Error getting courses by semester: {}", semester, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /courses - Create new course
     */
    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request) {
        try {
            logger.info("REST: Creating course: {}", request.getCode());
            Course course = courseService.createCourse(
                    request.getCode(),
                    request.getName(),
                    request.getDescription(),
                    request.getCredits(),
                    request.getSemester(),
                    request.getCapacity(),
                    request.getPrerequisites(),
                    request.getEnrollmentStartDate(),
                    request.getEnrollmentEndDate()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(course);
        } catch (Exception e) {
            logger.error("Error creating course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /courses/{id} - Update course
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        try {
            logger.info("REST: Updating course: {}", id);
            Course course = courseService.updateCourse(
                    id,
                    request.getName(),
                    request.getDescription(),
                    request.getCredits(),
                    request.getSemester(),
                    request.getCapacity(),
                    request.getPrerequisites(),
                    request.getEnrollmentStartDate(),
                    request.getEnrollmentEndDate(),
                    request.getIsActive()
            );
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            logger.error("Error updating course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /courses/{id} - Delete course
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            logger.info("REST: Deleting course: {}", id);
            boolean deleted = courseService.deleteCourse(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/{id}/schedules - Get course schedules
     */
    @GetMapping("/courses/{id}/schedules")
    public ResponseEntity<?> getCourseSchedules(@PathVariable Long id) {
        try {
            logger.info("REST: Getting schedules for course: {}", id);
            ScheduleList scheduleList = courseService.getScheduleByCourse(id);
            return ResponseEntity.ok(scheduleList.getSchedules());
        } catch (Exception e) {
            logger.error("Error getting course schedules: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /courses/{id}/schedules - Add schedule to course
     */
    @PostMapping("/courses/{id}/schedules")
    public ResponseEntity<?> addSchedule(@PathVariable Long id, @RequestBody ScheduleRequest request) {
        try {
            logger.info("REST: Adding schedule to course: {}", id);
            Schedule schedule = courseService.addSchedule(
                    id,
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getRoom(),
                    request.getBuilding()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
        } catch (Exception e) {
            logger.error("Error adding schedule to course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /schedules/{id} - Delete schedule
     */
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            logger.info("REST: Deleting schedule: {}", id);
            boolean deleted = courseService.deleteSchedule(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Schedule deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting schedule: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /enrollments - Enroll student in course
     */
    @PostMapping("/enrollments")
    public ResponseEntity<?> enrollStudent(@RequestBody EnrollmentRequest request) {
        try {
            logger.info("REST: Enrolling student {} in course {}", request.getStudentId(), request.getCourseId());
            StudentCourse enrollment = courseService.enrollStudent(
                    request.getStudentId(),
                    request.getCourseId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
        } catch (Exception e) {
            logger.error("Error enrolling student", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /enrollments - Unenroll student from course
     */
    @DeleteMapping("/enrollments")
    public ResponseEntity<?> unenrollStudent(@RequestBody EnrollmentRequest request) {
        try {
            logger.info("REST: Unenrolling student {} from course {}", request.getStudentId(), request.getCourseId());
            boolean unenrolled = courseService.unenrollStudent(
                    request.getStudentId(),
                    request.getCourseId()
            );
            if (unenrolled) {
                return ResponseEntity.ok(Map.of("message", "Student unenrolled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error unenrolling student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /students/{studentId}/courses - Get student's courses
     */
    @GetMapping("/students/{studentId}/courses")
    public ResponseEntity<?> getStudentCourses(@PathVariable Long studentId) {
        try {
            logger.info("REST: Getting courses for student: {}", studentId);
            StudentCourseList courseList = courseService.getStudentCourses(studentId);
            return ResponseEntity.ok(courseList.getStudentCourses());
        } catch (Exception e) {
            logger.error("Error getting student courses: {}", studentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /courses/{courseId}/enroll - Enroll student in specific course
     */
    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId, @RequestBody Map<String, String> request) {
        try {
            String studentIdStr = request.get("studentId");
            logger.info("REST: Enrolling student {} in course {}", studentIdStr, courseId);
            
            // Convert studentId string to Long (e.g., "STU000025" -> need to get numeric ID)
            // For now, try to extract numeric part or use as is
            Long studentId;
            try {
                // If studentId is like "STU000025", extract the number
                if (studentIdStr.startsWith("STU")) {
                    studentId = Long.parseLong(studentIdStr.substring(3));
                } else {
                    studentId = Long.parseLong(studentIdStr);
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid student ID format"));
            }
            
            StudentCourse enrollment = courseService.enrollStudent(studentId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
        } catch (Exception e) {
            logger.error("Error enrolling student in course {}", courseId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /courses/{courseId}/enroll/{studentId} - Unenroll student from course
     */
    @DeleteMapping("/courses/{courseId}/enroll/{studentId}")
    public ResponseEntity<?> unenrollFromCourse(@PathVariable Long courseId, @PathVariable String studentId) {
        try {
            logger.info("REST: Unenrolling student {} from course {}", studentId, courseId);
            
            Long studentIdLong;
            try {
                if (studentId.startsWith("STU")) {
                    studentIdLong = Long.parseLong(studentId.substring(3));
                } else {
                    studentIdLong = Long.parseLong(studentId);
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid student ID format"));
            }
            
            boolean unenrolled = courseService.unenrollStudent(studentIdLong, courseId);
            if (unenrolled) {
                return ResponseEntity.ok(Map.of("message", "Student unenrolled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error unenrolling student from course {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /student/{studentId}/enrollments - Get student's enrolled courses
     */
    @GetMapping("/student/{studentId}/enrollments")
    public ResponseEntity<?> getStudentEnrollments(@PathVariable String studentId) {
        try {
            logger.info("REST: Getting enrollments for student: {}", studentId);
            
            Long studentIdLong;
            try {
                if (studentId.startsWith("STU")) {
                    studentIdLong = Long.parseLong(studentId.substring(3));
                } else {
                    studentIdLong = Long.parseLong(studentId);
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid student ID format"));
            }
            
            StudentCourseList courseList = courseService.getStudentCourses(studentIdLong);
            return ResponseEntity.ok(courseList.getStudentCourses());
        } catch (Exception e) {
            logger.error("Error getting student enrollments: {}", studentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/{courseId}/enrollments - Get course enrollments
     */
    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<?> getCourseEnrollments(@PathVariable Long courseId) {
        try {
            logger.info("REST: Getting enrollments for course: {}", courseId);
            StudentCourseList enrollmentList = courseService.getCourseEnrollments(courseId);
            return ResponseEntity.ok(enrollmentList.getStudentCourses());
        } catch (Exception e) {
            logger.error("Error getting course enrollments: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /courses/{id}/details - Get course with schedules
     */
    @GetMapping("/courses/{id}/details")
    public ResponseEntity<?> getCourseWithSchedules(@PathVariable Long id) {
        try {
            logger.info("REST: Getting course with schedules: {}", id);
            CourseWithSchedules courseDetails = courseService.getCourseWithSchedules(id);
            return ResponseEntity.ok(courseDetails);
        } catch (Exception e) {
            logger.error("Error getting course details: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Request DTOs
    public static class CourseRequest {
        private String code;
        private String name;
        private String description;
        private Integer credits;
        private String semester;
        private Integer capacity;
        private String prerequisites;
        private String enrollmentStartDate;
        private String enrollmentEndDate;
        private Boolean isActive = true;

        // Getters and setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public String getPrerequisites() { return prerequisites; }
        public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
        public String getEnrollmentStartDate() { return enrollmentStartDate; }
        public void setEnrollmentStartDate(String enrollmentStartDate) { this.enrollmentStartDate = enrollmentStartDate; }
        public String getEnrollmentEndDate() { return enrollmentEndDate; }
        public void setEnrollmentEndDate(String enrollmentEndDate) { this.enrollmentEndDate = enrollmentEndDate; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class ScheduleRequest {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String room;
        private String building;

        // Getters and setters
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        public String getBuilding() { return building; }
        public void setBuilding(String building) { this.building = building; }
    }

    public static class EnrollmentRequest {
        private Long studentId;
        private Long courseId;

        // Getters and setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
    }
}
