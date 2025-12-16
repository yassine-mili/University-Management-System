package com.universite.courses.dao;

import com.universite.courses.config.DatabaseConfig;
import com.universite.courses.model.StudentCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentDAO.class);

    public StudentCourse enroll(Long studentId, Long courseId) throws SQLException {
        String sql = "INSERT INTO student_courses (student_id, course_id, status) " +
                     "VALUES (?, ?, 'ENROLLED') RETURNING id, enrollment_date, created_at, updated_at";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, studentId);
            stmt.setLong(2, courseId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                StudentCourse enrollment = StudentCourse.builder()
                        .id(rs.getLong("id"))
                        .studentId(studentId)
                        .courseId(courseId)
                        .enrollmentDate(rs.getTimestamp("enrollment_date").toString())
                        .status("ENROLLED")
                        .createdAt(rs.getTimestamp("created_at").toString())
                        .updatedAt(rs.getTimestamp("updated_at").toString())
                        .build();
                
                logger.info("Student {} enrolled in course {}", studentId, courseId);
                return enrollment;
            }
            return null;
        }
    }

    public StudentCourse findById(Long id) throws SQLException {
        String sql = "SELECT * FROM student_courses WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudentCourse(rs);
            }
            return null;
        }
    }

    public StudentCourse findByStudentAndCourse(Long studentId, Long courseId) throws SQLException {
        String sql = "SELECT * FROM student_courses WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, studentId);
            stmt.setLong(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudentCourse(rs);
            }
            return null;
        }
    }

    public List<StudentCourse> findByStudentId(Long studentId) throws SQLException {
        String sql = "SELECT * FROM student_courses WHERE student_id = ? ORDER BY enrollment_date DESC";
        List<StudentCourse> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToStudentCourse(rs));
            }
        }
        
        return enrollments;
    }

    public List<StudentCourse> findByCourseId(Long courseId) throws SQLException {
        String sql = "SELECT * FROM student_courses WHERE course_id = ? ORDER BY enrollment_date";
        List<StudentCourse> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToStudentCourse(rs));
            }
        }
        
        return enrollments;
    }

    public int countEnrollmentsByCourse(Long courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM student_courses WHERE course_id = ? AND status = 'ENROLLED'";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public boolean updateStatus(Long id, String status) throws SQLException {
        String sql = "UPDATE student_courses SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateGrade(Long id, String grade) throws SQLException {
        String sql = "UPDATE student_courses SET grade = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, grade);
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM student_courses WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted enrollment with id: {}", id);
                return true;
            }
            return false;
        }
    }

    public boolean unenroll(Long studentId, Long courseId) throws SQLException {
        String sql = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, studentId);
            stmt.setLong(2, courseId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Student {} unenrolled from course {}", studentId, courseId);
                return true;
            }
            return false;
        }
    }

    private StudentCourse mapResultSetToStudentCourse(ResultSet rs) throws SQLException {
        return StudentCourse.builder()
                .id(rs.getLong("id"))
                .studentId(rs.getLong("student_id"))
                .courseId(rs.getLong("course_id"))
                .enrollmentDate(rs.getTimestamp("enrollment_date").toString())
                .status(rs.getString("status"))
                .grade(rs.getString("grade"))
                .createdAt(rs.getTimestamp("created_at").toString())
                .updatedAt(rs.getTimestamp("updated_at").toString())
                .build();
    }
}
