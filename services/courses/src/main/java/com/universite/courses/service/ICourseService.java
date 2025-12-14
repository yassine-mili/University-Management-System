package main.java.com.universite.courses.service;

import com.universite.courses.dto.CourseDTO;
import com.universite.courses.dto.EnrollmentDTO;
import com.universite.courses.dto.ScheduleDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "CourseService", targetNamespace = "http://courses.universite.com/")
public interface ICourseService {
    
    // Course CRUD Operations
    @WebMethod
    CourseDTO createCourse(
        @WebParam(name = "code") String code,
        @WebParam(name = "name") String name,
        @WebParam(name = "description") String description,
        @WebParam(name = "credits") Integer credits,
        @WebParam(name = "semester") String semester,
        @WebParam(name = "capacity") Integer capacity,
        @WebParam(name = "department") String department,
        @WebParam(name = "level") String level
    );
    
    @WebMethod
    CourseDTO getCourse(@WebParam(name = "courseId") Long courseId);
    
    @WebMethod
    CourseDTO getCourseByCode(@WebParam(name = "code") String code);
    
    @WebMethod
    CourseDTO updateCourse(
        @WebParam(name = "courseId") Long courseId,
        @WebParam(name = "name") String name,
        @WebParam(name = "description") String description,
        @WebParam(name = "credits") Integer credits,
        @WebParam(name = "capacity") Integer capacity
    );
    
    @WebMethod
    boolean deleteCourse(@WebParam(name = "courseId") Long courseId);
    
    @WebMethod
    List<CourseDTO> listCourses();
    
    @WebMethod
    List<CourseDTO> listCoursesBySemester(@WebParam(name = "semester") String semester);
    
    @WebMethod
    List<CourseDTO> listCoursesByDepartment(@WebParam(name = "department") String department);
    
    @WebMethod
    List<CourseDTO> listAvailableCourses();
    
    // Schedule Operations
    @WebMethod
    ScheduleDTO addSchedule(
        @WebParam(name = "courseId") Long courseId,
        @WebParam(name = "dayOfWeek") String dayOfWeek,
        @WebParam(name = "startTime") String startTime,
        @WebParam(name = "endTime") String endTime,
        @WebParam(name = "room") String room,
        @WebParam(name = "building") String building,
        @WebParam(name = "scheduleType") String scheduleType
    );
    
    @WebMethod
    List<ScheduleDTO> getScheduleByCourse(@WebParam(name = "courseId") Long courseId);
    
    @WebMethod
    boolean deleteSchedule(@WebParam(name = "scheduleId") Long scheduleId);
    
    // Enrollment Operations
    @WebMethod
    EnrollmentDTO enrollStudent(
        @WebParam(name = "studentId") Long studentId,
        @WebParam(name = "courseId") Long courseId
    );
    
    @WebMethod
    boolean dropCourse(
        @WebParam(name = "studentId") Long studentId,
        @WebParam(name = "courseId") Long courseId
    );
    
    @WebMethod
    List<EnrollmentDTO> getStudentCourses(@WebParam(name = "studentId") Long studentId);
    
    @WebMethod
    List<EnrollmentDTO> getCourseEnrollments(@WebParam(name = "courseId") Long courseId);
    
    // Teacher Assignment Operations
    @WebMethod
    boolean assignTeacher(
        @WebParam(name = "teacherId") Long teacherId,
        @WebParam(name = "courseId") Long courseId,
        @WebParam(name = "role") String role
    );
    
    @WebMethod
    List<CourseDTO> getTeacherCourses(@WebParam(name = "teacherId") Long teacherId);
    
    // Health Check
    @WebMethod
    String health();
}
