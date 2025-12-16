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
@XmlRootElement(name = "TeacherCourse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "teacherId", "courseId", "role", "assignedAt"})
public class TeacherCourse {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String role;
    private String assignedAt;
}
