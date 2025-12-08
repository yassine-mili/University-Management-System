package main.java.com.universite.courses.repository;

import com.universite.courses.entity.Course;
import com.universite.courses.entity.TeacherCourse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class TeacherCourseRepository {
    
    private final EntityManager entityManager;
    
    public TeacherCourseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public TeacherCourse save(TeacherCourse teacherCourse) {
        try {
            entityManager.getTransaction().begin();
            if (teacherCourse.getId() == null) {
                entityManager.persist(teacherCourse);
            } else {
                teacherCourse = entityManager.merge(teacherCourse);
            }
            entityManager.getTransaction().commit();
            log.info("Teacher {} assigned to course {}", 
                teacherCourse.getTeacherId(), teacherCourse.getCourse().getCode());
            return teacherCourse;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error saving teacher course assignment: {}", e.getMessage());
            throw new RuntimeException("Failed to save teacher course assignment", e);
        }
    }
    
    public Optional<TeacherCourse> findById(Long id) {
        try {
            TeacherCourse teacherCourse = entityManager.find(TeacherCourse.class, id);
            return Optional.ofNullable(teacherCourse);
        } catch (Exception e) {
            log.error("Error finding teacher course by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<TeacherCourse> findByTeacher(Long teacherId) {
        try {
            TypedQuery<TeacherCourse> query = entityManager.createQuery(
                "SELECT tc FROM TeacherCourse tc WHERE tc.teacherId = :teacherId AND tc.active = true ORDER BY tc.assignedAt DESC", 
                TeacherCourse.class);
            query.setParameter("teacherId", teacherId);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding courses for teacher {}: {}", teacherId, e.getMessage());
            return List.of();
        }
    }
    
    public List<TeacherCourse> findByCourse(Course course) {
        try {
            TypedQuery<TeacherCourse> query = entityManager.createQuery(
                "SELECT tc FROM TeacherCourse tc WHERE tc.course = :course AND tc.active = true ORDER BY tc.assignedAt", 
                TeacherCourse.class);
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding teachers for course: {}", e.getMessage());
            return List.of();
        }
    }
    
    public void delete(TeacherCourse teacherCourse) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(teacherCourse)) {
                teacherCourse = entityManager.merge(teacherCourse);
            }
            entityManager.remove(teacherCourse);
            entityManager.getTransaction().commit();
            log.info("Teacher course assignment deleted");
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error deleting teacher course assignment: {}", e.getMessage());
            throw new RuntimeException("Failed to delete teacher course assignment", e);
        }
    }
}
