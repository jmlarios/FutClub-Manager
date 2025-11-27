package com.futclub.frontend.backend;

import com.futclub.database.DatabaseInitializer;
import com.futclub.database.dao.AttendanceDAO;
import com.futclub.database.dao.AttendanceDAOImpl;
import com.futclub.database.dao.MatchDAO;
import com.futclub.database.dao.MatchDAOImpl;
import com.futclub.database.dao.MatchEventDAO;
import com.futclub.database.dao.MatchEventDAOImpl;
import com.futclub.database.dao.PlayerDAO;
import com.futclub.database.dao.PlayerDAOImpl;
import com.futclub.database.dao.PlayerMatchStatsDAO;
import com.futclub.database.dao.PlayerMatchStatsDAOImpl;
import com.futclub.database.dao.StaffDAO;
import com.futclub.database.dao.StaffDAOImpl;
import com.futclub.database.dao.TrainingSessionDAO;
import com.futclub.database.dao.TrainingSessionDAOImpl;
import com.futclub.database.dao.UserDAO;
import com.futclub.database.dao.UserDAOImpl;
import com.futclub.service.AdministratorService;
import com.futclub.service.AnalystService;
import com.futclub.service.CoachService;
import com.futclub.service.auth.AuthenticationService;
import java.sql.SQLException;

/**
 * Centralises backend services and DAO instances for the JavaFX frontend.
 */
public final class BackendContext {

    private static BackendContext instance;

    private final UserDAO userDAO;
    private final PlayerDAO playerDAO;
    private final StaffDAO staffDAO;
    private final MatchDAO matchDAO;
    private final TrainingSessionDAO trainingSessionDAO;
    private final AttendanceDAO attendanceDAO;
    private final PlayerMatchStatsDAO playerMatchStatsDAO;
    private final MatchEventDAO matchEventDAO;

    private final AuthenticationService authenticationService;
    private final AdministratorService administratorService;
    private final CoachService coachService;
    private final AnalystService analystService;

    private BackendContext() {
        initializeSchema();
        this.userDAO = new UserDAOImpl();
        ensureSeedData();
        this.playerDAO = new PlayerDAOImpl();
        this.staffDAO = new StaffDAOImpl();
        this.matchDAO = new MatchDAOImpl();
        this.trainingSessionDAO = new TrainingSessionDAOImpl();
        this.attendanceDAO = new AttendanceDAOImpl();
        this.playerMatchStatsDAO = new PlayerMatchStatsDAOImpl();
        this.matchEventDAO = new MatchEventDAOImpl();

        this.authenticationService = new AuthenticationService(userDAO);
        this.administratorService = new AdministratorService(playerDAO, staffDAO, matchDAO);
        this.coachService = new CoachService(trainingSessionDAO, attendanceDAO, playerMatchStatsDAO, staffDAO);
        this.analystService = new AnalystService(playerMatchStatsDAO, matchEventDAO);
    }

    public static synchronized BackendContext getInstance() {
        if (instance == null) {
            instance = new BackendContext();
        }
        return instance;
    }

    private void initializeSchema() {
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
    }

    private void ensureSeedData() {
        try {
            if (userDAO.getAll().isEmpty()) {
                DatabaseInitializer.loadSeedData();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load seed data", e);
        }
    }

    public AuthenticationService authenticationService() {
        return authenticationService;
    }

    public AdministratorService administratorService() {
        return administratorService;
    }

    public CoachService coachService() {
        return coachService;
    }

    public AnalystService analystService() {
        return analystService;
    }

    public UserDAO userDAO() {
        return userDAO;
    }

    public PlayerDAO playerDAO() {
        return playerDAO;
    }

    public StaffDAO staffDAO() {
        return staffDAO;
    }

    public MatchDAO matchDAO() {
        return matchDAO;
    }

    public TrainingSessionDAO trainingSessionDAO() {
        return trainingSessionDAO;
    }

    public AttendanceDAO attendanceDAO() {
        return attendanceDAO;
    }

    public PlayerMatchStatsDAO playerMatchStatsDAO() {
        return playerMatchStatsDAO;
    }

    public MatchEventDAO matchEventDAO() {
        return matchEventDAO;
    }
}
