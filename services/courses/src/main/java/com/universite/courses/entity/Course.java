package main.java.com.universite.courses.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "Course code is required")
    @Pattern(regexp = "^[A-Z]{2,4}\\d{3,4}$", message = "Invalid course code format")
    private String code;
    
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 200, message = "Course name must be between 3 and 200 characters")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits cannot exceed 10")
    private Integer credits;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Semester is required")
    @Pattern(regexp = "^(Fall|Spring|Summer)\\s\\d{4}$", message = "Semester format: Fall 2024, Spring 2024, or Summer 2024")
    private String semester;
    
    @Column(nullable = false)
    @Min(value = 10, message = "Capacity must be at least 10")
    @Max(value = 100, message = "Capacity cannot exceed 100")
    private Integer capacity = 30;
    
    @Column(nullable = false)
    private Integer enrolled = 0;
    
    @Column(length = 50)
    private String department;
    
    @Column(length = 20)
    private String level; // Undergraduate, Graduate, Doctoral
    
    @Column(name = "prerequisite_course_ids")
    private String prerequisiteCourseIds; // Comma-separated course IDs
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeacherCourse> teacherCourses = new HashSet<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCourse> studentCourses = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enrolled == null) {
            enrolled = 0;
        }
        if (capacity == null) {
            capacity = 30;
        }
        if (active == null) {
            active = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean hasAvailableSeats() {
        return enrolled < capacity;
    }
    
    public int getAvailableSeats() {
        return capacity - enrolled;
    }
    
    public boolean isFull() {
        return enrolled >= capacity;
    }
    
    public void incrementEnrollment() {
        this.enrolled++;
    }
    
    public void decrementEnrollment() {
        if (this.enrolled > 0) {
            this.enrolled--;
        }
    }
}
