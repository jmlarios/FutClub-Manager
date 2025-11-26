package com.futclub.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseConnection class.
 */
class DatabaseConnectionTest {
    
    @Test
    void testGetConnection() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");
    }
    
    @Test
    void testConnectionIsSingleton() throws Exception {
        Connection conn1 = DatabaseConnection.getConnection();
        Connection conn2 = DatabaseConnection.getConnection();
        assertSame(conn1, conn2, "Should return same connection instance");
    }
    
    @Test
    void testTestConnection() {
        assertTrue(DatabaseConnection.testConnection(), "Test connection should succeed");
    }
    
    @Test
    void testCloseConnection() throws Exception {
        DatabaseConnection.getConnection(); // Ensure connection exists
        DatabaseConnection.closeConnection();
        // Connection should be closed now
        // Getting a new connection should work
        Connection newConn = DatabaseConnection.getConnection();
        assertNotNull(newConn, "Should be able to get new connection after close");
    }
}
