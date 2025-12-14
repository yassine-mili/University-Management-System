// Fichier : course-service/src/main/java/com/univ/course/Enrollment.java

package com.univ.course;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
public class Enrollment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "student_id", nullable = false)
    private Long studentId; // Clé externe vers le Service Étudiant

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    public Enrollment() {}

    // Getters
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Long getStudentId() { return studentId; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCourse(Course course) { this.course = course; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}