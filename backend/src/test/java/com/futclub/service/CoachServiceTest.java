package com.futclub.service;

import com.futclub.database.BaseDAOTest;
import com.futclub.database.dao.AttendanceDAO;
import com.futclub.database.dao.AttendanceDAOImpl;
import com.futclub.database.dao.PlayerMatchStatsDAO;
import com.futclub.database.dao.PlayerMatchStatsDAOImpl;
import com.futclub.database.dao.StaffDAO;
import com.futclub.database.dao.StaffDAOImpl;
import com.futclub.database.dao.TrainingSessionDAO;
import com.futclub.database.dao.TrainingSessionDAOImpl;
import com.futclub.database.dao.UserDAO;
import com.futclub.database.dao.UserDAOImpl;
import com.futclub.model.AttendanceRecord;
import com.futclub.model.CoachUser;
import com.futclub.model.PlayerMatchStats;
import com.futclub.model.TrainingSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CoachServiceTest extends BaseDAOTest {

    private CoachService coachService;
    private CoachUser coachUser;

    @BeforeEach
    void initService() {
        TrainingSessionDAO trainingSessionDAO = new TrainingSessionDAOImpl();
        AttendanceDAO attendanceDAO = new AttendanceDAOImpl();
        PlayerMatchStatsDAO playerMatchStatsDAO = new PlayerMatchStatsDAOImpl();
        StaffDAO staffDAO = new StaffDAOImpl();
        coachService = new CoachService(trainingSessionDAO, attendanceDAO, playerMatchStatsDAO, staffDAO);

        UserDAO userDAO = new UserDAOImpl();
        coachUser = (CoachUser) userDAO.getByUsername("coach.smith");
        assertThat(coachUser).isNotNull();
    }

    @Test
    void createsTrainingSessionAndAssignsCoach() {
        TrainingSession session = new TrainingSession();
        session.setSessionDate(Timestamp.valueOf("2025-01-05 10:00:00"));
        session.setFocus("High press rehearsal");
        session.setLocation("Pitch 2");
        session.setDurationMinutes(75);
        session.setIntensity("HIGH");
        session.setNotes("Focus on transitions");

        coachService.createSession(coachUser, session);

        assertThat(session.getSessionId()).isGreaterThan(0);
        List<TrainingSession> sessions = coachService.listSessions(coachUser);
        assertThat(sessions).extracting(TrainingSession::getSessionId).contains(session.getSessionId());
    }

    @Test
    void upsertAttendanceInsertsAndUpdates() {
        AttendanceRecord attendance = new AttendanceRecord();
        attendance.setPlayerId(1);
        attendance.setSessionId(1);
        attendance.setStatus("PRESENT");
        attendance.setNotes("Arrived early");

        coachService.upsertAttendance(coachUser, attendance);
        assertThat(attendance.getAttendanceId()).isGreaterThan(0);

        attendance.setStatus("LATE");
        attendance.setNotes("Traffic delay");
        coachService.upsertAttendance(coachUser, attendance);

        List<AttendanceRecord> sessionAttendance = coachService.getAttendanceForSession(coachUser, 1);
        assertThat(sessionAttendance)
            .filteredOn(a -> a.getPlayerId() == 1)
            .extracting(AttendanceRecord::getStatus)
            .contains("LATE");
    }

    @Test
    void managePlayerPerformanceLifecycle() {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setPlayerId(1);
        stats.setMatchId(4);
        stats.setMinutesPlayed(45);
        stats.setGoals(0);
        stats.setAssists(1);
        stats.setRating(7.2);
        stats.setShots(1);
        stats.setShotsOnTarget(1);
        stats.setPassesCompleted(32);
        stats.setPassesAttempted(35);
        stats.setTackles(0);
        stats.setInterceptions(1);
        stats.setYellowCards(0);
        stats.setRedCards(0);
        stats.setFoulsCommitted(0);
        stats.setFoulsWon(2);
        stats.setWasStarter(true);

        coachService.createPlayerPerformance(coachUser, stats);
        assertThat(stats.getStatsId()).isGreaterThan(0);

        stats.setRating(8.0);
        coachService.updatePlayerPerformance(coachUser, stats);

        List<PlayerMatchStats> matchStats = coachService.getPerformanceForMatch(coachUser, 4);
        assertThat(matchStats).extracting(PlayerMatchStats::getStatsId).contains(stats.getStatsId());

        coachService.deletePlayerPerformance(coachUser, stats.getStatsId());
        matchStats = coachService.getPerformanceForMatch(coachUser, 4);
        assertThat(matchStats).extracting(PlayerMatchStats::getStatsId).doesNotContain(stats.getStatsId());
    }
}
