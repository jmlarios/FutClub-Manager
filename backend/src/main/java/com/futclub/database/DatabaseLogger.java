package com.futclub.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for database operations.
 * Provides consistent logging format across the database layer.
 */
public class DatabaseLogger {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean loggingEnabled = true;
    private static LogLevel minimumLevel = LogLevel.INFO;
    
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Enable or disable logging.
     */
    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }
    
    /**
     * Set minimum log level to display.
     */
    public static void setMinimumLevel(LogLevel level) {
        minimumLevel = level;
    }
    
    /**
     * Log a debug message.
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Log an info message.
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Log a warning message.
     */
    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }
    
    /**
     * Log an error message.
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Log an error message with exception.
     */
    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message + " - " + throwable.getMessage());
        if (loggingEnabled && LogLevel.ERROR.ordinal() >= minimumLevel.ordinal()) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Core logging method.
     */
    private static void log(LogLevel level, String message) {
        if (!loggingEnabled || level.ordinal() < minimumLevel.ordinal()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);
        
        if (level == LogLevel.ERROR || level == LogLevel.WARN) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
    }
}
