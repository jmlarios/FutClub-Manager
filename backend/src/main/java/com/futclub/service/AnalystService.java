package com.futclub.service;

import com.futclub.database.dao.MatchEventDAO;
import com.futclub.database.dao.PlayerMatchStatsDAO;
import com.futclub.model.AnalystUser;
import com.futclub.model.MatchEvent;
import com.futclub.model.PlayerMatchStats;
import java.util.List;
import java.util.Objects;

/**
 * Exposes analytical workflows such as event logging and match statistics management.
 */
public class AnalystService {

    private final PlayerMatchStatsDAO playerMatchStatsDAO;
    private final MatchEventDAO matchEventDAO;

    public AnalystService(PlayerMatchStatsDAO playerMatchStatsDAO,
                          MatchEventDAO matchEventDAO) {
        this.playerMatchStatsDAO = Objects.requireNonNull(playerMatchStatsDAO, "playerMatchStatsDAO");
        this.matchEventDAO = Objects.requireNonNull(matchEventDAO, "matchEventDAO");
    }

    public PlayerMatchStats createPlayerMatchStats(AnalystUser analyst, PlayerMatchStats stats) {
        requireAnalyst(analyst);
        Objects.requireNonNull(stats, "stats");
        playerMatchStatsDAO.insert(stats);
        return stats;
    }

    public void updatePlayerMatchStats(AnalystUser analyst, PlayerMatchStats stats) {
        requireAnalyst(analyst);
        Objects.requireNonNull(stats, "stats");
        if (stats.getStatsId() <= 0) {
            throw new IllegalArgumentException("Stats entry must have an id");
        }
        playerMatchStatsDAO.update(stats);
    }

    public void deletePlayerMatchStats(AnalystUser analyst, int statsId) {
        requireAnalyst(analyst);
        playerMatchStatsDAO.delete(statsId);
    }

    public List<PlayerMatchStats> viewPlayerPerformanceByMatch(AnalystUser analyst, int matchId) {
        requireAnalyst(analyst);
        return playerMatchStatsDAO.getByMatchId(matchId);
    }

    public MatchEvent logMatchEvent(AnalystUser analyst, MatchEvent event) {
        requireAnalyst(analyst);
        Objects.requireNonNull(event, "event");
        matchEventDAO.insert(event);
        return event;
    }

    public void updateMatchEvent(AnalystUser analyst, MatchEvent event) {
        requireAnalyst(analyst);
        Objects.requireNonNull(event, "event");
        if (event.getEventId() <= 0) {
            throw new IllegalArgumentException("Event must have an id");
        }
        matchEventDAO.update(event);
    }

    public void deleteMatchEvent(AnalystUser analyst, int eventId) {
        requireAnalyst(analyst);
        matchEventDAO.delete(eventId);
    }

    public List<MatchEvent> getTimelineForMatch(AnalystUser analyst, int matchId) {
        requireAnalyst(analyst);
        return matchEventDAO.getByMatchIdOrdered(matchId);
    }

    private void requireAnalyst(AnalystUser analyst) {
        if (analyst == null) {
            throw new IllegalArgumentException("Analyst user required for this operation");
        }
    }
}
