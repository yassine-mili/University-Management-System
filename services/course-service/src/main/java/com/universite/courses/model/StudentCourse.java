package com.universite.courses.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "StudentCourse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "studentId", "courseId", "enrollmentDate", "status", "grade", "createdAt", "updatedAt"})
public class StudentCourse {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String enrollmentDate;
    private String status;
    private String grade;
    private String createdAt;
    private String updatedAt;
}
