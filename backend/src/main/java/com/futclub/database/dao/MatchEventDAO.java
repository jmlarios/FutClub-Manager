package com.futclub.database.dao;

import com.futclub.model.MatchEvent;
import java.util.List;

public interface MatchEventDAO {
    MatchEvent getById(int eventId);
    List<MatchEvent> getAll();
    void insert(MatchEvent event);
    void update(MatchEvent event);
    void delete(int eventId);

    List<MatchEvent> getByMatchId(int matchId);
    List<MatchEvent> getByMatchIdOrdered(int matchId);
    List<MatchEvent> getByEventType(String eventType);
}
