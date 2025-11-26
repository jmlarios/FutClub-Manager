package com.futclub.database.dao;

import com.futclub.model.User;
import java.util.List;

public interface UserDAO {
    User getById(int userId);
    List<User> getAll();
    void insert(User user);
    void update(User user);
    void delete(int userId);
    
    User getByUsername(String username);
    List<User> getByRole(String role);
    boolean authenticate(String username, String passwordHash);
    void updateLastLogin(int userId);
}
