package com.universite.courses.service;

import com.universite.courses.client.ServiceClients;
import com.universite.courses.dao.CourseDAO;
import com.universite.courses.dao.EnrollmentDAO;
import com.universite.courses.dao.ScheduleDAO;
import com.universite.courses.exception.CourseServiceException;
import com.universite.courses.model.Course;
import com.universite.courses.model.Schedule;
import com.universite.courses.model.StudentCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class CourseBusinessLogic {
    private static final Logger logger = LoggerFactory.getLogger(CourseBusinessLogic.class);
    private final CourseDAO courseDAO;
    private final ScheduleDAO scheduleDAO;
    private final EnrollmentDAO enrollmentDAO;

    public CourseBusinessLogic() {
        this.courseDAO = new CourseDAO();
        this.scheduleDAO = new ScheduleDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    /**
     * Validate course capacity before enrollment
     */
    public void validateCapacity(Long courseId) throws CourseServiceException {
        try {
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + courseId);
            }

            int currentEnrollment = enrollmentDAO.countEnrollmentsByCourse(courseId);
            if (currentEnrollment >= course.getCapacity()) {
                throw new CourseServiceException("Course is at full capacity. Capacity: " + 
                        course.getCapacity() + ", Current enrollment: " + currentEnrollment);
            }
        } catch (SQLException e) {
            logger.error("Database error while validating capacity", e);
            throw new CourseServiceException("Failed to validate course capacity", e);
        }
    }

    /**
     * Validate schedule conflicts
     */
    public void validateScheduleConflict(Schedule schedule) throws CourseServiceException {
        try {
            boolean hasConflict = scheduleDAO.hasScheduleConflict(
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getRoom(),
                    schedule.getId()
            );

            if (hasConflict) {
                throw new CourseServiceException(
                        String.format("Schedule conflict detected for room %s on %s from %s to %s",
                                schedule.getRoom(), schedule.getDayOfWeek(),
                                schedule.getStartTime(), schedule.getEndTime())
                );
            }
        } catch (SQLException e) {
            logger.error("Database error while validating schedule conflict", e);
            throw new CourseServiceException("Failed to validate schedule conflict", e);
        }
    }

    /**
     * Validate prerequisites for course enrollment
     */
    public void validatePrerequisites(Long studentId, Long courseId) throws CourseServiceException {
        try {
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + courseId);
            }

            String prerequisites = course.getPrerequisites();
            if (prerequisites != null && !prerequisites.trim().isEmpty()) {
                // Parse prerequisites (comma-separated course codes)
                String[] requiredCourses = prerequisites.split(",");
                
                // Get student's completed courses
                List<StudentCourse> studentCourses = enrollmentDAO.findByStudentId(studentId);
                
                for (String prereqCode : requiredCourses) {
                    prereqCode = prereqCode.trim();
                    Course prereqCourse = courseDAO.findByCode(prereqCode);
                    
                    if (prereqCourse != null) {
                        boolean hasCompleted = studentCourses.stream()
                                .anyMatch(sc -> sc.getCourseId().equals(prereqCourse.getId()) &&
                                        ("COMPLETED".equals(sc.getStatus()) || 
                                         "PASSED".equals(sc.getStatus())));
                        
                        if (!hasCompleted) {
                            throw new CourseServiceException(
                                    "Prerequisite not met: Student must complete " + prereqCode + 
                                    " before enrolling in this course"
                            );
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while validating prerequisites", e);
            throw new CourseServiceException("Failed to validate prerequisites", e);
        }
    }

    /**
     * Validate enrollment period
     */
    public void validateEnrollmentPeriod(Long courseId) throws CourseServiceException {
        try {
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + courseId);
            }

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (course.getEnrollmentStartDate() != null) {
                LocalDateTime startDate = LocalDateTime.parse(
                        course.getEnrollmentStartDate().substring(0, 19), formatter);
                if (now.isBefore(startDate)) {
                    throw new CourseServiceException(
                            "Enrollment has not started yet. Enrollment begins on: " + 
                            course.getEnrollmentStartDate()
                    );
                }
            }

            if (course.getEnrollmentEndDate() != null) {
                LocalDateTime endDate = LocalDateTime.parse(
                        course.getEnrollmentEndDate().substring(0, 19), formatter);
                if (now.isAfter(endDate)) {
                    throw new CourseServiceException(
                            "Enrollment period has ended. Enrollment closed on: " + 
                            course.getEnrollmentEndDate()
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while validating enrollment period", e);
            throw new CourseServiceException("Failed to validate enrollment period", e);
        }
    }

    /**
     * Validate if student is already enrolled
     */
    public void validateDuplicateEnrollment(Long studentId, Long courseId) throws CourseServiceException {
        try {
            StudentCourse existing = enrollmentDAO.findByStudentAndCourse(studentId, courseId);
            if (existing != null) {
                throw new CourseServiceException(
                        "Student is already enrolled in this course. Enrollment ID: " + existing.getId()
                );
            }
        } catch (SQLException e) {
            logger.error("Database error while checking duplicate enrollment", e);
            throw new CourseServiceException("Failed to validate enrollment", e);
        }
    }

    /**
     * Validate course status
     */
    public void validateCourseActive(Long courseId) throws CourseServiceException {
        try {
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + courseId);
            }

            if (!course.getIsActive()) {
                throw new CourseServiceException(
                        "Course is not currently active. Course code: " + course.getCode()
                );
            }
        } catch (SQLException e) {
            logger.error("Database error while validating course status", e);
            throw new CourseServiceException("Failed to validate course status", e);
        }
    }

    /**
     * Complete enrollment with all validations
     */
    public StudentCourse performEnrollment(Long studentId, Long courseId) throws CourseServiceException {
        String traceId = UUID.randomUUID().toString();
        
        // Skip student validation for now as student service requires authentication
        // TODO: Implement proper authentication when calling student service
        logger.info("[{}] Enrolling student {} in course {} (student validation skipped)", traceId, studentId, courseId);
        
        // Run all course validations
        validateCourseActive(courseId);
        validateEnrollmentPeriod(courseId);
        validateCapacity(courseId);
        validateDuplicateEnrollment(studentId, courseId);
        validatePrerequisites(studentId, courseId);

        try {
            // Perform enrollment
            StudentCourse enrollment = enrollmentDAO.enroll(studentId, courseId);
            
            // Update enrolled count
            courseDAO.updateEnrolledCount(courseId, 1);
            
            logger.info("[{}] Successfully enrolled student {} in course {}", traceId, studentId, courseId);
            return enrollment;
            
        } catch (SQLException e) {
            logger.error("[{}] Database error during enrollment", traceId, e);
            throw new CourseServiceException("Failed to complete enrollment", e);
        }
    }
}
