package com.universite.courses.dao;

import com.universite.courses.config.DatabaseConfig;
import com.universite.courses.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final Logger logger = LoggerFactory.getLogger(CourseDAO.class);

    public Course create(Course course) throws SQLException {
        String sql = "INSERT INTO courses (code, name, description, credits, semester, capacity, " +
                     "prerequisites, enrollment_start_date, enrollment_end_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?::timestamp, ?::timestamp, ?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCredits());
            stmt.setString(5, course.getSemester());
            stmt.setInt(6, course.getCapacity());
            stmt.setString(7, course.getPrerequisites());
            stmt.setString(8, course.getEnrollmentStartDate());
            stmt.setString(9, course.getEnrollmentEndDate());
            stmt.setBoolean(10, course.getIsActive() != null ? course.getIsActive() : true);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                course.setId(rs.getLong("id"));
            }
            
            logger.info("Created course: {}", course.getCode());
            return course;
        }
    }

    public Course findById(Long id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
            return null;
        }
    }

    public Course findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM courses WHERE code = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
            return null;
        }
    }

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT * FROM courses ORDER BY code";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        
        return courses;
    }

    public List<Course> findBySemester(String semester) throws SQLException {
        String sql = "SELECT * FROM courses WHERE semester = ? ORDER BY code";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, semester);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        
        return courses;
    }

    public Course update(Course course) throws SQLException {
        String sql = "UPDATE courses SET name = ?, description = ?, credits = ?, semester = ?, " +
                     "capacity = ?, prerequisites = ?, enrollment_start_date = ?::timestamp, " +
                     "enrollment_end_date = ?::timestamp, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getSemester());
            stmt.setInt(5, course.getCapacity());
            stmt.setString(6, course.getPrerequisites());
            stmt.setString(7, course.getEnrollmentStartDate());
            stmt.setString(8, course.getEnrollmentEndDate());
            stmt.setBoolean(9, course.getIsActive());
            stmt.setLong(10, course.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Updated course: {}", course.getCode());
                return findById(course.getId());
            }
            return null;
        }
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted course with id: {}", id);
                return true;
            }
            return false;
        }
    }

    public boolean updateEnrolledCount(Long courseId, int change) throws SQLException {
        String sql = "UPDATE courses SET enrolled_count = enrolled_count + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, change);
            stmt.setLong(2, courseId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        return Course.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .credits(rs.getInt("credits"))
                .semester(rs.getString("semester"))
                .capacity(rs.getInt("capacity"))
                .enrolledCount(rs.getInt("enrolled_count"))
                .prerequisites(rs.getString("prerequisites"))
                .enrollmentStartDate(rs.getTimestamp("enrollment_start_date") != null ? 
                        rs.getTimestamp("enrollment_start_date").toString() : null)
                .enrollmentEndDate(rs.getTimestamp("enrollment_end_date") != null ? 
                        rs.getTimestamp("enrollment_end_date").toString() : null)
                .isActive(rs.getBoolean("is_active"))
                .createdAt(rs.getTimestamp("created_at").toString())
                .updatedAt(rs.getTimestamp("updated_at").toString())
                .build();
    }
}
