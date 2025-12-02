package com.futclub.frontend.backend;

import com.futclub.model.AdministratorUser;
import com.futclub.model.AnalystUser;
import com.futclub.model.CoachUser;
import com.futclub.model.Match;
import com.futclub.model.MatchEvent;
import com.futclub.model.Player;
import com.futclub.model.PlayerMatchStats;
import com.futclub.model.Staff;
import com.futclub.model.TrainingSession;
import com.futclub.model.User;
import com.futclub.model.enums.UserRole;
import com.futclub.service.CoachService;
import com.futclub.service.auth.AuthenticationService;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * High-level facade that adapts backend services for the JavaFX UI.
 */
public class BackendFacade {

    private final BackendContext context;

    public BackendFacade() {
        this(BackendContext.getInstance());
    }

    BackendFacade(BackendContext context) {
        this.context = context;
    }

    public LoginResult login(String username, String password) {
        AuthenticationService auth = context.authenticationService();
        Optional<User> userOpt = auth.login(username, password);
        if (userOpt.isEmpty()) {
            return LoginResult.failure("Invalid username or password.");
        }

        User user = userOpt.get();
        Staff staff = context.staffDAO().getByUserId(user.getUserId());
        return LoginResult.success(user, staff);
    }

    public RegistrationResult register(String fullName,
                                       String username,
                                       String password,
                                       UserRole role) {
        if (fullName == null || fullName.isBlank() ||
            username == null || username.isBlank() ||
            password == null || password.isBlank() ||
            role == null) {
            return RegistrationResult.failure("All fields are required.");
        }

        if (context.userDAO().getByUsername(username.trim()) != null) {
            return RegistrationResult.failure("Username already exists. Choose another one.");
        }

        User newUser = createUserForRole(role);
        newUser.setUsername(username.trim());
        newUser.setActive(true);

        context.authenticationService().register(newUser, password);
        if (newUser.getUserId() <= 0) {
            return RegistrationResult.failure("Unable to create the user account.");
        }

        Staff staff = new Staff();
        staff.setFullName(fullName.trim());
        staff.setUserId(newUser.getUserId());
        staff.setHireDate(Date.valueOf(LocalDate.now()));
        context.staffDAO().insert(staff);

        return RegistrationResult.success(newUser, staff);
    }

    public DashboardSnapshot loadDashboard(User user) {
        if (user == null) {
            return new DashboardSnapshot(0, 0, 0, 0, 0, null, null, 0);
        }
        List<Player> players = context.playerDAO().getAll();
        int totalPlayers = players.size();
        int availablePlayers = (int) players.stream()
                .filter(p -> "AVAILABLE".equalsIgnoreCase(p.getStatus()))
                .count();
        int injuredPlayers = (int) players.stream()
                .filter(p -> "INJURED".equalsIgnoreCase(p.getStatus()))
                .count();

        List<Match> upcomingMatches = context.matchDAO().getUpcomingMatches();
        Match nextMatch = upcomingMatches.isEmpty() ? null : upcomingMatches.get(0);

        List<TrainingSession> sessionsSource = resolveSessionsSource(user);
        Instant now = Instant.now();
        List<TrainingSession> upcomingSessions = sessionsSource.stream()
            .filter(session -> session.getSessionDate() != null && session.getSessionDate().toInstant().isAfter(now))
            .sorted(Comparator.comparing(TrainingSession::getSessionDate))
            .toList();
        TrainingSession nextTraining = upcomingSessions.isEmpty() ? null : upcomingSessions.get(0);

        int totalStaff = context.staffDAO().getAll().size();

        return new DashboardSnapshot(
                totalPlayers,
                availablePlayers,
                injuredPlayers,
                upcomingMatches.size(),
            upcomingSessions.size(),
                nextMatch,
                nextTraining,
                totalStaff
        );
    }

    public List<Player> listPlayers(User user) {
        if (user == null) {
            return List.of();
        }
        return List.copyOf(context.playerDAO().getAll());
    }

    public List<Match> listUpcomingMatches(User user) {
        if (user == null) {
            return List.of();
        }
        return List.copyOf(context.matchDAO().getUpcomingMatches());
    }

    public List<TrainingSession> listUpcomingSessions(User user) {
        if (user == null) {
            return List.of();
        }
        List<TrainingSession> sessions = resolveSessionsSource(user);
        Instant now = Instant.now();
        return sessions.stream()
                .filter(session -> session.getSessionDate() != null && session.getSessionDate().toInstant().isAfter(now))
                .sorted(Comparator.comparing(TrainingSession::getSessionDate))
                .toList();
    }

