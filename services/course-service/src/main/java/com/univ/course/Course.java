// Fichier : course-service/src/main/java/com/univ/course/Course.java

package com.univ.course;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "capacity")
    private Integer capacity; // Pour la gestion de la capacité (Business Logic)

    @Column(name = "semester")
    private String semester; // Ex: S1, S2

    // Relations OneToMany
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TeacherCourse> teachers = new HashSet<>();

    public Course() {}

    // Getters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getCredits() { return credits; }
    public Integer getCapacity() { return capacity; }
    public String getSemester() { return semester; }
    public Set<Schedule> getSchedules() { return schedules; }
    public Set<Enrollment> getEnrollments() { return enrollments; }
    public Set<TeacherCourse> getTeachers() { return teachers; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setSchedules(Set<Schedule> schedules) { this.schedules = schedules; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }
    public void setTeachers(Set<TeacherCourse> teachers) { this.teachers = teachers; }
}