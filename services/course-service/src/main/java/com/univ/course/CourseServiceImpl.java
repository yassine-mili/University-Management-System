// Fichier : course-service/src/main/java/com/univ/course/CourseServiceImpl.java

package com.univ.course;

import jakarta.jws.WebService;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebService(endpointInterface = "com.univ.course.CourseService")
public class CourseServiceImpl implements CourseService {

    private final EntityManagerFactory emf;

    /**
     * Constructor for use by CourseServicePublisher (accepts dynamic properties).
     */
    public CourseServiceImpl(Map<String, String> jpaProperties) {
        // Initializes the EMF using dynamically passed configuration (Fix 1)
        this.emf = Persistence.createEntityManagerFactory("CourseUnit", jpaProperties);
    }
    
    /**
     * Default constructor for fallback/testing (reads from persistence.xml).
     */
    public CourseServiceImpl() {
        this.emf = Persistence.createEntityManagerFactory("CourseUnit"); 
    }

    private EntityManager getEntityManager() { 
        return emf.createEntityManager(); 
    }

    // --- MAPPING UTILITY METHODS (Fix 3) ---

    // Convert Entity (DB) to DTO (SOAP)
    private CourseDTO toDto(Course entity) {
        if (entity == null) return null;
        CourseDTO dto = new CourseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCredits(entity.getCredits());
        dto.setCapacity(entity.getCapacity());
        dto.setSemester(entity.getSemester());
        // Nested entities (schedules, enrollments) would be mapped to DTOs here
        return dto;
    }
    
    // Convert DTO (SOAP) to Entity (DB)
    private Course toEntity(CourseDTO dto) {
        if (dto == null) return null;
        Course entity = new Course();
        entity.setId(dto.getId()); 
        entity.setCode(dto.getCode());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCredits(dto.getCredits());
        entity.setCapacity(dto.getCapacity());
        entity.setSemester(dto.getSemester());
        // Nested DTOs would be mapped to Entities here
        return entity;
    }
    
    // --- LOGIQUE MÉTIER ---

    private boolean checkScheduleConflict(EntityManager em, Schedule newSchedule) {
        // Logique de détection de conflit inchangée
        List<Schedule> conflicts = em.createQuery(
                        "SELECT s FROM Schedule s WHERE s.day = :day AND s.room = :room AND " +
                                "(:newStart < s.endTime AND :newEnd > s.startTime)", Schedule.class)
                .setParameter("day", newSchedule.getDay())
                .setParameter("room", newSchedule.getRoom())
                .setParameter("newStart", newSchedule.getStartTime())
                .setParameter("newEnd", newSchedule.getEndTime())
                .getResultList();

        return !conflicts.isEmpty();
    }

    // Efficient enrollment count using JPQL (Fix 2)
    private boolean checkCapacity(EntityManager em, Course course) {
        // Use a direct COUNT query to avoid lazy loading issues
        Long currentEnrollmentCount = em.createQuery(
                "SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId", Long.class)
                .setParameter("courseId", course.getId())
                .getSingleResult();

        return currentEnrollmentCount < course.getCapacity();
    }

    // --- OPÉRATIONS CRUD COURSE (Using DTOs) ---

