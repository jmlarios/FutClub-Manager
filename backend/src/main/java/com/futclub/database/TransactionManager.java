package com.futclub.database;

import com.futclub.database.exception.DatabaseException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database transactions.
 * Ensures that multiple database operations either all succeed or all fail together.
 */
public class TransactionManager {
    
    /**
     * Executes a transaction with automatic commit/rollback.
     * If any exception occurs, the transaction is rolled back.
     * 
     * @param transaction The transaction to execute
     * @throws DatabaseException if transaction fails
     */
    public static void executeTransaction(Transaction transaction) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Execute transaction logic
            transaction.execute(conn);
            
            // Commit if successful
            conn.commit();
            
        } catch (Exception e) {
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw new DatabaseException("Transaction failed", e);
            
        } finally {
            // Restore auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error restoring auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Functional interface for transaction operations.
     */
    @FunctionalInterface
    public interface Transaction {
        void execute(Connection conn) throws Exception;
    }
}
