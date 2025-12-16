package com.universite.courses.service;

import com.universite.courses.dao.CourseDAO;
import com.universite.courses.dao.EnrollmentDAO;
import com.universite.courses.dao.ScheduleDAO;
import com.universite.courses.exception.CourseServiceException;
import com.universite.courses.model.*;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

@WebService(
        name = "CourseService",
        targetNamespace = "http://courses.universite.com/",
        serviceName = "CourseService",
        portName = "CourseServicePort",
        endpointInterface = "com.universite.courses.service.ICourseService"
)
// @HandlerChain(file = "/handler-chain.xml")  // Disabled for now - REST API doesn't need SOAP handlers
public class CourseServiceImpl implements ICourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    
    private final CourseDAO courseDAO;
    private final ScheduleDAO scheduleDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final CourseBusinessLogic businessLogic;

    public CourseServiceImpl() {
        this.courseDAO = new CourseDAO();
        this.scheduleDAO = new ScheduleDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.businessLogic = new CourseBusinessLogic();
    }

    @Override
    public Course createCourse(String code, String name, String description, Integer credits,
                               String semester, Integer capacity, String prerequisites,
                               String enrollmentStartDate, String enrollmentEndDate) throws Exception {
        try {
            logger.info("Creating course: {}", code);
            
            // Validate course code is unique
            Course existing = courseDAO.findByCode(code);
            if (existing != null) {
                throw new CourseServiceException("Course with code " + code + " already exists");
            }

            Course course = Course.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .credits(credits)
                    .semester(semester)
                    .capacity(capacity != null ? capacity : 30)
                    .enrolledCount(0)
                    .prerequisites(prerequisites)
                    .enrollmentStartDate(enrollmentStartDate)
                    .enrollmentEndDate(enrollmentEndDate)
                    .isActive(true)
                    .build();

            return courseDAO.create(course);
        } catch (SQLException e) {
            logger.error("Database error creating course", e);
            throw new Exception("Failed to create course: " + e.getMessage());
        }
    }

    @Override
    public Course getCourse(Long id) throws Exception {
        try {
            logger.info("Retrieving course with id: {}", id);
            Course course = courseDAO.findById(id);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + id);
            }
            return course;
        } catch (SQLException e) {
            logger.error("Database error retrieving course", e);
            throw new Exception("Failed to retrieve course: " + e.getMessage());
        }
    }

    @Override
    public Course getCourseByCode(String code) throws Exception {
        try {
            logger.info("Retrieving course with code: {}", code);
            Course course = courseDAO.findByCode(code);
            if (course == null) {
                throw new CourseServiceException("Course not found with code: " + code);
            }
            return course;
        } catch (SQLException e) {
            logger.error("Database error retrieving course by code", e);
            throw new Exception("Failed to retrieve course: " + e.getMessage());
        }
    }

    @Override
    public Course updateCourse(Long id, String name, String description, Integer credits,
                               String semester, Integer capacity, String prerequisites,
                               String enrollmentStartDate, String enrollmentEndDate,
                               Boolean isActive) throws Exception {
        try {
            logger.info("Updating course with id: {}", id);
            
            Course existing = courseDAO.findById(id);
            if (existing == null) {
                throw new CourseServiceException("Course not found with ID: " + id);
            }

            Course course = Course.builder()
                    .id(id)
                    .code(existing.getCode()) // Code cannot be changed
                    .name(name)
                    .description(description)
                    .credits(credits)
                    .semester(semester)
                    .capacity(capacity)
                    .prerequisites(prerequisites)
                    .enrollmentStartDate(enrollmentStartDate)
                    .enrollmentEndDate(enrollmentEndDate)
                    .isActive(isActive)
                    .build();

            return courseDAO.update(course);
        } catch (SQLException e) {
            logger.error("Database error updating course", e);
            throw new Exception("Failed to update course: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteCourse(Long id) throws Exception {
        try {
            logger.info("Deleting course with id: {}", id);
            
            // Check if course has enrollments
            int enrollmentCount = enrollmentDAO.countEnrollmentsByCourse(id);
            if (enrollmentCount > 0) {
                throw new CourseServiceException(
                        "Cannot delete course with active enrollments. Current enrollments: " + enrollmentCount);
            }

            return courseDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Database error deleting course", e);
            throw new Exception("Failed to delete course: " + e.getMessage());
        }
    }

    @Override
    public CourseList listCourses() throws Exception {
        try {
            logger.info("Listing all courses");
            List<Course> courses = courseDAO.findAll();
            return CourseList.builder()
                    .courses(courses)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error listing courses", e);
            throw new Exception("Failed to list courses: " + e.getMessage());
        }
    }

    @Override
    public CourseList listCoursesBySemester(String semester) throws Exception {
        try {
            logger.info("Listing courses for semester: {}", semester);
            List<Course> courses = courseDAO.findBySemester(semester);
            return CourseList.builder()
                    .courses(courses)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error listing courses by semester", e);
            throw new Exception("Failed to list courses: " + e.getMessage());
        }
    }

    @Override
    public Schedule addSchedule(Long courseId, String dayOfWeek, String startTime,
                                String endTime, String room, String building) throws Exception {
        try {
            logger.info("Adding schedule for course: {}", courseId);
            
            // Validate course exists
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + courseId);
            }

            Schedule schedule = Schedule.builder()
                    .courseId(courseId)
                    .dayOfWeek(dayOfWeek)
                    .startTime(startTime)
                    .endTime(endTime)
                    .room(room)
                    .building(building)
                    .build();

            // Validate schedule conflicts
            businessLogic.validateScheduleConflict(schedule);

            return scheduleDAO.create(schedule);
        } catch (SQLException e) {
            logger.error("Database error adding schedule", e);
            throw new Exception("Failed to add schedule: " + e.getMessage());
        }
    }

    @Override
    public ScheduleList getScheduleByCourse(Long courseId) throws Exception {
        try {
            logger.info("Retrieving schedules for course: {}", courseId);
            List<Schedule> schedules = scheduleDAO.findByCourseId(courseId);
            return ScheduleList.builder()
                    .schedules(schedules)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error retrieving schedules", e);
            throw new Exception("Failed to retrieve schedules: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteSchedule(Long id) throws Exception {
        try {
            logger.info("Deleting schedule with id: {}", id);
            return scheduleDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Database error deleting schedule", e);
            throw new Exception("Failed to delete schedule: " + e.getMessage());
        }
    }

    @Override
    public StudentCourse enrollStudent(Long studentId, Long courseId) throws Exception {
        try {
            logger.info("Enrolling student {} in course {}", studentId, courseId);
            return businessLogic.performEnrollment(studentId, courseId);
        } catch (CourseServiceException e) {
            logger.warn("Enrollment validation failed: {}", e.getMessage());
            throw new Exception("Enrollment failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error enrolling student", e);
            throw new Exception("Failed to enroll student: " + e.getMessage());
        }
    }

    @Override
    public boolean unenrollStudent(Long studentId, Long courseId) throws Exception {
        try {
            logger.info("Unenrolling student {} from course {}", studentId, courseId);
            
            // Check if enrollment exists
            StudentCourse enrollment = enrollmentDAO.findByStudentAndCourse(studentId, courseId);
            if (enrollment == null) {
                throw new CourseServiceException("Student is not enrolled in this course");
            }

            boolean success = enrollmentDAO.unenroll(studentId, courseId);
            if (success) {
                // Update enrolled count
                courseDAO.updateEnrolledCount(courseId, -1);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Database error unenrolling student", e);
            throw new Exception("Failed to unenroll student: " + e.getMessage());
        }
    }

    @Override
    public StudentCourseList getStudentCourses(Long studentId) throws Exception {
        try {
            logger.info("Retrieving courses for student: {}", studentId);
            List<StudentCourse> studentCourses = enrollmentDAO.findByStudentId(studentId);
            return StudentCourseList.builder()
                    .studentCourses(studentCourses)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error retrieving student courses", e);
            throw new Exception("Failed to retrieve student courses: " + e.getMessage());
        }
    }

    @Override
    public StudentCourseList getCourseEnrollments(Long courseId) throws Exception {
        try {
            logger.info("Retrieving enrollments for course: {}", courseId);
            List<StudentCourse> enrollments = enrollmentDAO.findByCourseId(courseId);
            return StudentCourseList.builder()
                    .studentCourses(enrollments)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error retrieving course enrollments", e);
            throw new Exception("Failed to retrieve course enrollments: " + e.getMessage());
        }
    }

    @Override
    public CourseWithSchedules getCourseWithSchedules(Long id) throws Exception {
        try {
            logger.info("Retrieving course with schedules for id: {}", id);
            
            Course course = courseDAO.findById(id);
            if (course == null) {
                throw new CourseServiceException("Course not found with ID: " + id);
            }
            
            List<Schedule> schedules = scheduleDAO.findByCourseId(id);
            
            return CourseWithSchedules.builder()
                    .course(course)
                    .schedules(schedules)
                    .build();
        } catch (SQLException e) {
            logger.error("Database error retrieving course with schedules", e);
            throw new Exception("Failed to retrieve course with schedules: " + e.getMessage());
        }
    }

    @Override
    public String getServiceInfo() throws Exception {
        return "University Course Management SOAP Service v1.0.0 - " +
               "Provides course management, scheduling, and enrollment operations";
    }
}
