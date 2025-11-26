package com.futclub.database.dao;

import com.futclub.model.TrainingSession;
import java.sql.Timestamp;
import java.util.List;

public interface TrainingSessionDAO {
    TrainingSession getById(int sessionId);
    List<TrainingSession> getAll();
    void insert(TrainingSession session);
    void update(TrainingSession session);
    void delete(int sessionId);
    
    List<TrainingSession> getByCoachId(int coachId);
    List<TrainingSession> getSessionsInDateRange(Timestamp startDate, Timestamp endDate);
    List<TrainingSession> getRecentSessions(int limit);
}
