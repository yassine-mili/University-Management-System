package com.universite.courses.service;

import com.universite.courses.model.*;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(name = "CourseService", targetNamespace = "http://courses.universite.com/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ICourseService {

    @WebMethod
    Course createCourse(
            @WebParam(name = "code") String code,
            @WebParam(name = "name") String name,
            @WebParam(name = "description") String description,
            @WebParam(name = "credits") Integer credits,
            @WebParam(name = "semester") String semester,
            @WebParam(name = "capacity") Integer capacity,
            @WebParam(name = "prerequisites") String prerequisites,
            @WebParam(name = "enrollmentStartDate") String enrollmentStartDate,
            @WebParam(name = "enrollmentEndDate") String enrollmentEndDate
    ) throws Exception;

    @WebMethod
    Course getCourse(@WebParam(name = "id") Long id) throws Exception;

    @WebMethod
    Course getCourseByCode(@WebParam(name = "code") String code) throws Exception;

    @WebMethod
    Course updateCourse(
            @WebParam(name = "id") Long id,
            @WebParam(name = "name") String name,
            @WebParam(name = "description") String description,
            @WebParam(name = "credits") Integer credits,
            @WebParam(name = "semester") String semester,
            @WebParam(name = "capacity") Integer capacity,
            @WebParam(name = "prerequisites") String prerequisites,
            @WebParam(name = "enrollmentStartDate") String enrollmentStartDate,
            @WebParam(name = "enrollmentEndDate") String enrollmentEndDate,
            @WebParam(name = "isActive") Boolean isActive
    ) throws Exception;

    @WebMethod
    boolean deleteCourse(@WebParam(name = "id") Long id) throws Exception;

    @WebMethod
    CourseList listCourses() throws Exception;

    @WebMethod
    CourseList listCoursesBySemester(@WebParam(name = "semester") String semester) throws Exception;

    @WebMethod
    Schedule addSchedule(
            @WebParam(name = "courseId") Long courseId,
            @WebParam(name = "dayOfWeek") String dayOfWeek,
            @WebParam(name = "startTime") String startTime,
            @WebParam(name = "endTime") String endTime,
            @WebParam(name = "room") String room,
            @WebParam(name = "building") String building
    ) throws Exception;

    @WebMethod
    ScheduleList getScheduleByCourse(@WebParam(name = "courseId") Long courseId) throws Exception;

    @WebMethod
    boolean deleteSchedule(@WebParam(name = "id") Long id) throws Exception;

    @WebMethod
    StudentCourse enrollStudent(
            @WebParam(name = "studentId") Long studentId,
            @WebParam(name = "courseId") Long courseId
    ) throws Exception;

    @WebMethod
    boolean unenrollStudent(
            @WebParam(name = "studentId") Long studentId,
            @WebParam(name = "courseId") Long courseId
    ) throws Exception;

    @WebMethod
    StudentCourseList getStudentCourses(@WebParam(name = "studentId") Long studentId) throws Exception;

    @WebMethod
    StudentCourseList getCourseEnrollments(@WebParam(name = "courseId") Long courseId) throws Exception;

    @WebMethod
    CourseWithSchedules getCourseWithSchedules(@WebParam(name = "id") Long id) throws Exception;

    @WebMethod
    String getServiceInfo() throws Exception;
}
