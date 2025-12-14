// Fichier : course-service/src/main/java/com/univ/course/TeacherCourse.java

package com.univ.course;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "teacher_course")
public class TeacherCourse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // Clé externe vers le Service Enseignant

    public TeacherCourse() {}

    // Getters
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Long getTeacherId() { return teacherId; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCourse(Course course) { this.course = course; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}