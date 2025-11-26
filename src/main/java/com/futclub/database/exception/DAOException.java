package com.futclub.database.exception;

/**
 * Exception thrown by DAO operations when database operations fail.
 */
public class DAOException extends DatabaseException {
    
    public DAOException(String message) {
        super(message);
    }
    
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DAOException(Throwable cause) {
        super(cause);
    }
}
