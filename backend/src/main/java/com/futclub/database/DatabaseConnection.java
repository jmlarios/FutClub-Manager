package com.futclub.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections for the FutClub Manager application.
 * Implements singleton pattern to ensure only one connection exists.
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:data/futclub.db";
    private static Connection connection = null;
    private DatabaseConnection() {}

    /**
     * Gets the database connection. Creates a new connection if one doesn't exist.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");

                // Create connection
                connection = DriverManager.getConnection(DB_URL);

                // Enable foreign keys
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");

                // Log resolved DB path to help diagnosing multiple DB files issue
                try {
                    File dbFile = new File("data/futclub.db");
                    System.out.println("Using database file: " + dbFile.getAbsolutePath());
                    System.out.println("JDBC URL: " + DB_URL);
                } catch (Exception ex) {
                    System.out.println("Could not resolve DB file path: " + ex.getMessage());
                }

                System.out.println("Database connection established successfully.");

            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Tests if the database connection is valid.
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}