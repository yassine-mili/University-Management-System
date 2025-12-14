package com.universite.courses.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"teacher_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "teacher_id", nullable = false)
    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;
    
    @Column(length = 50)
    private String role; // Primary Instructor, Co-Instructor, Teaching Assistant
    
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
        if (role == null) {
            role = "Primary Instructor";
        }
    }
}
