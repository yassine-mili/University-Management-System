package com.universite.courses.dao;

import com.universite.courses.config.DatabaseConfig;
import com.universite.courses.model.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleDAO.class);

    public Schedule create(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (course_id, day_of_week, start_time, end_time, room, building) " +
                     "VALUES (?, ?, ?::time, ?::time, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, schedule.getCourseId());
            stmt.setString(2, schedule.getDayOfWeek());
            stmt.setString(3, schedule.getStartTime());
            stmt.setString(4, schedule.getEndTime());
            stmt.setString(5, schedule.getRoom());
            stmt.setString(6, schedule.getBuilding());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                schedule.setId(rs.getLong("id"));
            }
            
            logger.info("Created schedule for course ID: {}", schedule.getCourseId());
            return schedule;
        }
    }

    public Schedule findById(Long id) throws SQLException {
        String sql = "SELECT * FROM schedules WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSchedule(rs);
            }
            return null;
        }
    }

    public List<Schedule> findByCourseId(Long courseId) throws SQLException {
        String sql = "SELECT * FROM schedules WHERE course_id = ? ORDER BY day_of_week, start_time";
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        }
        
        return schedules;
    }

    public List<Schedule> findAll() throws SQLException {
        String sql = "SELECT * FROM schedules ORDER BY course_id, day_of_week, start_time";
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        }
        
        return schedules;
    }

    public boolean hasScheduleConflict(String dayOfWeek, String startTime, String endTime, String room, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM schedules WHERE day_of_week = ? AND room = ? " +
                     "AND ((start_time < ?::time AND end_time > ?::time) " +
                     "OR (start_time < ?::time AND end_time > ?::time) " +
                     "OR (start_time >= ?::time AND end_time <= ?::time))";
        
        if (excludeId != null) {
            sql += " AND id != ?";
        }
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dayOfWeek);
            stmt.setString(2, room);
            stmt.setString(3, endTime);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            stmt.setString(6, endTime);
            stmt.setString(7, startTime);
            stmt.setString(8, endTime);
            
            if (excludeId != null) {
                stmt.setLong(9, excludeId);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted schedule with id: {}", id);
                return true;
            }
            return false;
        }
    }

    public boolean deleteByCourseId(Long courseId) throws SQLException {
        String sql = "DELETE FROM schedules WHERE course_id = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, courseId);
            int rowsAffected = stmt.executeUpdate();
            logger.info("Deleted {} schedules for course ID: {}", rowsAffected, courseId);
            return rowsAffected > 0;
        }
    }

    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        return Schedule.builder()
                .id(rs.getLong("id"))
                .courseId(rs.getLong("course_id"))
                .dayOfWeek(rs.getString("day_of_week"))
                .startTime(rs.getTime("start_time").toString())
                .endTime(rs.getTime("end_time").toString())
                .room(rs.getString("room"))
                .building(rs.getString("building"))
                .createdAt(rs.getTimestamp("created_at").toString())
                .build();
    }
}
