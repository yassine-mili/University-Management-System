package main.java.com.universite.courses.repository;

import com.universite.courses.entity.Course;
import com.universite.courses.entity.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ScheduleRepository {
    
    private final EntityManager entityManager;
    
    public ScheduleRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Schedule save(Schedule schedule) {
        try {
            entityManager.getTransaction().begin();
            if (schedule.getId() == null) {
                entityManager.persist(schedule);
            } else {
                schedule = entityManager.merge(schedule);
            }
            entityManager.getTransaction().commit();
            log.info("Schedule saved for course: {}", schedule.getCourse().getCode());
            return schedule;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error saving schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to save schedule", e);
        }
    }
    
    public Optional<Schedule> findById(Long id) {
        try {
            Schedule schedule = entityManager.find(Schedule.class, id);
            return Optional.ofNullable(schedule);
        } catch (Exception e) {
            log.error("Error finding schedule by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Schedule> findByCourse(Course course) {
        try {
            TypedQuery<Schedule> query = entityManager.createQuery(
                "SELECT s FROM Schedule s WHERE s.course = :course ORDER BY s.dayOfWeek, s.startTime", 
                Schedule.class);
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding schedules for course: {}", e.getMessage());
            return List.of();
        }
    }
    
    public List<Schedule> findByCourseId(Long courseId) {
        try {
            TypedQuery<Schedule> query = entityManager.createQuery(
                "SELECT s FROM Schedule s WHERE s.course.id = :courseId ORDER BY s.dayOfWeek, s.startTime", 
                Schedule.class);
            query.setParameter("courseId", courseId);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding schedules for course ID {}: {}", courseId, e.getMessage());
            return List.of();
        }
    }
    
    public List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek) {
        try {
            TypedQuery<Schedule> query = entityManager.createQuery(
                "SELECT s FROM Schedule s WHERE s.dayOfWeek = :dayOfWeek ORDER BY s.startTime", 
                Schedule.class);
            query.setParameter("dayOfWeek", dayOfWeek);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding schedules for day {}: {}", dayOfWeek, e.getMessage());
            return List.of();
        }
    }
    
    public List<Schedule> findByRoom(String room) {
        try {
            TypedQuery<Schedule> query = entityManager.createQuery(
                "SELECT s FROM Schedule s WHERE s.room = :room ORDER BY s.dayOfWeek, s.startTime", 
                Schedule.class);
            query.setParameter("room", room);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error finding schedules for room {}: {}", room, e.getMessage());
            return List.of();
        }
    }
    
    public void delete(Schedule schedule) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(schedule)) {
                schedule = entityManager.merge(schedule);
            }
            entityManager.remove(schedule);
            entityManager.getTransaction().commit();
            log.info("Schedule deleted for course: {}", schedule.getCourse().getCode());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            log.error("Error deleting schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to delete schedule", e);
        }
    }
}
