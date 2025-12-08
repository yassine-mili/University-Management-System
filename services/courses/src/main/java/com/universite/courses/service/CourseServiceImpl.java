package main.java.com.universite.courses.service;

import com.universite.courses.dto.CourseDTO;
import com.universite.courses.dto.EnrollmentDTO;
import com.universite.courses.dto.ScheduleDTO;
import com.universite.courses.entity.*;
import com.universite.courses.repository.*;
import com.universite.courses.util.EntityMapper;
import jakarta.jws.WebService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@WebService(
    serviceName = "CourseService",
    portName = "CourseServicePort",
    targetNamespace = "http://courses.universite.com/",
    endpointInterface = "com.universite.courses.service.ICourseService"
)
@Slf4j
public class CourseServiceImpl implements ICourseService {
    
    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final EntityMapper entityMapper;
    
    public CourseServiceImpl(EntityManager entityManager) {
        this.courseRepository = new CourseRepository(entityManager);
        this.scheduleRepository = new ScheduleRepository(entityManager);
        this.studentCourseRepository = new StudentCourseRepository(entityManager);
        this.teacherCourseRepository = new TeacherCourseRepository(entityManager);
        this.entityMapper = new EntityMapper();
    }
    
    @Override
    public CourseDTO createCourse(String code, String name, String description, 
                                  Integer credits, String semester, Integer capacity,
                                  String department, String level) {
        try {
            log.info("Creating course: {}", code);
            
            // Validation
            if (courseRepository.existsByCode(code)) {
                throw new IllegalArgumentException("Course with code " + code + " already exists");
            }
            
            if (capacity < 10 || capacity > 100) {
                throw new IllegalArgumentException("Capacity must be between 10 and 100");
            }
            
            // Create course entity
            Course course = new Course();
            course.setCode(code);
            course.setName(name);
            course.setDescription(description);
            course.setCredits(credits);
            course.setSemester(semester);
            course.setCapacity(capacity);
            course.setDepartment(department);
            course.setLevel(level);
            course.setActive(true);
            course.setEnrolled(0);
            
            // Save course
            course = courseRepository.save(course);
            
            log.info("Course created successfully: {}", code);
            return entityMapper.toDTO(course);
            
        } catch (Exception e) {
            log.error("Error creating course: {}", e.getMessage());
            throw new RuntimeException("Failed to create course: " + e.getMessage());
        }
    }
    
