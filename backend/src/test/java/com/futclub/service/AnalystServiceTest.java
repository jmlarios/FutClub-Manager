package com.futclub.service;

import com.futclub.database.BaseDAOTest;
import com.futclub.database.dao.MatchEventDAO;
import com.futclub.database.dao.MatchEventDAOImpl;
import com.futclub.database.dao.PlayerMatchStatsDAO;
import com.futclub.database.dao.PlayerMatchStatsDAOImpl;
import com.futclub.database.dao.UserDAO;
import com.futclub.database.dao.UserDAOImpl;
import com.futclub.model.AnalystUser;
import com.futclub.model.MatchEvent;
import com.futclub.model.PlayerMatchStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class AnalystServiceTest extends BaseDAOTest {

    private AnalystService analystService;
    private AnalystUser analystUser;

    @BeforeEach
    void initService() {
        PlayerMatchStatsDAO statsDAO = new PlayerMatchStatsDAOImpl();
        MatchEventDAO eventDAO = new MatchEventDAOImpl();
        analystService = new AnalystService(statsDAO, eventDAO);

        UserDAO userDAO = new UserDAOImpl();
        analystUser = (AnalystUser) userDAO.getByUsername("analyst.jones");
        assertThat(analystUser).isNotNull();
    }

    @Test
    void createsAndUpdatesMatchEvent() {
        MatchEvent event = new MatchEvent();
        event.setMatchId(2);
        event.setPlayerId(12);
        event.setEventType("SHOT");
        event.setMinute(5);
        event.setSecond(22);
        event.setDescription("Early chance forces save");

        analystService.logMatchEvent(analystUser, event);
        assertThat(event.getEventId()).isGreaterThan(0);

        event.setDescription("Shot deflected for corner");
        analystService.updateMatchEvent(analystUser, event);

        List<MatchEvent> events = analystService.getTimelineForMatch(analystUser, 2);
        assertThat(events).extracting(MatchEvent::getDescription).contains("Shot deflected for corner");

        analystService.deleteMatchEvent(analystUser, event.getEventId());
        events = analystService.getTimelineForMatch(analystUser, 2);
        assertThat(events).extracting(MatchEvent::getEventId).doesNotContain(event.getEventId());
    }

    @Test
    void managesPlayerMatchStats() {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setPlayerId(8);
        stats.setMatchId(4);
        stats.setMinutesPlayed(30);
        stats.setGoals(0);
        stats.setAssists(0);
        stats.setRating(6.9);
        stats.setShots(1);
        stats.setShotsOnTarget(0);
        stats.setPassesCompleted(20);
        stats.setPassesAttempted(24);
        stats.setTackles(1);
        stats.setInterceptions(0);
        stats.setYellowCards(0);
        stats.setRedCards(0);
        stats.setFoulsCommitted(1);
        stats.setFoulsWon(2);
        stats.setWasStarter(false);

        analystService.createPlayerMatchStats(analystUser, stats);
        assertThat(stats.getStatsId()).isGreaterThan(0);

        stats.setAssists(1);
        analystService.updatePlayerMatchStats(analystUser, stats);

        List<PlayerMatchStats> performance = analystService.viewPlayerPerformanceByMatch(analystUser, 4);
        assertThat(performance).extracting(PlayerMatchStats::getStatsId).contains(stats.getStatsId());

        analystService.deletePlayerMatchStats(analystUser, stats.getStatsId());
        performance = analystService.viewPlayerPerformanceByMatch(analystUser, 4);
        assertThat(performance).extracting(PlayerMatchStats::getStatsId).doesNotContain(stats.getStatsId());
    }
}
