package com.futclub.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages database configuration settings.
 * Loads configuration from database.properties file.
 */
public class DatabaseConfig {
    
    private static final String CONFIG_FILE = "/config/database.properties";
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    /**
     * Load configuration from properties file.
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Configuration file not found: " + CONFIG_FILE);
                setDefaults();
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            setDefaults();
        }
    }
    
    /**
     * Set default configuration values.
     */
    private static void setDefaults() {
        properties.setProperty("database.url", "jdbc:sqlite:data/futclub.db");
        properties.setProperty("database.driver", "org.sqlite.JDBC");
        properties.setProperty("database.schema.auto.init", "true");
        properties.setProperty("database.log.queries", "false");
    }
    
    /**
     * Get database URL.
     */
    public static String getDatabaseUrl() {
        return properties.getProperty("database.url", "jdbc:sqlite:data/futclub.db");
    }
    
    /**
     * Get database driver class name.
     */
    public static String getDriverClassName() {
        return properties.getProperty("database.driver", "org.sqlite.JDBC");
    }
    
    /**
     * Check if schema should be auto-initialized.
     */
    public static boolean isAutoInitEnabled() {
        return Boolean.parseBoolean(properties.getProperty("database.schema.auto.init", "true"));
    }
    
    /**
     * Check if query logging is enabled.
     */
    public static boolean isQueryLoggingEnabled() {
        return Boolean.parseBoolean(properties.getProperty("database.log.queries", "false"));
    }
    
    /**
     * Get a configuration property by key.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get a configuration property with default value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