    @Override
    public CourseDTO createCourse(CourseDTO dto) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            Course course = toEntity(dto); // Map DTO to Entity
            em.persist(course);
            em.getTransaction().commit();
            return toDto(course); // Return mapped Entity to DTO
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erreur creation: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public CourseDTO getCourse(String code) {
        EntityManager em = getEntityManager();
        try {
            Course course = em.createQuery("SELECT c FROM Course c WHERE c.code = :code", Course.class)
                    .setParameter("code", code)
                    .getSingleResult();
            
            return toDto(course); // Return mapped Entity to DTO
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public CourseDTO updateCourse(CourseDTO dto) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            // 1. Fetch existing entity
            Course existingCourse = em.find(Course.class, dto.getId());
            if (existingCourse == null) {
                em.getTransaction().rollback();
                return null;
            }
            
            // 2. Update existing entity with DTO data
            existingCourse.setCode(dto.getCode());
            existingCourse.setTitle(dto.getTitle());
            existingCourse.setDescription(dto.getDescription());
            existingCourse.setCredits(dto.getCredits());
            existingCourse.setCapacity(dto.getCapacity());
            existingCourse.setSemester(dto.getSemester());
            
            em.getTransaction().commit();
            
            return toDto(existingCourse); // Return mapped Entity to DTO
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erreur mise à jour: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteCourse(Long courseId) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            Course course = em.find(Course.class, courseId);
            if (course != null) {
                em.remove(course);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback(); 
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erreur suppression: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<CourseDTO> listCourses(String semesterFilter) {
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT c FROM Course c";
            TypedQuery<Course> courseQuery;
            
            if (semesterFilter != null && !semesterFilter.trim().isEmpty()) {
                query += " WHERE c.semester = :semester";
                courseQuery = em.createQuery(query, Course.class)
                        .setParameter("semester", semesterFilter);
            } else {
                courseQuery = em.createQuery(query, Course.class);
            }
            
            // Map the resulting list of Entities to a list of DTOs
            return courseQuery.getResultList().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
                    
        } finally {
            em.close();
        }
    }

    // --- OPÉRATIONS SCHEDULE ---

    @Override
    public Schedule addSchedule(String courseCode, Schedule schedule) throws Exception {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            // NOTE: We still use getCourse(code) which returns DTO, we need the entity here.
            // A better solution would be to create a private getCourseEntity method.
            Course course = em.createQuery("SELECT c FROM Course c WHERE c.code = :code", Course.class)
                    .setParameter("code", courseCode)
                    .getSingleResult();
            
            if (course == null) throw new IllegalArgumentException("Cours non trouvé.");

            if (checkScheduleConflict(em, schedule)) {
                throw new Exception("Conflit d'horaire ou de salle détecté.");
            }

            schedule.setCourse(course);
            em.persist(schedule);
            em.getTransaction().commit();
            return schedule;
        } catch (NoResultException e) {
             throw new IllegalArgumentException("Cours non trouvé.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Schedule> getScheduleByCourse(String courseCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT s FROM Schedule s JOIN s.course c WHERE c.code = :code", Schedule.class)
                    .setParameter("code", courseCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // --- OPÉRATIONS ENROLLMENT ---

    @Override
    public Enrollment enrollStudent(Long studentId, String courseCode) throws Exception {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
             // NOTE: Same issue as above, need the Entity, not the DTO result. 
             Course course = em.createQuery("SELECT c FROM Course c WHERE c.code = :code", Course.class)
                    .setParameter("code", courseCode)
                    .getSingleResult();
            
            if (course == null) throw new IllegalArgumentException("Cours non trouvé.");

            // Vérification si l'étudiant est déjà inscrit
            List<Enrollment> existingEnrollment = em.createQuery(
                            "SELECT e FROM Enrollment e WHERE e.studentId = :studentId AND e.course.code = :courseCode", Enrollment.class)
                    .setParameter("studentId", studentId)
                    .setParameter("courseCode", courseCode)
                    .getResultList();

            if (!existingEnrollment.isEmpty()) {
                throw new Exception("L'étudiant est déjà inscrit à ce cours.");
            }

            // Vérification de la Capacité (Business Logic) - Using efficient method (Fix 2)
            if (!checkCapacity(em, course)) {
                throw new Exception("Le cours a atteint sa capacité maximale.");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudentId(studentId);
            enrollment.setEnrollmentDate(LocalDate.now());

            em.persist(enrollment);
            em.getTransaction().commit();
            return enrollment;
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Cours non trouvé.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<CourseDTO> getStudentCourses(Long studentId) {
        EntityManager em = getEntityManager();
        try {
            // Selects the Course entity, then maps it to DTO (Fix 3)
            List<Course> courses = em.createQuery(
                            "SELECT e.course FROM Enrollment e WHERE e.studentId = :studentId", Course.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
            
            return courses.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
                    
        } finally {
            em.close();
        }
    }
}