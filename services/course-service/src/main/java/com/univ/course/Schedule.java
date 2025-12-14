// Fichier : course-service/src/main/java/com/univ/course/Schedule.java

package com.univ.course;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime; // Nécessite Java 8+ pour LocalTime

@Entity
@Table(name = "schedule")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Utiliser EAGER pour simplifier la logique
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "day", nullable = false)
    private String day; // Lundi, Mardi, etc.

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "room", nullable = false)
    private String room;

    public Schedule() {}

    // Getters
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getDay() { return day; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getRoom() { return room; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCourse(Course course) { this.course = course; }
    public void setDay(String day) { this.day = day; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setRoom(String room) { this.room = room; }
}