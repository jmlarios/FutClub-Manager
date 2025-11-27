package com.futclub.database.dao;

import com.futclub.model.PlayerMatchStats;
import java.util.List;

public interface PlayerMatchStatsDAO {
    PlayerMatchStats getById(int statsId);
    List<PlayerMatchStats> getAll();
    void insert(PlayerMatchStats stats);
    void update(PlayerMatchStats stats);
    void delete(int statsId);

    List<PlayerMatchStats> getByMatchId(int matchId);
    List<PlayerMatchStats> getByPlayerId(int playerId);
    PlayerMatchStats getByPlayerAndMatch(int playerId, int matchId);
    List<PlayerMatchStats> getTopScorers(int limit);
    List<PlayerMatchStats> getTopRatedPlayers(int limit);
}
