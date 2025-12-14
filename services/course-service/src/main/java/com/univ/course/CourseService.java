// Fichier : course-service/src/main/java/com/univ/course/CourseService.java

package com.univ.course;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "CourseServicePort")
public interface CourseService {

    // CRUD des cours
    @WebMethod Course createCourse(@WebParam(name = "course") Course course);
    @WebMethod Course getCourse(@WebParam(name = "code") String code);
    @WebMethod Course updateCourse(@WebParam(name = "course") Course course);
    @WebMethod boolean deleteCourse(@WebParam(name = "courseId") Long courseId);
    @WebMethod List<Course> listCourses(@WebParam(name = "semesterFilter") String semesterFilter);

    // Gestion des horaires
    @WebMethod Schedule addSchedule(@WebParam(name = "courseCode") String courseCode, @WebParam(name = "schedule") Schedule schedule) throws Exception;
    @WebMethod List<Schedule> getScheduleByCourse(@WebParam(name = "courseCode") String courseCode);

    // Inscription des étudiants
    @WebMethod Enrollment enrollStudent(@WebParam(name = "studentId") Long studentId, @WebParam(name = "courseCode") String courseCode) throws Exception;
    @WebMethod List<Course> getStudentCourses(@WebParam(name = "studentId") Long studentId);
}