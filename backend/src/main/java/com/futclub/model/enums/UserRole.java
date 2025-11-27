package com.futclub.model.enums;

/**
 * Enumerates the supported FutClub user roles.
 */
public enum UserRole {
    COACH,
    ANALYST,
    ADMINISTRATOR;

    /**
     * Parses the provided database value into an enum.
     *
     * @param value raw role string stored in the database
     * @return enum matching the value
     * @throws IllegalArgumentException if the value is null or unknown
     */
    public static UserRole fromDatabaseValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Role value cannot be null");
        }
        return UserRole.valueOf(value.trim().toUpperCase());
    }

    /**
     * Formats this enum for database persistence.
     */
    public String toDatabaseValue() {
        return name();
    }
}
