package com.universite.courses.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "day_of_week", "start_time", "room"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;
    
    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Room is required")
    @Size(min = 2, max = 50, message = "Room must be between 2 and 50 characters")
    private String room;
    
    @Column(length = 100)
    @Size(max = 100, message = "Building name cannot exceed 100 characters")
    private String building;
    
    @Column(name = "schedule_type", length = 20)
    private String scheduleType; // Lecture, Lab, Tutorial, Seminar
    
    @PrePersist
    @PreUpdate
    protected void validate() {
        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
    
    // Business methods
    public boolean conflictsWith(Schedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        
        // Check if time ranges overlap
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }
    
    public boolean isInSameRoom(Schedule other) {
        return this.room.equalsIgnoreCase(other.room) && 
               (this.building == null || other.building == null || 
                this.building.equalsIgnoreCase(other.building));
    }
}
