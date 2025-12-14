package main.java.com.universite.courses.repository;

import com.universite.courses.entity.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class CourseRepository {
    
    private final EntityManager entityManager;
    
    public CourseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Course save(Course course) {
        try {
            entityManager.getTransaction().begin();
            if (course.getId() == null) {
                entityManager.persist(course);
            } else {
                course = entityManager.merge(course);
            }
            entityManager.getTransaction().commit();
            log.info("Course saved: {}", course.getCode());
            return course;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error saving course: {}", e.getMessage());
            throw new RuntimeException("Failed to save course", e);
        }
    }
    
    public Optional<Course> findById(Long id) {
        try {
            Course course = entityManager.find(Course.class, id);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            log.error("Error finding course by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<Course> findByCode(String code) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.code = :code", Course.class);
            query.setParameter("code", code);
            List<Course> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding course by code {}: {}", code, e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Course> findAll() {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c ORDER BY c.code", Course.class);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding all courses: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve courses", e);
        }
    }
    
    public List<Course> findBySemester(String semester) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.semester = :semester ORDER BY c.code", Course.class);
            query.setParameter("semester", semester);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding courses by semester {}: {}", semester, e.getMessage());
            return List.of();
        }
    }
    
    public List<Course> findByDepartment(String department) {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.department = :department ORDER BY c.code", Course.class);
            query.setParameter("department", department);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding courses by department {}: {}", department, e.getMessage());
            return List.of();
        }
    }
    
    public List<Course> findAvailableCourses() {
        try {
            TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.active = true AND c.enrolled < c.capacity ORDER BY c.code", Course.class);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding available courses: {}", e.getMessage());
            return List.of();
        }
    }
    
    public void delete(Course course) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(course)) {
                course = entityManager.merge(course);
            }
            entityManager.remove(course);
            entityManager.getTransaction().commit();
            log.info("Course deleted: {}", course.getCode());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error deleting course: {}", e.getMessage());
            throw new RuntimeException("Failed to delete course", e);
        }
    }
    
    public boolean existsByCode(String code) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Course c WHERE c.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            log.error("Error checking if course exists: {}", e.getMessage());
            return false;
        }
    }
}
