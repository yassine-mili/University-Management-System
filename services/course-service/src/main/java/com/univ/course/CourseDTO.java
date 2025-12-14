// Fichier : course-service/src/main/java/com/univ/course/CourseDTO.java

package com.univ.course;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object (DTO) for public Course representation.
 * Decouples the SOAP contract from the JPA entity.
 */
public class CourseDTO implements Serializable {

    private Long id;
    private String code;
    private String title;
    private String description;
    private Integer credits;
    private Integer capacity;
    private String semester;
    
    // Use DTOs for nested relationships in the public contract
    private List<ScheduleDTO> schedules; 

    // Constructors (required for easy serialization/deserialization in SOAP)
    public CourseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public List<ScheduleDTO> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleDTO> schedules) { this.schedules = schedules; }
}