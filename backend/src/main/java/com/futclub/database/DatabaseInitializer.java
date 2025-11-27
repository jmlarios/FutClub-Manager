package com.futclub.database;

import com.futclub.security.PasswordHasher;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Initializes the database by creating tables.
 */
public class DatabaseInitializer {

    private static final String SCHEMA_FILE = "/database/schema.sql";
    private static final String SEED_DATA_FILE = "/database/seed_data.sql";
    private static final Map<String, String> DEFAULT_SEED_PASSWORDS = Map.of(
        "coach.smith", "password123",
        "analyst.jones", "password123",
        "admin.wilson", "password123"
    );

    /**
     * Initializes the database by creating all tables.
     * Only creates tables if they don't already exist.
     */
    public static void initializeDatabase() throws SQLException {
        // Ensure data directory exists
        createDataDirectory();

        // Get connection
        Connection conn = DatabaseConnection.getConnection();

        // Execute schema SQL
        System.out.println("Initializing database schema...");
        executeSQL(conn, SCHEMA_FILE);
        System.out.println("Database schema initialized successfully.");
    }

    /**
     * Loads seed data into the database.
     */
    public static void loadSeedData() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        System.out.println("Loading seed data...");
        clearExistingData(conn);
        executeSQL(conn, SEED_DATA_FILE);
        upgradeSeedUserPasswords(conn);
        System.out.println("Seed data loaded successfully.");
    }

    private static void clearExistingData(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = OFF;");

            String[] tables = {
                "attendance",
                "player_match_stats",
                "match_events",
                "training_sessions",
                "matches",
                "players",
                "staff",
                "users"
            };

            for (String table : tables) {
                try {
                    stmt.executeUpdate("DELETE FROM " + table + ";");
                } catch (SQLException e) {
                    if (!e.getMessage().toLowerCase().contains("no such table")) {
                        throw e;
                    }
                }
            }

            try {
                stmt.executeUpdate("DELETE FROM sqlite_sequence;");
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("no such table")) {
                    throw e;
                }
            }

        } finally {
            if (stmt != null) {
                try {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                } catch (SQLException e) {
                    // Ignore inability to re-enable foreign keys; subsequent operations will fail fast.
                }
                stmt.close();
            }
        }
    }

    /**
     * Executes SQL statements from a resource file.
     */
    private static void executeSQL(Connection conn, String resourcePath) throws SQLException {
        try {
            // Read SQL file from resources
            InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new SQLException("SQL file not found: " + resourcePath);
            }

            // Read entire file into string
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            String fullSQL = fileContent.toString();

            // Remove comment lines
            String[] lines = fullSQL.split("\n");
            StringBuilder cleanSQL = new StringBuilder();

            for (String l : lines) {
                l = l.trim();
                if (l.startsWith("--") || l.isEmpty()) {
                    continue;
                }
                int commentPos = l.indexOf("--");
                if (commentPos > 0) {
                    l = l.substring(0, commentPos).trim();
                }
                cleanSQL.append(l).append(" ");
            }

            // Handle triggers with BEGIN...END
            String sqlText = cleanSQL.toString();
            java.util.List<String> statements = new java.util.ArrayList<>();
            StringBuilder currentStmt = new StringBuilder();
            boolean inTrigger = false;

            for (int i = 0; i < sqlText.length(); i++) {
                char c = sqlText.charAt(i);
                currentStmt.append(c);

                String soFar = currentStmt.toString().toUpperCase();
                if (soFar.contains("CREATE TRIGGER") && soFar.contains("BEGIN")) {
                    inTrigger = true;
                }

                if (inTrigger && soFar.endsWith("END;")) {
                    inTrigger = false;
                    statements.add(currentStmt.toString().trim());
                    currentStmt = new StringBuilder();
                }

                else if (!inTrigger && c == ';') {
                    statements.add(currentStmt.toString().trim());
                    currentStmt = new StringBuilder();
                }
            }

            // Add any remaining statement
            if (currentStmt.length() > 0) {
                statements.add(currentStmt.toString().trim());
            }

            // Execute statements
            Statement statement = conn.createStatement();

            for (String sql : statements) {
                sql = sql.trim();
                if (sql.isEmpty()) {
                    continue;
                }

                try {
                    statement.execute(sql);
                } catch (SQLException e) {
                    if (!e.getMessage().toLowerCase().contains("already exists")) {
                        System.err.println("Failed SQL: " + sql.substring(0, Math.min(100, sql.length())));
                        throw e;
                    }
                }
            }

            statement.close();

        } catch (Exception e) {
            throw new SQLException("Error executing SQL from " + resourcePath, e);
        }
    }

    private static void upgradeSeedUserPasswords(Connection conn) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE username = ? AND password_hash LIKE 'hashed_password_%'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : DEFAULT_SEED_PASSWORDS.entrySet()) {
                pstmt.setString(1, PasswordHasher.hash(entry.getValue()));
                pstmt.setString(2, entry.getKey());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Creates the data directory if it doesn't exist.
     */
    private static void createDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            if (dataDir.mkdir()) {
                System.out.println("Created data directory.");
            }
        }

        File backupDir = new File("data/backups");
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }
    }

    /**
     * Checks if the database has been initialized (if tables exist).
     */
    public static boolean isDatabaseInitialized() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Try to query users table
            stmt.executeQuery("SELECT COUNT(*) FROM users");
            stmt.close();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Main method for testing database initialization.
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== Database Initialization ===");

            // Initialize database
            initializeDatabase();

            // Check if initialized
            if (isDatabaseInitialized()) {
                System.out.println("Database initialized successfully!");
            }

            System.out.println("\nLoad seed data? (y/n)");
            loadSeedData();
            System.out.println("Seed data loaded!");

            // Close connection
            DatabaseConnection.closeConnection();

            System.out.println("\n=== Initialization Complete ===");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}