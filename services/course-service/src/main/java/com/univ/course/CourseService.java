// Fichier : course-service/src/main/java/com/univ/course/CourseService.java

package com.univ.course;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "CourseServicePort")
public interface CourseService {

    // CRUD des cours - NOW USING CourseDTO
    @WebMethod CourseDTO createCourse(@WebParam(name = "course") CourseDTO course);
    @WebMethod CourseDTO getCourse(@WebParam(name = "code") String code);
    @WebMethod CourseDTO updateCourse(@WebParam(name = "course") CourseDTO course);
    @WebMethod boolean deleteCourse(@WebParam(name = "courseId") Long courseId);
    @WebMethod List<CourseDTO> listCourses(@WebParam(name = "semesterFilter") String semesterFilter);

    // Gestion des horaires - KEEPING ENTITY FOR NOW (or would use ScheduleDTO)
    @WebMethod Schedule addSchedule(@WebParam(name = "courseCode") String courseCode, @WebParam(name = "schedule") Schedule schedule) throws Exception;
    @WebMethod List<Schedule> getScheduleByCourse(@WebParam(name = "courseCode") String courseCode);

    // Inscription des étudiants - KEEPING ENTITY FOR NOW (or would use EnrollmentDTO)
    @WebMethod Enrollment enrollStudent(@WebParam(name = "studentId") Long studentId, @WebParam(name = "courseCode") String courseCode) throws Exception;
    @WebMethod List<CourseDTO> getStudentCourses(@WebParam(name = "studentId") Long studentId);
}