package main.java.com.universite.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO implements Serializable {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String enrollmentStatus;
    private LocalDateTime enrolledAt;
    private LocalDateTime droppedAt;
    private Double grade;
    private String gradeLetter;
}
