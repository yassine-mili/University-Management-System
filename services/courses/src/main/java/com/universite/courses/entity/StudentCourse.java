package com.universite.courses.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;
    
    @Column(name = "enrollment_status", nullable = false, length = 20)
    private String enrollmentStatus; // ENROLLED, DROPPED, COMPLETED, WITHDRAWN
    
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;
    
    @Column(name = "dropped_at")
    private LocalDateTime droppedAt;
    
    @Column(precision = 5, scale = 2)
    private Double grade;
    
    @Column(name = "grade_letter", length = 2)
    private String gradeLetter; // A+, A, A-, B+, etc.
    
    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
        if (enrollmentStatus == null) {
            enrollmentStatus = "ENROLLED";
        }
    }
    
    // Business methods
    public boolean isActive() {
        return "ENROLLED".equals(enrollmentStatus);
    }
    
    public void drop() {
        this.enrollmentStatus = "DROPPED";
        this.droppedAt = LocalDateTime.now();
    }
    
    public void complete(Double grade, String gradeLetter) {
        this.enrollmentStatus = "COMPLETED";
        this.grade = grade;
        this.gradeLetter = gradeLetter;
    }
}
