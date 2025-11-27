package com.futclub.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps BCrypt hashing to keep a single implementation point.
 */
public final class PasswordHasher {

    private static final int DEFAULT_ROUNDS = 12;

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(DEFAULT_ROUNDS));
    }

    public static boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
