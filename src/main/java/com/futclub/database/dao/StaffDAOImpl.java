package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAOImpl implements StaffDAO {
    
    @Override
    public Staff getById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Staff> getAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                staffList.add(extractStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all staff: " + e.getMessage());
        }
        return staffList;
    }
    
    @Override
    public void insert(Staff staff) {
        String sql = "INSERT INTO staff (full_name, user_id, email, phone, hire_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, staff.getFullName());
            pstmt.setInt(2, staff.getUserId());
            pstmt.setString(3, staff.getEmail());
            pstmt.setString(4, staff.getPhone());
            pstmt.setDate(5, staff.getHireDate());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                staff.setStaffId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting staff: " + e.getMessage());
        }
    }
    
    @Override
    public void update(Staff staff) {
        String sql = "UPDATE staff SET full_name = ?, user_id = ?, email = ?, phone = ?, hire_date = ? WHERE staff_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staff.getFullName());
            pstmt.setInt(2, staff.getUserId());
            pstmt.setString(3, staff.getEmail());
            pstmt.setString(4, staff.getPhone());
            pstmt.setDate(5, staff.getHireDate());
            pstmt.setInt(6, staff.getStaffId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
        }
    }
    
    @Override
    public Staff getByUserId(int userId) {
        String sql = "SELECT * FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by user ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Staff getByEmail(String email) {
        String sql = "SELECT * FROM staff WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by email: " + e.getMessage());
        }
        return null;
    }
    
    private Staff extractStaffFromResultSet(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setFullName(rs.getString("full_name"));
        staff.setUserId(rs.getInt("user_id"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        staff.setHireDate(rs.getDate("hire_date"));
        return staff;
    }
}