    public List<TrainingSession> listCoachSessions(User user) {
        if (!(user instanceof CoachUser coachUser)) {
            return List.of();
        }
        return context.coachService().listSessions(coachUser);
    }

    public List<Match> listAllMatches(User user) {
        if (user == null) {
            return List.of();
        }
        return context.matchDAO().getAll();
    }

    public OperationResult<Player> registerPlayer(User user, Player player) {
        if (!(user instanceof AdministratorUser admin)) {
            return OperationResult.failure("Only administrators can register players.");
        }
        try {
            Player registered = context.administratorService().registerPlayer(admin, player);
            return OperationResult.success("Player registered successfully.", registered);
        } catch (Exception ex) {
            return OperationResult.failure("Unable to register player: " + ex.getMessage());
        }
    }

    public OperationResult<Match> scheduleMatch(User user, Match match) {
        if (!(user instanceof AdministratorUser admin)) {
            return OperationResult.failure("Only administrators can schedule matches.");
        }
        try {
            Match scheduled = context.administratorService().scheduleMatch(admin, match);
            return OperationResult.success("Match scheduled successfully.", scheduled);
        } catch (Exception ex) {
            return OperationResult.failure("Unable to schedule match: " + ex.getMessage());
        }
    }

    public OperationResult<TrainingSession> createTrainingSession(User user, TrainingSession session) {
        if (!(user instanceof CoachUser coachUser)) {
            return OperationResult.failure("Only coaches can create training sessions.");
        }
        try {
            TrainingSession created = context.coachService().createSession(coachUser, session);
            return OperationResult.success("Training session created successfully.", created);
        } catch (Exception ex) {
            return OperationResult.failure("Unable to create training session: " + ex.getMessage());
        }
    }

    public OperationResult<MatchEvent> logMatchEvent(User user, MatchEvent event) {
        if (!(user instanceof AnalystUser analystUser)) {
            return OperationResult.failure("Only analysts can log match events.");
        }
        try {
            MatchEvent saved = context.analystService().logMatchEvent(analystUser, event);
            return OperationResult.success("Match event logged successfully.", saved);
        } catch (Exception ex) {
            return OperationResult.failure("Unable to log match event: " + ex.getMessage());
        }
    }

    public OperationResult<PlayerMatchStats> recordPlayerMatchStats(User user, PlayerMatchStats stats) {
        if (!(user instanceof AnalystUser analystUser)) {
            return OperationResult.failure("Only analysts can record match statistics.");
        }
        try {
            PlayerMatchStats saved = context.analystService().createPlayerMatchStats(analystUser, stats);
            return OperationResult.success("Player match stats recorded successfully.", saved);
        } catch (Exception ex) {
            return OperationResult.failure("Unable to record match stats: " + ex.getMessage());
        }
    }

    public List<MatchEvent> getMatchTimeline(User user, int matchId) {
        if (!(user instanceof AnalystUser analystUser)) {
            return List.of();
        }
        return context.analystService().getTimelineForMatch(analystUser, matchId);
    }

    public List<PlayerMatchStats> getTopScorers(User user) {
        if (user == null) {
            return List.of();
        }
        return context.playerMatchStatsDAO().getTopScorers(10);
    }

    public List<PlayerMatchStats> getTopRatedPlayers(User user) {
        if (user == null) {
            return List.of();
        }
        return context.playerMatchStatsDAO().getTopRatedPlayers(10);
    }

    public List<PlayerMatchStats> getPlayerStats(User user, int playerId) {
        if (user == null) {
            return List.of();
        }
        return context.playerMatchStatsDAO().getByPlayerId(playerId);
    }

    public List<PlayerMatchStats> getMatchStats(User user, int matchId) {
        if (user == null) {
            return List.of();
        }
        return context.playerMatchStatsDAO().getByMatchId(matchId);
    }

    private List<TrainingSession> resolveSessionsSource(User user) {
        if (user instanceof CoachUser coachUser) {
            CoachService coachService = context.coachService();
            return coachService.listSessions(coachUser);
        }
        return context.trainingSessionDAO().getAll();
    }

    private User createUserForRole(UserRole role) {
        return switch (role) {
            case COACH -> new CoachUser();
            case ANALYST -> new AnalystUser();
            case ADMINISTRATOR -> new AdministratorUser();
        };
    }
}
