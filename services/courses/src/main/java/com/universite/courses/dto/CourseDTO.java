package main.java.com.universite.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private String semester;
    private Integer capacity;
    private Integer enrolled;
    private String department;
    private String level;
    private String prerequisiteCourseIds;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ScheduleDTO> schedules = new ArrayList<>();
    private Integer availableSeats;
}
