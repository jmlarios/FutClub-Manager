package com.futclub.database.dao;

import com.futclub.model.User;
import com.futclub.model.enums.UserRole;
import java.util.List;

public interface UserDAO {
    User getById(int userId);
    List<User> getAll();
    void insert(User user);
    void update(User user);
    void delete(int userId);
    
    User getByUsername(String username);
    List<User> getByRole(UserRole role);
    boolean authenticate(String username, String plainPassword);
    void updateLastLogin(int userId);
}
