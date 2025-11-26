package com.futclub.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for all database tests.
 * Provides common setup and teardown for test database.
 */
public abstract class BaseDAOTest {

    protected Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize test database
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.loadSeedData();
        connection = DatabaseConnection.getConnection();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up after each test
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Helper method to verify connection is active.
     */
    protected boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}