// Fichier : course-service/src/main/java/com/univ/course/CourseServiceImpl.java

package com.univ.course;

import jakarta.jws.WebService;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebService(endpointInterface = "com.univ.course.CourseService")
public class CourseServiceImpl implements CourseService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("CourseUnit");

    private EntityManager getEntityManager() { return emf.createEntityManager(); }

    // --- LOGIQUE MÉTIER ---

    private boolean checkScheduleConflict(EntityManager em, Schedule newSchedule) {
        // Logique de détection de conflit: vérifie si une autre entrée a lieu
        // le même jour, dans la même salle, avec un chevauchement temporel.

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

    private boolean checkCapacity(Course course) {
        // La méthode getEnrollments() renvoie une collection de relations d'inscription
        // Nous nous assurons de charger cette collection avant de la compter.
        if (course.getEnrollments() == null) {
            return true; // Pas d'inscrits, capacité non atteinte
        }
        return course.getEnrollments().size() < course.getCapacity();
    }

    // --- OPÉRATIONS CRUD COURSE ---

    @Override
    public Course createCourse(Course course) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(course);
            em.getTransaction().commit();
            return course;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // Gérer les violations d'unicité (code) ici si nécessaire
            System.err.println("Erreur creation: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Course getCourse(String code) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Course c WHERE c.code = :code", Course.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Course updateCourse(Course updatedCourse) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            // S'assurer que l'entité existe avant de la mettre à jour
            Course existingCourse = em.find(Course.class, updatedCourse.getId());
            if (existingCourse == null) {
                em.getTransaction().rollback();
                return null;
            }
            Course course = em.merge(updatedCourse);
            em.getTransaction().commit();
            return course;
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
            em.getTransaction().rollback(); // Si non trouvé, on annule
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
    public List<Course> listCourses(String semesterFilter) {
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT c FROM Course c";
            if (semesterFilter != null && !semesterFilter.trim().isEmpty()) {
                query += " WHERE c.semester = :semester";
                return em.createQuery(query, Course.class)
                        .setParameter("semester", semesterFilter)
                        .getResultList();
            }
            return em.createQuery(query, Course.class).getResultList();
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
            Course course = getCourse(courseCode);
            if (course == null) throw new IllegalArgumentException("Cours non trouvé.");

            if (checkScheduleConflict(em, schedule)) {
                throw new Exception("Conflit d'horaire ou de salle détecté.");
            }

            schedule.setCourse(course);
            em.persist(schedule);
            em.getTransaction().commit();
            return schedule;
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
            Course course = getCourse(courseCode);
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

            // Vérification de la Capacité (Business Logic)
            if (!checkCapacity(course)) {
                throw new Exception("Le cours a atteint sa capacité maximale.");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudentId(studentId);
            enrollment.setEnrollmentDate(LocalDate.now());

            em.persist(enrollment);
            em.getTransaction().commit();
            return enrollment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Course> getStudentCourses(Long studentId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e.course FROM Enrollment e WHERE e.studentId = :studentId", Course.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}