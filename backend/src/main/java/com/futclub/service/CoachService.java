package com.futclub.service;

import com.futclub.database.dao.AttendanceDAO;
import com.futclub.database.dao.PlayerMatchStatsDAO;
import com.futclub.database.dao.StaffDAO;
import com.futclub.database.dao.TrainingSessionDAO;
import com.futclub.model.AttendanceRecord;
import com.futclub.model.CoachUser;
import com.futclub.model.PlayerMatchStats;
import com.futclub.model.Staff;
import com.futclub.model.TrainingSession;
import java.util.List;
import java.util.Objects;

/**
 * Exposes the operations a coach can perform within the system.
 */
public class CoachService {

    private final TrainingSessionDAO trainingSessionDAO;
    private final AttendanceDAO attendanceDAO;
    private final PlayerMatchStatsDAO playerMatchStatsDAO;
    private final StaffDAO staffDAO;

    public CoachService(TrainingSessionDAO trainingSessionDAO,
                        AttendanceDAO attendanceDAO,
                        PlayerMatchStatsDAO playerMatchStatsDAO,
                        StaffDAO staffDAO) {
        this.trainingSessionDAO = Objects.requireNonNull(trainingSessionDAO, "trainingSessionDAO");
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO");
        this.playerMatchStatsDAO = Objects.requireNonNull(playerMatchStatsDAO, "playerMatchStatsDAO");
        this.staffDAO = Objects.requireNonNull(staffDAO, "staffDAO");
    }

    public TrainingSession createSession(CoachUser coach, TrainingSession session) {
        requireCoach(coach);
        Objects.requireNonNull(session, "session");
        if (session.getCoachId() == null) {
            session.setCoachId(resolveCoachStaffId(coach));
        }
        trainingSessionDAO.insert(session);
        return session;
    }

    public void updateSession(CoachUser coach, TrainingSession session) {
        requireCoach(coach);
        Objects.requireNonNull(session, "session");
        if (session.getSessionId() <= 0) {
            throw new IllegalArgumentException("Session must have a valid id for update");
        }
        trainingSessionDAO.update(session);
    }

    public void deleteSession(CoachUser coach, int sessionId) {
        requireCoach(coach);
        trainingSessionDAO.delete(sessionId);
    }

    public List<TrainingSession> listSessions(CoachUser coach) {
        requireCoach(coach);
        int staffId = resolveCoachStaffId(coach);
        return trainingSessionDAO.getByCoachId(staffId);
    }

    public AttendanceRecord upsertAttendance(CoachUser coach, AttendanceRecord record) {
        requireCoach(coach);
        Objects.requireNonNull(record, "record");
        AttendanceRecord existing = attendanceDAO.getByPlayerAndSession(record.getPlayerId(), record.getSessionId());
        if (existing == null) {
            attendanceDAO.insert(record);
            return record;
        }
        record.setAttendanceId(existing.getAttendanceId());
        attendanceDAO.update(record);
        return record;
    }

    public List<AttendanceRecord> getAttendanceForSession(CoachUser coach, int sessionId) {
        requireCoach(coach);
        return attendanceDAO.getBySessionId(sessionId);
    }

    public PlayerMatchStats createPlayerPerformance(CoachUser coach, PlayerMatchStats stats) {
        requireCoach(coach);
        Objects.requireNonNull(stats, "stats");
        playerMatchStatsDAO.insert(stats);
        return stats;
    }

    public void updatePlayerPerformance(CoachUser coach, PlayerMatchStats stats) {
        requireCoach(coach);
        Objects.requireNonNull(stats, "stats");
        if (stats.getStatsId() <= 0) {
            throw new IllegalArgumentException("Stats entry must have an id");
        }
        playerMatchStatsDAO.update(stats);
    }

    public void deletePlayerPerformance(CoachUser coach, int statsId) {
        requireCoach(coach);
        playerMatchStatsDAO.delete(statsId);
    }

    public List<PlayerMatchStats> getPerformanceForMatch(CoachUser coach, int matchId) {
        requireCoach(coach);
        return playerMatchStatsDAO.getByMatchId(matchId);
    }

    private void requireCoach(CoachUser coach) {
        if (coach == null) {
            throw new IllegalArgumentException("Coach user required for this operation");
        }
    }

    private int resolveCoachStaffId(CoachUser coach) {
        Staff staff = staffDAO.getByUserId(coach.getUserId());
        if (staff == null) {
            throw new IllegalStateException("No staff record linked to coach user " + coach.getUsername());
        }
        return staff.getStaffId();
    }
}
