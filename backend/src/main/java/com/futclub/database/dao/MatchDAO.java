package com.futclub.database.dao;

import com.futclub.model.Match;
import java.sql.Timestamp;
import java.util.List;

public interface MatchDAO {
    Match getById(int matchId);
    List<Match> getAll();
    void insert(Match match);
    void update(Match match);
    void delete(int matchId);
    
    List<Match> getUpcomingMatches();
    List<Match> getCompletedMatches();
    List<Match> getByCompetition(String competition);
    List<Match> getMatchesInDateRange(Timestamp startDate, Timestamp endDate);
}
