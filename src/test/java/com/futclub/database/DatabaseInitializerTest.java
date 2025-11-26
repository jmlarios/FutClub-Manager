package com.futclub.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseInitializer class.
 */
class DatabaseInitializerTest {
    
    @Test
    void testInitializeDatabase() throws Exception {
        // Initialize database
        DatabaseInitializer.initializeDatabase();
        
        // Verify tables were created
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        
        // Check that users table exists
        ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
        assertTrue(rs.next(), "Users table should exist");
        
        // Check that players table exists
        rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='players'");
        assertTrue(rs.next(), "Players table should exist");
        
        stmt.close();
    }
    
    @Test
    void testLoadSeedData() throws Exception {
        // Initialize and load seed data
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.loadSeedData();
        
        // Verify data was loaded
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        
        // Check that users exist
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
        assertTrue(rs.next());
        int userCount = rs.getInt("count");
        assertTrue(userCount > 0, "Should have users after loading seed data");
        
        // Check that players exist
        rs = stmt.executeQuery("SELECT COUNT(*) as count FROM players");
        assertTrue(rs.next());
        int playerCount = rs.getInt("count");
        assertTrue(playerCount > 0, "Should have players after loading seed data");
        
        stmt.close();
    }
    
    @Test
    void testIsDatabaseInitialized() throws Exception {
        DatabaseInitializer.initializeDatabase();
        assertTrue(DatabaseInitializer.isDatabaseInitialized(), 
                  "Database should be initialized after calling initializeDatabase()");
    }
}
