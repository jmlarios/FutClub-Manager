package com.futclub.database.dao;

import com.futclub.model.Staff;
import java.util.List;

public interface StaffDAO {
    Staff getById(int staffId);
    List<Staff> getAll();
    void insert(Staff staff);
    void update(Staff staff);
    void delete(int staffId);
    
    Staff getByUserId(int userId);
    Staff getByEmail(String email);
}
