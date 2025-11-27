package com.futclub.frontend.backend;

import com.futclub.model.Staff;
import com.futclub.model.User;

/**
 * Result wrapper for login attempts.
 */
public record LoginResult(boolean success, String message, User user, Staff staff) {

    public static LoginResult success(User user, Staff staff) {
        return new LoginResult(true, null, user, staff);
    }

    public static LoginResult failure(String message) {
        return new LoginResult(false, message, null, null);
    }
}
