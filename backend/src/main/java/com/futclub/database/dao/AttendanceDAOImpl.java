package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.AttendanceRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {
    
    @Override
    public AttendanceRecord getById(int attendanceId) {
        String sql = "SELECT * FROM attendance WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<AttendanceRecord> getAll() {
        List<AttendanceRecord> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY recorded_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all attendance records: " + e.getMessage());
        }
        return attendanceList;
    }
    
    @Override
    public void insert(AttendanceRecord attendance) {
        String sql = "INSERT INTO attendance (player_id, session_id, status, notes) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attendance.getPlayerId());
            pstmt.setInt(2, attendance.getSessionId());
            pstmt.setString(3, attendance.getStatus());
            pstmt.setString(4, attendance.getNotes());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                attendance.setAttendanceId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting attendance: " + e.getMessage());
        }
    }
    
    @Override
    public void update(AttendanceRecord attendance) {
        String sql = "UPDATE attendance SET player_id = ?, session_id = ?, status = ?, notes = ? WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendance.getPlayerId());
            pstmt.setInt(2, attendance.getSessionId());
            pstmt.setString(3, attendance.getStatus());
            pstmt.setString(4, attendance.getNotes());
            pstmt.setInt(5, attendance.getAttendanceId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating attendance: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting attendance: " + e.getMessage());
        }
    }
    
    @Override
    public List<AttendanceRecord> getBySessionId(int sessionId) {
        List<AttendanceRecord> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE session_id = ? ORDER BY player_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance by session: " + e.getMessage());
        }
        return attendanceList;
    }
    
    @Override
    public List<AttendanceRecord> getByPlayerId(int playerId) {
        List<AttendanceRecord> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE player_id = ? ORDER BY recorded_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance by player: " + e.getMessage());
        }
        return attendanceList;
    }
    
    @Override
    public AttendanceRecord getByPlayerAndSession(int playerId, int sessionId) {
        String sql = "SELECT * FROM attendance WHERE player_id = ? AND session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance by player and session: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public int getAttendanceCountByPlayer(int playerId, String status) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE player_id = ? AND status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance count: " + e.getMessage());
        }
        return 0;
    }
    
    private AttendanceRecord extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        AttendanceRecord attendance = new AttendanceRecord();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setPlayerId(rs.getInt("player_id"));
        attendance.setSessionId(rs.getInt("session_id"));
        attendance.setStatus(rs.getString("status"));
        attendance.setNotes(rs.getString("notes"));
        attendance.setRecordedAt(rs.getTimestamp("recorded_at"));
        return attendance;
    }
}
