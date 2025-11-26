package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.TrainingSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingSessionDAOImpl implements TrainingSessionDAO {
    
    @Override
    public TrainingSession getById(int sessionId) {
        String sql = "SELECT * FROM training_sessions WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTrainingSessionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting training session by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<TrainingSession> getAll() {
        List<TrainingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM training_sessions ORDER BY session_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sessions.add(extractTrainingSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all training sessions: " + e.getMessage());
        }
        return sessions;
    }
    
    @Override
    public void insert(TrainingSession session) {
        String sql = "INSERT INTO training_sessions (session_date, focus, location, duration_minutes, intensity, coach_id, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, session.getSessionDate());
            pstmt.setString(2, session.getFocus());
            pstmt.setString(3, session.getLocation());
            pstmt.setInt(4, session.getDurationMinutes());
            pstmt.setString(5, session.getIntensity());
            pstmt.setInt(6, session.getCoachId());
            pstmt.setString(7, session.getNotes());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                session.setSessionId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting training session: " + e.getMessage());
        }
    }
    
    @Override
    public void update(TrainingSession session) {
        String sql = "UPDATE training_sessions SET session_date = ?, focus = ?, location = ?, " +
                    "duration_minutes = ?, intensity = ?, coach_id = ?, notes = ? WHERE session_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, session.getSessionDate());
            pstmt.setString(2, session.getFocus());
            pstmt.setString(3, session.getLocation());
            pstmt.setInt(4, session.getDurationMinutes());
            pstmt.setString(5, session.getIntensity());
            pstmt.setInt(6, session.getCoachId());
            pstmt.setString(7, session.getNotes());
            pstmt.setInt(8, session.getSessionId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating training session: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int sessionId) {
        String sql = "DELETE FROM training_sessions WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting training session: " + e.getMessage());
        }
    }
    
    @Override
    public List<TrainingSession> getByCoachId(int coachId) {
        List<TrainingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM training_sessions WHERE coach_id = ? ORDER BY session_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coachId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(extractTrainingSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting training sessions by coach: " + e.getMessage());
        }
        return sessions;
    }
    
    @Override
    public List<TrainingSession> getSessionsInDateRange(Timestamp startDate, Timestamp endDate) {
        List<TrainingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM training_sessions WHERE session_date BETWEEN ? AND ? ORDER BY session_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(extractTrainingSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting training sessions in date range: " + e.getMessage());
        }
        return sessions;
    }
    
    @Override
    public List<TrainingSession> getRecentSessions(int limit) {
        List<TrainingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM training_sessions ORDER BY session_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(extractTrainingSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent training sessions: " + e.getMessage());
        }
        return sessions;
    }
    
    private TrainingSession extractTrainingSessionFromResultSet(ResultSet rs) throws SQLException {
        TrainingSession session = new TrainingSession();
        session.setSessionId(rs.getInt("session_id"));
        session.setSessionDate(rs.getTimestamp("session_date"));
        session.setFocus(rs.getString("focus"));
        session.setLocation(rs.getString("location"));
        session.setDurationMinutes(rs.getInt("duration_minutes"));
        session.setIntensity(rs.getString("intensity"));
        session.setCoachId(rs.getInt("coach_id"));
        session.setNotes(rs.getString("notes"));
        return session;
    }
}
