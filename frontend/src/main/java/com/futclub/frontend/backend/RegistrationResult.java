package com.futclub.frontend.backend;

import com.futclub.model.Staff;
import com.futclub.model.User;

/**
 * Result wrapper for registration workflows.
 */
public record RegistrationResult(boolean success, String message, User user, Staff staff) {

    public static RegistrationResult success(User user, Staff staff) {
        return new RegistrationResult(true, "Account created successfully.", user, staff);
    }

    public static RegistrationResult failure(String message) {
        return new RegistrationResult(false, message, null, null);
    }
}
