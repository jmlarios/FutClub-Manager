package com.futclub.service.auth;

import com.futclub.database.dao.UserDAO;
import com.futclub.model.User;
import com.futclub.model.enums.UserRole;
import com.futclub.security.PasswordHasher;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles authentication workflows such as login and registration.
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
    }

    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        User user = userDAO.getByUsername(username.trim());
        if (user == null || !user.isActive()) {
            return Optional.empty();
        }
        if (!PasswordHasher.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        userDAO.updateLastLogin(user.getUserId());
        return Optional.of(user);
    }

    public User register(User user, String plainPassword) {
        validatePreconditions(user, plainPassword);
        user.setPasswordHash(PasswordHasher.hash(plainPassword));
        userDAO.insert(user);
        return user;
    }

    public void changePassword(int userId, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        User user = userDAO.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for id " + userId);
        }
        user.setPasswordHash(PasswordHasher.hash(newPassword));
        userDAO.update(user);
    }

    public boolean hasAccess(User user, UserRole requiredRole) {
        return user != null && requiredRole != null && user.hasRole(requiredRole);
    }

    private void validatePreconditions(User user, String plainPassword) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role must be defined");
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
