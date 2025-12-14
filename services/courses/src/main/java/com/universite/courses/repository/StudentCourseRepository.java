package main.java.com.universite.courses.repository;

import com.universite.courses.entity.Course;
import com.universite.courses.entity.StudentCourse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class StudentCourseRepository {
    
    private final EntityManager entityManager;
    
    public StudentCourseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public StudentCourse save(StudentCourse studentCourse) {
        try {
            entityManager.getTransaction().begin();
            if (studentCourse.getId() == null) {
                entityManager.persist(studentCourse);
            } else {
                studentCourse = entityManager.merge(studentCourse);
            }
            entityManager.getTransaction().commit();
            log.info("Student {} enrolled in course {}", 
                studentCourse.getStudentId(), studentCourse.getCourse().getCode());
            return studentCourse;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error saving student course enrollment: {}", e.getMessage());
            throw new RuntimeException("Failed to save student course enrollment", e);
        }
    }
    
    public Optional<StudentCourse> findById(Long id) {
        try {
            StudentCourse studentCourse = entityManager.find(StudentCourse.class, id);
            return Optional.ofNullable(studentCourse);
        } catch (Exception e) {
            log.error("Error finding student course by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<StudentCourse> findByStudentAndCourse(Long studentId, Long courseId) {
        try {
            TypedQuery<StudentCourse> query = entityManager.createQuery(
                "SELECT sc FROM StudentCourse sc WHERE sc.studentId = :studentId AND sc.course.id = :courseId", 
                StudentCourse.class);
            query.setParameter("studentId", studentId);
            query.setParameter("courseId", courseId);
            List<StudentCourse> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding student course enrollment: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<StudentCourse> findByStudent(Long studentId) {
        try {
            TypedQuery<StudentCourse> query = entityManager.createQuery(
                "SELECT sc FROM StudentCourse sc WHERE sc.studentId = :studentId ORDER BY sc.enrolledAt DESC", 
                StudentCourse.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding courses for student {}: {}", studentId, e.getMessage());
            return List.of();
        }
    }
    
    public List<StudentCourse> findByCourse(Course course) {
        try {
            TypedQuery<StudentCourse> query = entityManager.createQuery(
                "SELECT sc FROM StudentCourse sc WHERE sc.course = :course ORDER BY sc.enrolledAt", 
                StudentCourse.class);
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding students for course: {}", e.getMessage());
            return List.of();
        }
    }
    
    public List<StudentCourse> findActiveByStudent(Long studentId) {
        try {
            TypedQuery<StudentCourse> query = entityManager.createQuery(
                "SELECT sc FROM StudentCourse sc WHERE sc.studentId = :studentId AND sc.enrollmentStatus = 'ENROLLED' ORDER BY sc.enrolledAt DESC", 
                StudentCourse.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding active courses for student {}: {}", studentId, e.getMessage());
            return List.of();
        }
    }
    
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(sc) FROM StudentCourse sc WHERE sc.studentId = :studentId AND sc.course.id = :courseId AND sc.enrollmentStatus = 'ENROLLED'", 
                Long.class);
            query.setParameter("studentId", studentId);
            query.setParameter("courseId", courseId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            log.error("Error checking student enrollment: {}", e.getMessage());
            return false;
        }
    }
    
    public void delete(StudentCourse studentCourse) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(studentCourse)) {
                studentCourse = entityManager.merge(studentCourse);
            }
            entityManager.remove(studentCourse);
            entityManager.getTransaction().commit();
            log.info("Student course enrollment deleted");
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error deleting student course enrollment: {}", e.getMessage());
            throw new RuntimeException("Failed to delete student course enrollment", e);
        }
    }
}
