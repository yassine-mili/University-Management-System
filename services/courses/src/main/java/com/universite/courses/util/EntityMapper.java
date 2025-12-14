package main.java.com.universite.courses.util;

import com.universite.courses.dto.CourseDTO;
import com.universite.courses.dto.EnrollmentDTO;
import com.universite.courses.dto.ScheduleDTO;
import com.universite.courses.entity.Course;
import com.universite.courses.entity.Schedule;
import com.universite.courses.entity.StudentCourse;

import java.util.stream.Collectors;

public class EntityMapper {
    
    public CourseDTO toDTO(Course course) {
        if (course == null) return null;
        
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        dto.setSemester(course.getSemester());
        dto.setCapacity(course.getCapacity());
        dto.setEnrolled(course.getEnrolled());
        dto.setDepartment(course.getDepartment());
        dto.setLevel(course.getLevel());
        dto.setPrerequisiteCourseIds(course.getPrerequisiteCourseIds());
        dto.setActive(course.getActive());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setAvailableSeats(course.getAvailableSeats());
        
        // Map schedules if loaded
        if (course.getSchedules() != null && !course.getSchedules().isEmpty()) {
            dto.setSchedules(course.getSchedules().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public ScheduleDTO toDTO(Schedule schedule) {
        if (schedule == null) return null;
        
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setCourseId(schedule.getCourse().getId());
        dto.setCourseCode(schedule.getCourse().getCode());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setRoom(schedule.getRoom());
        dto.setBuilding(schedule.getBuilding());
        dto.setScheduleType(schedule.getScheduleType());
        
        return dto;
    }
    
    public EnrollmentDTO toEnrollmentDTO(StudentCourse studentCourse) {
        if (studentCourse == null) return null;
        
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(studentCourse.getId());
        dto.setStudentId(studentCourse.getStudentId());
        dto.setCourseId(studentCourse.getCourse().getId());
        dto.setCourseCode(studentCourse.getCourse().getCode());
        dto.setCourseName(studentCourse.getCourse().getName());
        dto.setEnrollmentStatus(studentCourse.getEnrollmentStatus());
        dto.setEnrolledAt(studentCourse.getEnrolledAt());
        dto.setDroppedAt(studentCourse.getDroppedAt());
        dto.setGrade(studentCourse.getGrade());
        dto.setGradeLetter(studentCourse.getGradeLetter());
        
        return dto;
    }
}
