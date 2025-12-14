// Fichier : course-service/src/main/java/com/univ/course/ScheduleDTO.java

package com.univ.course;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for Schedule public representation.
 */
public class ScheduleDTO implements Serializable {

    private Long id;
    private String day; 
    private String room;
    private LocalTime startTime;
    private LocalTime endTime;

    public ScheduleDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}