    @Override
    public CourseDTO getCourse(Long courseId) {
        try {
            log.info("Fetching course with ID: {}", courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            return entityMapper.toDTO(course);
            
        } catch (Exception e) {
            log.error("Error fetching course: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch course: " + e.getMessage());
        }
    }
    
    @Override
    public CourseDTO getCourseByCode(String code) {
        try {
            log.info("Fetching course with code: {}", code);
            
            Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + code));
            
            return entityMapper.toDTO(course);
            
        } catch (Exception e) {
            log.error("Error fetching course by code: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch course: " + e.getMessage());
        }
    }
    
    @Override
    public CourseDTO updateCourse(Long courseId, String name, String description, 
                                  Integer credits, Integer capacity) {
        try {
            log.info("Updating course with ID: {}", courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            if (name != null && !name.trim().isEmpty()) {
                course.setName(name);
            }
            if (description != null) {
                course.setDescription(description);
            }
            if (credits != null && credits > 0 && credits <= 10) {
                course.setCredits(credits);
            }
            if (capacity != null) {
                if (capacity < course.getEnrolled()) {
                    throw new IllegalArgumentException("Cannot reduce capacity below current enrollment");
                }
                if (capacity < 10 || capacity > 100) {
                    throw new IllegalArgumentException("Capacity must be between 10 and 100");
                }
                course.setCapacity(capacity);
            }
            
            course = courseRepository.save(course);
            
            log.info("Course updated successfully: {}", course.getCode());
            return entityMapper.toDTO(course);
            
        } catch (Exception e) {
            log.error("Error updating course: {}", e.getMessage());
            throw new RuntimeException("Failed to update course: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteCourse(Long courseId) {
        try {
            log.info("Deleting course with ID: {}", courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            // Check if course has enrollments
            if (course.getEnrolled() > 0) {
                throw new IllegalArgumentException("Cannot delete course with active enrollments");
            }
            
            courseRepository.delete(course);
            
            log.info("Course deleted successfully: {}", course.getCode());
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting course: {}", e.getMessage());
            throw new RuntimeException("Failed to delete course: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseDTO> listCourses() {
        try {
            log.info("Fetching all courses");
            
            List<Course> courses = courseRepository.findAll();
            return courses.stream()
                .map(entityMapper::toDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error listing courses: {}", e.getMessage());
            throw new RuntimeException("Failed to list courses: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseDTO> listCoursesBySemester(String semester) {
        try {
            log.info("Fetching courses for semester: {}", semester);
            
            List<Course> courses = courseRepository.findBySemester(semester);
            return courses.stream()
                .map(entityMapper::toDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error listing courses by semester: {}", e.getMessage());
            throw new RuntimeException("Failed to list courses by semester: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseDTO> listCoursesByDepartment(String department) {
        try {
            log.info("Fetching courses for department: {}", department);
            
            List<Course> courses = courseRepository.findByDepartment(department);
            return courses.stream()
                .map(entityMapper::toDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error listing courses by department: {}", e.getMessage());
            throw new RuntimeException("Failed to list courses by department: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseDTO> listAvailableCourses() {
        try {
            log.info("Fetching available courses");
            
            List<Course> courses = courseRepository.findAvailableCourses();
            return courses.stream()
                .map(entityMapper::toDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error listing available courses: {}", e.getMessage());
            throw new RuntimeException("Failed to list available courses: " + e.getMessage());
        }
    }
    
    @Override
    public ScheduleDTO addSchedule(Long courseId, String dayOfWeek, String startTime, 
                                   String endTime, String room, String building, String scheduleType) {
        try {
            log.info("Adding schedule for course ID: {}", courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            // Create schedule
            Schedule schedule = new Schedule();
            schedule.setCourse(course);
            schedule.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek.toUpperCase()));
            schedule.setStartTime(LocalTime.parse(startTime));
            schedule.setEndTime(LocalTime.parse(endTime));
            schedule.setRoom(room);
            schedule.setBuilding(building);
            schedule.setScheduleType(scheduleType);
            
            // Check for schedule conflicts
            List<Schedule> existingSchedules = scheduleRepository.findByRoom(room);
            for (Schedule existing : existingSchedules) {
                if (schedule.conflictsWith(existing)) {
                    throw new IllegalArgumentException("Schedule conflicts with existing schedule in room " + room);
                }
            }
            
            schedule = scheduleRepository.save(schedule);
            
            log.info("Schedule added successfully for course: {}", course.getCode());
            return entityMapper.toDTO(schedule);
            
        } catch (Exception e) {
            log.error("Error adding schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to add schedule: " + e.getMessage());
        }
    }
    
    @Override
    public List<ScheduleDTO> getScheduleByCourse(Long courseId) {
        try {
            log.info("Fetching schedules for course ID: {}", courseId);
            
            List<Schedule> schedules = scheduleRepository.findByCourseId(courseId);
            return schedules.stream()
                .map(entityMapper::toDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error fetching schedules: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch schedules: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteSchedule(Long scheduleId) {
        try {
            log.info("Deleting schedule with ID: {}", scheduleId);
            
            Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
            
            scheduleRepository.delete(schedule);
            
            log.info("Schedule deleted successfully");
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to delete schedule: " + e.getMessage());
        }
    }
    
    @Override
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        try {
            log.info("Enrolling student {} in course {}", studentId, courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            // Check if course is full
            if (course.isFull()) {
                throw new IllegalArgumentException("Course is full");
            }
            
            // Check if student is already enrolled
            if (studentCourseRepository.isStudentEnrolled(studentId, courseId)) {
                throw new IllegalArgumentException("Student is already enrolled in this course");
            }
            
            // Create enrollment
            StudentCourse enrollment = new StudentCourse();
            enrollment.setStudentId(studentId);
            enrollment.setCourse(course);
            enrollment.setEnrollmentStatus("ENROLLED");
            
            enrollment = studentCourseRepository.save(enrollment);
            
            // Update course enrollment count
            course.incrementEnrollment();
            courseRepository.save(course);
            
            log.info("Student {} enrolled successfully in course {}", studentId, course.getCode());
            return entityMapper.toEnrollmentDTO(enrollment);
            
        } catch (Exception e) {
            log.error("Error enrolling student: {}", e.getMessage());
            throw new RuntimeException("Failed to enroll student: " + e.getMessage());
        }
    }
    
    @Override
    public boolean dropCourse(Long studentId, Long courseId) {
        try {
            log.info("Student {} dropping course {}", studentId, courseId);
            
            StudentCourse enrollment = studentCourseRepository.findByStudentAndCourse(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
            
            if (!"ENROLLED".equals(enrollment.getEnrollmentStatus())) {
                throw new IllegalArgumentException("Student is not currently enrolled in this course");
            }
            
            enrollment.drop();
            studentCourseRepository.save(enrollment);
            
            // Update course enrollment count
            Course course = enrollment.getCourse();
            course.decrementEnrollment();
            courseRepository.save(course);
            
            log.info("Student {} dropped course {} successfully", studentId, courseId);
            return true;
            
        } catch (Exception e) {
            log.error("Error dropping course: {}", e.getMessage());
            throw new RuntimeException("Failed to drop course: " + e.getMessage());
        }
    }
    
    @Override
    public List<EnrollmentDTO> getStudentCourses(Long studentId) {
        try {
            log.info("Fetching courses for student: {}", studentId);
            
            List<StudentCourse> enrollments = studentCourseRepository.findByStudent(studentId);
            return enrollments.stream()
                .map(entityMapper::toEnrollmentDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error fetching student courses: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch student courses: " + e.getMessage());
        }
    }
    
    @Override
    public List<EnrollmentDTO> getCourseEnrollments(Long courseId) {
        try {
            log.info("Fetching enrollments for course: {}", courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            List<StudentCourse> enrollments = studentCourseRepository.findByCourse(course);
            return enrollments.stream()
                .map(entityMapper::toEnrollmentDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error fetching course enrollments: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch course enrollments: " + e.getMessage());
        }
    }
    
    @Override
    public boolean assignTeacher(Long teacherId, Long courseId, String role) {
        try {
            log.info("Assigning teacher {} to course {}", teacherId, courseId);
            
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
            
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setTeacherId(teacherId);
            teacherCourse.setCourse(course);
            teacherCourse.setRole(role);
            teacherCourse.setActive(true);
            
            teacherCourseRepository.save(teacherCourse);
            
            log.info("Teacher {} assigned to course {} successfully", teacherId, course.getCode());
            return true;
            
        } catch (Exception e) {
            log.error("Error assigning teacher: {}", e.getMessage());
            throw new RuntimeException("Failed to assign teacher: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseDTO> getTeacherCourses(Long teacherId) {
        try {
            log.info("Fetching courses for teacher: {}", teacherId);
            
            List<TeacherCourse> teacherCourses = teacherCourseRepository.findByTeacher(teacherId);
            return teacherCourses.stream()
                .map(tc -> entityMapper.toDTO(tc.getCourse()))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error fetching teacher courses: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch teacher courses: " + e.getMessage());
        }
    }
    
    @Override
    public String health() {
        return "Courses Service is running";
    }
}
