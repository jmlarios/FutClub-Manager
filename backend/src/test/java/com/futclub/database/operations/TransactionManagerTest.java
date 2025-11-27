package com.futclub.database.operations;

import com.futclub.database.BaseDAOTest;
import com.futclub.database.TransactionManager;
import com.futclub.database.exception.DatabaseException;
import org.junit.jupiter.api.Test;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TransactionManager.
 */
class TransactionManagerTest extends BaseDAOTest {
    
    @Test
    void testSuccessfulTransaction() {
        // Execute a transaction that should succeed
        assertDoesNotThrow(() -> {
            TransactionManager.executeTransaction(conn -> {
                String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "Transaction");
                    pstmt.setString(2, "Success");
                    pstmt.setDate(3, java.sql.Date.valueOf("1992-07-15"));
                    pstmt.setString(4, "RW");
                    pstmt.setInt(5, 33);
                    pstmt.setString(6, "AVAILABLE");
                    pstmt.executeUpdate();
                }
            });
        });
        
        // Verify the insert was committed
        assertDoesNotThrow(() -> {
            String sql = "SELECT * FROM players WHERE shirt_number = 33";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Transaction should have been committed");
                assertEquals("Transaction", rs.getString("first_name"));
            }
        });
    }
    
    @Test
    void testFailedTransactionRollback() {
        // Execute a transaction that should fail and rollback
        assertThrows(DatabaseException.class, () -> {
            TransactionManager.executeTransaction(conn -> {
                // First operation succeeds
                String sql1 = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setString(1, "Rollback");
                    pstmt.setString(2, "Test");
                    pstmt.setDate(3, java.sql.Date.valueOf("1991-05-05"));
                    pstmt.setString(4, "LB");
                    pstmt.setInt(5, 65);
                    pstmt.setString(6, "AVAILABLE");
                    pstmt.executeUpdate();
                }
                
                // Second operation fails (duplicate shirt number from seed data)
                String sql2 = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                    pstmt.setString(1, "Will");
                    pstmt.setString(2, "Fail");
                    pstmt.setDate(3, java.sql.Date.valueOf("1990-10-10"));
                    pstmt.setString(4, "ST");
                    pstmt.setInt(5, 1); // Duplicate shirt number
                    pstmt.setString(6, "AVAILABLE");
                    pstmt.executeUpdate();
                }
            });
        });
        
        // Verify the first insert was rolled back
        assertDoesNotThrow(() -> {
            String sql = "SELECT * FROM players WHERE shirt_number = 65";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                assertFalse(rs.next(), "Transaction should have been rolled back");
            }
        });
    }
    
    @Test
    void testMultipleOperationsInTransaction() {
        assertDoesNotThrow(() -> {
            TransactionManager.executeTransaction(conn -> {
                // Insert multiple players in one transaction
                String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status) VALUES (?, ?, ?, ?, ?, ?)";
                
                for (int i = 50; i < 53; i++) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, "Multi");
                        pstmt.setString(2, "Player" + i);
                        pstmt.setDate(3, java.sql.Date.valueOf("1993-12-12"));
                        pstmt.setString(4, "CM");
                        pstmt.setInt(5, i);
                        pstmt.setString(6, "AVAILABLE");
                        pstmt.executeUpdate();
                    }
                }
            });
        });
        
        // Verify all players were inserted
        assertDoesNotThrow(() -> {
            String sql = "SELECT COUNT(*) as count FROM players WHERE shirt_number BETWEEN 50 AND 52";
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(3, rs.getInt("count"), "All 3 players should be inserted");
            }
        });
    }
}
