package com.universite.courses.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Course")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "code", "name", "description", "credits", "semester", 
                      "capacity", "enrolledCount", "prerequisites", "enrollmentStartDate", 
                      "enrollmentEndDate", "isActive", "createdAt", "updatedAt"})
public class Course {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private String semester;
    private Integer capacity;
    private Integer enrolledCount;
    private String prerequisites;
    private String enrollmentStartDate;
    private String enrollmentEndDate;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
