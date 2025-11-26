package com.futclub.database.dao;

import com.futclub.model.AttendanceRecord;
import java.util.List;

public interface AttendanceDAO {
    AttendanceRecord getById(int attendanceId);
    List<AttendanceRecord> getAll();
    void insert(AttendanceRecord attendance);
    void update(AttendanceRecord attendance);
    void delete(int attendanceId);
    
    List<AttendanceRecord> getBySessionId(int sessionId);
    List<AttendanceRecord> getByPlayerId(int playerId);
    AttendanceRecord getByPlayerAndSession(int playerId, int sessionId);
    int getAttendanceCountByPlayer(int playerId, String status);
}
