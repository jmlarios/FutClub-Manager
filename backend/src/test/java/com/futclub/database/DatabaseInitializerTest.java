package com.futclub.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseInitializer class.
 */
class DatabaseInitializerTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.loadSeedData();
        connection = DatabaseConnection.getConnection();
    }

    @Test
    void testInitializeDatabase() throws Exception {
        // Verify tables were created
        try (Statement stmt = connection.createStatement()) {
            // Check that users table exists
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
            assertTrue(rs.next(), "Users table should exist");

            // Check that players table exists
            rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='players'");
            assertTrue(rs.next(), "Players table should exist");
        }
    }

    @Test
    void testLoadSeedData() throws Exception {
        // Verify data was loaded
        try (Statement stmt = connection.createStatement()) {
            // Check that users exist
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            assertTrue(rs.next());
            int userCount = rs.getInt("count");
            assertTrue(userCount >= 3, "Should have at least 3 users after loading seed data");

            // Check that players exist
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM players");
            assertTrue(rs.next());
            int playerCount = rs.getInt("count");
            assertTrue(playerCount >= 10, "Should have at least 10 players after loading seed data");
        }
    }

    @Test
    void testIsDatabaseInitialized() throws Exception {
        assertTrue(DatabaseInitializer.isDatabaseInitialized(),
                  "Database should be initialized after calling initializeDatabase()");
    }
}
