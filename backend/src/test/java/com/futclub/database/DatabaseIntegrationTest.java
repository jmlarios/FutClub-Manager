package com.futclub.database;

import org.junit.jupiter.api.Test;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database operations.
 * Tests actual CRUD operations on the database.
 */
class DatabaseIntegrationTest extends BaseDAOTest {
    
    @Test
    void testInsertPlayer() throws Exception {
        String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Test");
            pstmt.setString(2, "Player");
            pstmt.setDate(3, java.sql.Date.valueOf("1995-01-01"));
            pstmt.setString(4, "ST");
            pstmt.setInt(5, 99);
            pstmt.setString(6, "AVAILABLE");
            
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected, "Should insert 1 row");
        }
        
        // Verify insertion
        String selectSql = "SELECT * FROM players WHERE shirt_number = 99";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSql);
             ResultSet rs = pstmt.executeQuery()) {
            assertTrue(rs.next(), "Should find inserted player");
            assertEquals("Test", rs.getString("first_name"));
            assertEquals("Player", rs.getString("last_name"));
        }
    }
    
    @Test
    void testUpdatePlayer() throws Exception {
        // First insert a player
        String insertSql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
        int playerId;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Update");
            pstmt.setString(2, "Test");
            pstmt.setDate(3, java.sql.Date.valueOf("1994-05-10"));
            pstmt.setString(4, "GK");
            pstmt.setInt(5, 88);
            pstmt.setString(6, "AVAILABLE");
            pstmt.executeUpdate();
            
            ResultSet keys = pstmt.getGeneratedKeys();
            assertTrue(keys.next());
            playerId = keys.getInt(1);
        }
        
        // Update the player
        String updateSql = "UPDATE players SET status = ? WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
            pstmt.setString(1, "INJURED");
            pstmt.setInt(2, playerId);
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected, "Should update 1 row");
        }
        
        // Verify update
        String selectSql = "SELECT status FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("INJURED", rs.getString("status"));
        }
    }
    
    @Test
    void testDeletePlayer() throws Exception {
        // First insert a player
        String insertSql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
        int playerId;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Delete");
            pstmt.setString(2, "Test");
            pstmt.setDate(3, java.sql.Date.valueOf("1993-03-15"));
            pstmt.setString(4, "CB");
            pstmt.setInt(5, 77);
            pstmt.setString(6, "AVAILABLE");
            pstmt.executeUpdate();
            
            ResultSet keys = pstmt.getGeneratedKeys();
            assertTrue(keys.next());
            playerId = keys.getInt(1);
        }
        
        // Delete the player
        String deleteSql = "DELETE FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setInt(1, playerId);
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected, "Should delete 1 row");
        }
        
        // Verify deletion
        String selectSql = "SELECT * FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next(), "Player should be deleted");
        }
    }
    
    @Test
    void testForeignKeyConstraint() throws Exception {
        // Try to insert attendance record with non-existent player
        String sql = "INSERT INTO attendance (player_id, session_id, status) VALUES (?, ?, ?)";
        
        assertThrows(Exception.class, () -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, 99999); // Non-existent player
                pstmt.setInt(2, 1);
                pstmt.setString(3, "PRESENT");
                pstmt.executeUpdate();
            }
        }, "Should throw exception for foreign key violation");
    }
    
    @Test
    void testUniqueConstraint() throws Exception {
        // Try to insert two players with same shirt number
        String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        // First insert
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Player");
            pstmt.setString(2, "One");
            pstmt.setDate(3, java.sql.Date.valueOf("1992-02-02"));
            pstmt.setString(4, "ST");
            pstmt.setInt(5, 66);
            pstmt.setString(6, "AVAILABLE");
            pstmt.executeUpdate();
        }
        
        // Second insert with same shirt number should fail
        assertThrows(Exception.class, () -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "Player");
                pstmt.setString(2, "Two");
                pstmt.setDate(3, java.sql.Date.valueOf("1996-06-06"));
                pstmt.setString(4, "GK");
                pstmt.setInt(5, 66); // Same shirt number
                pstmt.setString(6, "AVAILABLE");
                pstmt.executeUpdate();
            }
        }, "Should throw exception for unique constraint violation");
    }
    
    @Test
    void testTransactionRollback() throws Exception {
        connection.setAutoCommit(false);
        
        try {
            // Insert a player
            String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "Rollback");
                pstmt.setString(2, "Test");
                pstmt.setDate(3, java.sql.Date.valueOf("1991-04-04"));
                pstmt.setString(4, "CM");
                pstmt.setInt(5, 55);
                pstmt.setString(6, "AVAILABLE");
                pstmt.executeUpdate();
            }
            
            // Rollback the transaction
            connection.rollback();
            
            // Verify player was not inserted
            String selectSql = "SELECT * FROM players WHERE shirt_number = 55";
            try (PreparedStatement pstmt = connection.prepareStatement(selectSql);
                 ResultSet rs = pstmt.executeQuery()) {
                assertFalse(rs.next(), "Player should not exist after rollback");
            }
            
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    @Test
    void testTransactionCommit() throws Exception {
        connection.setAutoCommit(false);
        
        try {
            // Insert a player
            String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "Commit");
                pstmt.setString(2, "Test");
                pstmt.setDate(3, java.sql.Date.valueOf("1990-08-08"));
                pstmt.setString(4, "LW");
                pstmt.setInt(5, 44);
                pstmt.setString(6, "AVAILABLE");
                pstmt.executeUpdate();
            }
            
            // Commit the transaction
            connection.commit();
            
            // Verify player was inserted
            String selectSql = "SELECT * FROM players WHERE shirt_number = 44";
            try (PreparedStatement pstmt = connection.prepareStatement(selectSql);
                 ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Player should exist after commit");
                assertEquals("Commit", rs.getString("first_name"));
            }
            
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
