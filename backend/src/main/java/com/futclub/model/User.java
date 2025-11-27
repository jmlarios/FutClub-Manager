package com.futclub.model;

import com.futclub.model.enums.UserRole;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Base class that represents an authenticated FutClub user.
 */
public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private UserRole role;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean active = true;

    public User() {
    }

    protected User(UserRole role) {
        this.role = Objects.requireNonNull(role, "role");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "role");
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean hasRole(UserRole expectedRole) {
        return role == expectedRole;
    }
}
