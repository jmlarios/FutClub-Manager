package com.futclub.frontend.ui;

import com.futclub.frontend.backend.BackendFacade;
import com.futclub.frontend.backend.DashboardSnapshot;
import com.futclub.frontend.backend.OperationResult;
import com.futclub.model.Match;
import com.futclub.model.MatchEvent;
import com.futclub.model.Player;
import com.futclub.model.PlayerMatchStats;
import com.futclub.model.Staff;
import com.futclub.model.TrainingSession;
import com.futclub.model.User;
import com.futclub.model.enums.UserRole;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class HomeView {

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.getDefault());
        private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        private static final List<String> PLAYER_POSITIONS = List.of("GK", "CB", "LB", "RB", "CDM", "CM", "CAM", "LW", "RW", "ST");
        private static final List<String> VENUES = List.of("HOME", "AWAY", "NEUTRAL");
        private static final List<String> TRAINING_INTENSITIES = List.of("LOW", "MEDIUM", "HIGH");
        private static final List<String> MATCH_EVENT_TYPES = List.of(
            "GOAL", "ASSIST", "SHOT", "YELLOW_CARD", "RED_CARD", "FOUL", "PENALTY", "SUBSTITUTION", "OTHER"
        );

    private final MainApp mainApp;
    private final User user;
    private final Staff staff;

    public HomeView(MainApp mainApp, User user, Staff staff) {
        this.mainApp = mainApp;
        this.user = user;
        this.staff = staff;
    }

    public javafx.scene.Scene getScene() {
        BackendFacade backend = mainApp.getBackendFacade();
        DashboardSnapshot snapshot = backend.loadDashboard(user);

        BorderPane root = new BorderPane();
        root.setId("root");
        root.setPadding(new Insets(20));

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Label welcome = new Label("Welcome, " + resolveDisplayName());
        welcome.getStyleClass().add("welcome-label");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("secondary-button");

        topBar.getChildren().addAll(welcome, logoutButton);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));
        root.setTop(topBar);

        VBox center = new VBox(20);
        center.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Club Overview");
        title.getStyleClass().add("section-title");

        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);

        VBox playersCard = createStatCard(
                "Players",
                Integer.toString(snapshot.totalPlayers()),
                snapshot.availablePlayers() + " available · " + snapshot.injuredPlayers() + " injured"
        );

        VBox matchesCard = createStatCard(
                "Matches",
                Integer.toString(snapshot.upcomingMatches()),
                snapshot.nextMatch() != null ? formatMatch(snapshot.nextMatch()) : "No upcoming match"
        );

        VBox trainingCard = createStatCard(
                "Training",
                Integer.toString(snapshot.upcomingSessions()),
                snapshot.nextTraining() != null ? formatTraining(snapshot.nextTraining()) : "No upcoming session"
        );

        statsRow.getChildren().addAll(playersCard, matchesCard, trainingCard);

        Label staffSummary = new Label("Active staff members: " + snapshot.totalStaff());
        staffSummary.getStyleClass().add("section-subtitle");

        center.getChildren().add(title);
        center.getChildren().add(statsRow);
        center.getChildren().add(staffSummary);

        VBox roleActions = createRoleActions();
        if (roleActions != null) {
            center.getChildren().add(roleActions);
        }

        root.setCenter(center);

        HBox bottom = new HBox(15);
        bottom.setAlignment(Pos.CENTER);

        Button playersButton = new Button("View Players");
        playersButton.getStyleClass().add("primary-button");

        Button matchesButton = new Button("Upcoming Matches");
        matchesButton.getStyleClass().add("primary-button");

        Button trainingButton = new Button("Upcoming Training");
        trainingButton.getStyleClass().add("primary-button");

        bottom.getChildren().addAll(playersButton, matchesButton, trainingButton);
        BorderPane.setMargin(bottom, new Insets(20, 0, 0, 0));
        root.setBottom(bottom);

        logoutButton.setOnAction(e -> mainApp.logout());
        playersButton.setOnAction(e -> showPlayersDialog());
        matchesButton.setOnAction(e -> showMatchesDialog());
        trainingButton.setOnAction(e -> showTrainingDialog());

        return new javafx.scene.Scene(root, 820, 520);
    }

    private VBox createStatCard(String title, String value, String subtitle) {
        Label titleLabel = new Label(title.toUpperCase(Locale.getDefault()));
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("stat-subtitle");

        VBox card = new VBox(6, titleLabel, valueLabel, subtitleLabel);
        card.getStyleClass().add("dashboard-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(16));
        card.setPrefWidth(240);
        return card;
    }

    private void showPlayersDialog() {
        BackendFacade backend = mainApp.getBackendFacade();
        List<Player> players = backend.listPlayers(user);
        String content = players.isEmpty()
                ? "No players found."
                : players.stream()
                .map(this::formatPlayer)
                .collect(Collectors.joining("\n"));
        showInfoDialog("Players", "Current squad", content);
    }

    private void showMatchesDialog() {
        BackendFacade backend = mainApp.getBackendFacade();
        List<Match> matches = backend.listUpcomingMatches(user);
        String content = matches.isEmpty()
                ? "No scheduled matches."
                : matches.stream()
                .map(this::formatMatch)
                .collect(Collectors.joining("\n"));
        showInfoDialog("Matches", "Upcoming fixtures", content);
    }

    private void showTrainingDialog() {
        BackendFacade backend = mainApp.getBackendFacade();
        List<TrainingSession> sessions = backend.listUpcomingSessions(user);
        String content = sessions.isEmpty()
                ? "No upcoming training sessions."
                : sessions.stream()
                .map(this::formatTraining)
                .collect(Collectors.joining("\n"));
        showInfoDialog("Training", "Upcoming sessions", content);
    }

    private VBox createRoleActions() {
        UserRole role = user.getRole();
        if (role == null) {
            return null;
        }
        return switch (role) {
            case ADMINISTRATOR -> createAdminActions();
            case COACH -> createCoachActions();
            case ANALYST -> createAnalystActions();
        };
    }

    private VBox createAdminActions() {
        Label header = new Label("Administrator actions");
        header.getStyleClass().add("section-subtitle");

        Button addPlayer = new Button("Register player");
        addPlayer.getStyleClass().add("primary-button");
        addPlayer.setOnAction(e -> showRegisterPlayerDialog());

        Button scheduleMatch = new Button("Schedule match");
        scheduleMatch.getStyleClass().add("primary-button");
        scheduleMatch.setOnAction(e -> showScheduleMatchDialog());

        VBox container = new VBox(10, header, addPlayer, scheduleMatch);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(12));
        container.getStyleClass().add("role-actions");
        return container;
    }

    private VBox createCoachActions() {
        Label header = new Label("Coach actions");
        header.getStyleClass().add("section-subtitle");

        Button createSession = new Button("Create training session");
        createSession.getStyleClass().add("primary-button");
        createSession.setOnAction(e -> showCreateTrainingSessionDialog());

        Button viewSessions = new Button("View my sessions");
        viewSessions.getStyleClass().add("primary-button");
        viewSessions.setOnAction(e -> showCoachSessionsDialog());

        VBox container = new VBox(10, header, createSession, viewSessions);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(12));
        container.getStyleClass().add("role-actions");
        return container;
    }

    private VBox createAnalystActions() {
        Label header = new Label("Analyst actions");
        header.getStyleClass().add("section-subtitle");

        Button logEvent = new Button("Log match event");
        logEvent.getStyleClass().add("primary-button");
        logEvent.setOnAction(e -> showLogMatchEventDialog());

        Button recordStats = new Button("Record player stats");
        recordStats.getStyleClass().add("primary-button");
        recordStats.setOnAction(e -> showRecordPlayerStatsDialog());

        Button viewTimeline = new Button("View match timeline");
        viewTimeline.getStyleClass().add("primary-button");
        viewTimeline.setOnAction(e -> showMatchTimelineDialog());

        VBox container = new VBox(10, header, logEvent, recordStats, viewTimeline);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(12));
        container.getStyleClass().add("role-actions");
        return container;
    }

    private void showRegisterPlayerDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Register player");
        dialog.setHeaderText("Add a new squad member");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> positionBox = new ComboBox<>(FXCollections.observableArrayList(PLAYER_POSITIONS));
        TextField shirtNumberField = new TextField();
        ComboBox<String> footBox = new ComboBox<>(FXCollections.observableArrayList("RIGHT", "LEFT", "BOTH"));
        footBox.setValue("RIGHT");

        grid.addRow(0, new Label("First name"), firstNameField);
        grid.addRow(1, new Label("Last name"), lastNameField);
        grid.addRow(2, new Label("Date of birth"), dobPicker);
        grid.addRow(3, new Label("Position"), positionBox);
        grid.addRow(4, new Label("Shirt number"), shirtNumberField);
        grid.addRow(5, new Label("Preferred foot"), footBox);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            StringJoiner errors = new StringJoiner("\n");
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            if (firstName.isEmpty()) {
                errors.add("First name is required.");
            }
            if (lastName.isEmpty()) {
                errors.add("Last name is required.");
            }
            ensureDateSelected(dobPicker, "Date of birth", errors);
            String position = positionBox.getValue();
            if (position == null || position.isBlank()) {
                errors.add("Position must be selected.");
            }
            Integer shirtNumber = parseInteger(shirtNumberField, "Shirt number", true, errors);
            if (shirtNumber != null && (shirtNumber < 1 || shirtNumber > 99)) {
                errors.add("Shirt number must be between 1 and 99.");
            }

            if (errors.length() > 0) {
                showAlert(AlertType.ERROR, "Player not registered", errors.toString());
                event.consume();
                return;
            }

            Player player = new Player();
            player.setFirstName(firstName);
            player.setLastName(lastName);
            player.setDateOfBirth(Date.valueOf(dobPicker.getValue()));
            player.setPosition(position);
            player.setShirtNumber(shirtNumber);
            player.setStatus("AVAILABLE");
            player.setOverallRating(70);
            player.setFitnessLevel(100);
            player.setPreferredFoot(footBox.getValue());
            player.setJoinedDate(Date.valueOf(LocalDate.now()));

            OperationResult<Player> result = mainApp.getBackendFacade().registerPlayer(user, player);
            if (!result.success()) {
                showAlert(AlertType.ERROR, "Player not registered", messageOrDefault(result, "Operation failed."));
                event.consume();
                return;
            }

            showAlert(AlertType.INFORMATION, "Player registered", messageOrDefault(result, "Player created."));
            Platform.runLater(mainApp::showHomeView);
        });

        dialog.showAndWait();
    }

    private void showScheduleMatchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Schedule match");
        dialog.setHeaderText("Create a new fixture");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm");
        TextField opponentField = new TextField();
        ComboBox<String> venueBox = new ComboBox<>(FXCollections.observableArrayList(VENUES));
        venueBox.getSelectionModel().selectFirst();
        TextField competitionField = new TextField();
        TextField attendanceField = new TextField();
        attendanceField.setPromptText("Optional");

        grid.addRow(0, new Label("Date"), datePicker);
        grid.addRow(1, new Label("Kick-off"), timeField);
        grid.addRow(2, new Label("Opponent"), opponentField);
        grid.addRow(3, new Label("Venue"), venueBox);
        grid.addRow(4, new Label("Competition"), competitionField);
        grid.addRow(5, new Label("Attendance"), attendanceField);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            StringJoiner errors = new StringJoiner("\n");
            ensureDateSelected(datePicker, "Match date", errors);
            LocalTime kickOff = parseTimeField(timeField, errors);
            String opponent = opponentField.getText().trim();
            if (opponent.isEmpty()) {
                errors.add("Opponent is required.");
            }
            String venue = venueBox.getValue();
            if (venue == null || venue.isBlank()) {
                errors.add("Venue must be selected.");
            }
            String competition = competitionField.getText().trim();
            if (competition.isEmpty()) {
                errors.add("Competition is required.");
            }
            Integer attendance = parseInteger(attendanceField, "Attendance", false, errors);

            if (errors.length() > 0) {
                showAlert(AlertType.ERROR, "Match not scheduled", errors.toString());
                event.consume();
                return;
            }

            LocalDate matchDate = datePicker.getValue();
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(matchDate, kickOff));

            Match match = new Match();
            match.setMatchDate(timestamp);
            match.setOpponent(opponent);
            match.setVenue(venue);
            match.setCompetition(competition);
            match.setGoalsFor(0);
            match.setGoalsAgainst(0);
            match.setMatchStatus("SCHEDULED");
            match.setAttendance(attendance);

            OperationResult<Match> result = mainApp.getBackendFacade().scheduleMatch(user, match);
            if (!result.success()) {
                showAlert(AlertType.ERROR, "Match not scheduled", messageOrDefault(result, "Operation failed."));
                event.consume();
                return;
            }

            showAlert(AlertType.INFORMATION, "Match scheduled", messageOrDefault(result, "Match created."));
            Platform.runLater(mainApp::showHomeView);
        });

        dialog.showAndWait();
    }

    private void showCreateTrainingSessionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create training session");
        dialog.setHeaderText("Plan a session for your squad");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm");
        TextField focusField = new TextField();
        TextField locationField = new TextField();
        TextField durationField = new TextField();
        durationField.setPromptText("Minutes");
        ComboBox<String> intensityBox = new ComboBox<>(FXCollections.observableArrayList(TRAINING_INTENSITIES));
        intensityBox.getSelectionModel().select("MEDIUM");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes (optional)");
        notesArea.setPrefRowCount(3);

        grid.addRow(0, new Label("Date"), datePicker);
        grid.addRow(1, new Label("Start time"), timeField);
        grid.addRow(2, new Label("Focus"), focusField);
        grid.addRow(3, new Label("Location"), locationField);
        grid.addRow(4, new Label("Duration"), durationField);
        grid.addRow(5, new Label("Intensity"), intensityBox);
        grid.addRow(6, new Label("Notes"), notesArea);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            StringJoiner errors = new StringJoiner("\n");
            ensureDateSelected(datePicker, "Session date", errors);
            LocalTime startTime = parseTimeField(timeField, errors);
            String focus = focusField.getText().trim();
            if (focus.isEmpty()) {
                errors.add("Focus is required.");
            }
            String location = locationField.getText().trim();
            if (location.isEmpty()) {
                errors.add("Location is required.");
            }
            Integer duration = parseInteger(durationField, "Duration", true, errors);
            if (duration != null && duration <= 0) {
                errors.add("Duration must be greater than zero.");
            }
            String intensity = intensityBox.getValue();
            if (intensity == null || intensity.isBlank()) {
                errors.add("Intensity must be selected.");
            }

            if (errors.length() > 0) {
                showAlert(AlertType.ERROR, "Session not created", errors.toString());
                event.consume();
                return;
            }

            TrainingSession session = new TrainingSession();
            session.setSessionDate(Timestamp.valueOf(LocalDateTime.of(datePicker.getValue(), startTime)));
            session.setFocus(focus);
            session.setLocation(location);
            session.setDurationMinutes(duration);
            session.setIntensity(intensity);
            String notes = notesArea.getText().trim();
            session.setNotes(notes.isEmpty() ? null : notes);

            OperationResult<TrainingSession> result = mainApp.getBackendFacade().createTrainingSession(user, session);
            if (!result.success()) {
                showAlert(AlertType.ERROR, "Session not created", messageOrDefault(result, "Operation failed."));
                event.consume();
                return;
            }

            showAlert(AlertType.INFORMATION, "Session created", messageOrDefault(result, "Training session created."));
            Platform.runLater(mainApp::showHomeView);
        });

        dialog.showAndWait();
    }

    private void showCoachSessionsDialog() {
        List<TrainingSession> sessions = mainApp.getBackendFacade().listCoachSessions(user);
        String content = sessions.isEmpty()
                ? "You have not scheduled any sessions yet."
                : sessions.stream()
                .map(this::formatSession)
                .collect(Collectors.joining("\n"));
        showInfoDialog("Training sessions", "Your schedule", content);
    }

    private void showLogMatchEventDialog() {
        List<Match> matches = mainApp.getBackendFacade().listAllMatches(user);
        if (matches.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No matches", "There are no matches available to log events.");
            return;
        }
        List<Player> players = mainApp.getBackendFacade().listPlayers(user);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Log match event");
        dialog.setHeaderText("Capture an important in-game moment");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        ComboBox<Match> matchBox = new ComboBox<>(FXCollections.observableArrayList(matches));
        configureMatchComboBox(matchBox);
        matchBox.getSelectionModel().selectFirst();

        ComboBox<Player> playerBox = new ComboBox<>(FXCollections.observableArrayList(players));
        configurePlayerComboBox(playerBox);
        playerBox.setPromptText("Optional");

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(MATCH_EVENT_TYPES));
        typeBox.getSelectionModel().selectFirst();

        TextField minuteField = new TextField();
        TextField secondField = new TextField();
        secondField.setPromptText("Optional");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setPrefRowCount(3);

        grid.addRow(0, new Label("Match"), matchBox);
        grid.addRow(1, new Label("Player"), playerBox);
        grid.addRow(2, new Label("Event type"), typeBox);
        grid.addRow(3, new Label("Minute"), minuteField);
        grid.addRow(4, new Label("Second"), secondField);
        grid.addRow(5, new Label("Description"), descriptionArea);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            StringJoiner errors = new StringJoiner("\n");
            Match selectedMatch = matchBox.getValue();
            if (selectedMatch == null) {
                errors.add("Match must be selected.");
            }
            String eventType = typeBox.getValue();
            if (eventType == null || eventType.isBlank()) {
                errors.add("Event type is required.");
            }
            Integer minute = parseInteger(minuteField, "Minute", true, errors);
            if (minute != null && (minute < 0 || minute > 130)) {
                errors.add("Minute must be between 0 and 130.");
            }
            Integer second = parseInteger(secondField, "Second", false, errors);
            if (second != null && (second < 0 || second > 59)) {
                errors.add("Second must be between 0 and 59.");
            }

            if (errors.length() > 0) {
                showAlert(AlertType.ERROR, "Event not logged", errors.toString());
                event.consume();
                return;
            }

            MatchEvent matchEvent = new MatchEvent();
            matchEvent.setMatchId(selectedMatch.getMatchId());
            Player selectedPlayer = playerBox.getValue();
            matchEvent.setPlayerId(selectedPlayer != null ? selectedPlayer.getPlayerId() : null);
            matchEvent.setEventType(eventType);
            matchEvent.setMinute(minute);
            matchEvent.setSecond(second != null ? second : 0);
            String description = descriptionArea.getText().trim();
            matchEvent.setDescription(description.isEmpty() ? null : description);
            matchEvent.setRecordedAt(new Timestamp(System.currentTimeMillis()));

            OperationResult<MatchEvent> result = mainApp.getBackendFacade().logMatchEvent(user, matchEvent);
            if (!result.success()) {
                showAlert(AlertType.ERROR, "Event not logged", messageOrDefault(result, "Operation failed."));
                event.consume();
                return;
            }

            showAlert(AlertType.INFORMATION, "Event logged", messageOrDefault(result, "Match event stored."));
        });

        dialog.showAndWait();
    }

    private void showRecordPlayerStatsDialog() {
        List<Match> matches = mainApp.getBackendFacade().listAllMatches(user);
        if (matches.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No matches", "There are no matches available for statistics.");
            return;
        }
        List<Player> players = mainApp.getBackendFacade().listPlayers(user);
        if (players.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No players", "There are no players available for statistics.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Record player performance");
        dialog.setHeaderText("Capture a player's contribution");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        ComboBox<Match> matchBox = new ComboBox<>(FXCollections.observableArrayList(matches));
        configureMatchComboBox(matchBox);
        matchBox.getSelectionModel().selectFirst();

        ComboBox<Player> playerBox = new ComboBox<>(FXCollections.observableArrayList(players));
        configurePlayerComboBox(playerBox);
        playerBox.getSelectionModel().selectFirst();

        TextField minutesField = new TextField();
        TextField goalsField = new TextField();
        TextField assistsField = new TextField();
        TextField ratingField = new TextField();
        ratingField.setPromptText("1.0 - 10.0");
        TextField shotsField = new TextField();
        TextField shotsOnTargetField = new TextField();
        TextField passesCompletedField = new TextField();
        TextField passesAttemptedField = new TextField();
        CheckBox starterCheck = new CheckBox("Started the match");

        grid.addRow(0, new Label("Match"), matchBox);
        grid.addRow(1, new Label("Player"), playerBox);
        grid.addRow(2, new Label("Minutes"), minutesField);
        grid.addRow(3, new Label("Goals"), goalsField);
        grid.addRow(4, new Label("Assists"), assistsField);
        grid.addRow(5, new Label("Rating"), ratingField);
        grid.addRow(6, new Label("Shots"), shotsField);
        grid.addRow(7, new Label("Shots on target"), shotsOnTargetField);
        grid.addRow(8, new Label("Passes completed"), passesCompletedField);
        grid.addRow(9, new Label("Passes attempted"), passesAttemptedField);
        grid.add(starterCheck, 1, 10);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            StringJoiner errors = new StringJoiner("\n");
            Match selectedMatch = matchBox.getValue();
            Player selectedPlayer = playerBox.getValue();
            if (selectedMatch == null) {
                errors.add("Match must be selected.");
            }
            if (selectedPlayer == null) {
                errors.add("Player must be selected.");
            }
            Integer minutes = parseInteger(minutesField, "Minutes", true, errors);
            if (minutes != null && (minutes < 0 || minutes > 120)) {
                errors.add("Minutes must be between 0 and 120.");
            }
            Integer goals = parseInteger(goalsField, "Goals", false, errors);
            Integer assists = parseInteger(assistsField, "Assists", false, errors);
            Double rating = parseDouble(ratingField, "Rating", true, errors);
            if (rating != null && (rating < 1.0 || rating > 10.0)) {
                errors.add("Rating must be between 1.0 and 10.0.");
            }
            Integer shots = parseInteger(shotsField, "Shots", false, errors);
            Integer shotsOnTarget = parseInteger(shotsOnTargetField, "Shots on target", false, errors);
            Integer passesCompleted = parseInteger(passesCompletedField, "Passes completed", false, errors);
            Integer passesAttempted = parseInteger(passesAttemptedField, "Passes attempted", false, errors);

            if (errors.length() > 0) {
                showAlert(AlertType.ERROR, "Stats not recorded", errors.toString());
                event.consume();
                return;
            }

            PlayerMatchStats stats = new PlayerMatchStats();
            stats.setMatchId(selectedMatch.getMatchId());
            stats.setPlayerId(selectedPlayer.getPlayerId());
            stats.setMinutesPlayed(minutes != null ? minutes : 0);
            stats.setGoals(goals != null ? goals : 0);
            stats.setAssists(assists != null ? assists : 0);
            stats.setRating(rating != null ? rating : 6.0);
            stats.setShots(shots != null ? shots : 0);
            stats.setShotsOnTarget(shotsOnTarget != null ? shotsOnTarget : 0);
            stats.setPassesCompleted(passesCompleted != null ? passesCompleted : 0);
            stats.setPassesAttempted(passesAttempted != null ? passesAttempted : 0);
            stats.setTackles(0);
            stats.setInterceptions(0);
            stats.setYellowCards(0);
            stats.setRedCards(0);
            stats.setFoulsCommitted(0);
            stats.setFoulsWon(0);
            stats.setWasStarter(starterCheck.isSelected());

            OperationResult<PlayerMatchStats> result = mainApp.getBackendFacade().recordPlayerMatchStats(user, stats);
            if (!result.success()) {
                showAlert(AlertType.ERROR, "Stats not recorded", messageOrDefault(result, "Operation failed."));
                event.consume();
                return;
            }

            showAlert(AlertType.INFORMATION, "Stats recorded", messageOrDefault(result, "Player performance stored."));
        });

        dialog.showAndWait();
    }

    private void showMatchTimelineDialog() {
        List<Match> matches = mainApp.getBackendFacade().listAllMatches(user);
        if (matches.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No matches", "There are no matches available to review.");
            return;
        }

        Dialog<Match> dialog = new Dialog<>();
        dialog.setTitle("Select match");
        dialog.setHeaderText("Choose a match to view its timeline");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Match> matchBox = new ComboBox<>(FXCollections.observableArrayList(matches));
        configureMatchComboBox(matchBox);
        matchBox.getSelectionModel().selectFirst();

        GridPane grid = createFormGrid();
        grid.addRow(0, new Label("Match"), matchBox);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> button == ButtonType.OK ? matchBox.getValue() : null);

        Optional<Match> selection = dialog.showAndWait();
        selection.ifPresent(match -> {
            List<MatchEvent> events = mainApp.getBackendFacade().getMatchTimeline(user, match.getMatchId());
            String content = events.isEmpty()
                    ? "No events have been logged for this match."
                    : events.stream()
                    .map(this::formatEvent)
                    .collect(Collectors.joining("\n"));
            showInfoDialog("Match timeline", formatMatch(match), content);
        });
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 0, 10));
        return grid;
    }

    private void configureMatchComboBox(ComboBox<Match> matchBox) {
        matchBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Match match) {
                return match == null ? "" : formatMatch(match);
            }

            @Override
            public Match fromString(String string) {
                return null;
            }
        });
        matchBox.setPrefWidth(320);
    }

    private void configurePlayerComboBox(ComboBox<Player> playerBox) {
        playerBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Player player) {
                if (player == null) {
                    return "";
                }
                return formatPlayer(player);
            }

            @Override
            public Player fromString(String string) {
                return null;
            }
        });
        playerBox.setPrefWidth(320);
    }

    private String formatSession(TrainingSession session) {
        if (session.getSessionDate() == null) {
            String detail = session.getFocus() != null ? session.getFocus() : "Training";
            return detail + " • " + (session.getLocation() != null ? session.getLocation() : "Location TBD");
        }
        String date = session.getSessionDate().toLocalDateTime().format(DATE_TIME_FORMATTER);
        String focus = session.getFocus() != null ? session.getFocus() : "Training";
        String location = session.getLocation() != null ? session.getLocation() : "Location TBD";
        return date + " • " + focus + " @ " + location;
    }

    private String formatEvent(MatchEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append(event.getMinute()).append("'");
        if (event.getSecond() > 0) {
            builder.append(String.format(Locale.getDefault(), "%02d", event.getSecond()));
        }
        builder.append(" ");
        String type = event.getEventType() != null ? event.getEventType() : "EVENT";
        builder.append(type);
        if (event.getPlayerId() != null) {
            builder.append(" - ").append(resolvePlayerName(event.getPlayerId()));
        }
        if (event.getDescription() != null && !event.getDescription().isBlank()) {
            builder.append(" | ").append(event.getDescription());
        }
        return builder.toString();
    }

    private String resolvePlayerName(Integer playerId) {
        if (playerId == null || playerId <= 0) {
            return "Team";
        }
        return mainApp.getBackendFacade().listPlayers(user).stream()
                .filter(player -> player.getPlayerId() == playerId)
                .map(player -> {
                    String fullName = (player.getFirstName() != null ? player.getFirstName() : "") + " " +
                            (player.getLastName() != null ? player.getLastName() : "");
                    String trimmed = fullName.trim();
                    return trimmed.isEmpty() ? "Player #" + playerId : trimmed;
                })
                .findFirst()
                .orElse("Player #" + playerId);
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private Integer parseInteger(TextField field, String fieldName, boolean required, StringJoiner errors) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            if (required) {
                errors.add(fieldName + " is required.");
            }
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            errors.add(fieldName + " must be a number.");
            return null;
        }
    }

    private Double parseDouble(TextField field, String fieldName, boolean required, StringJoiner errors) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            if (required) {
                errors.add(fieldName + " is required.");
            }
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            errors.add(fieldName + " must be a decimal number.");
            return null;
        }
    }

    private LocalTime parseTimeField(TextField field, StringJoiner errors) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            errors.add("Time is required.");
            return null;
        }
        try {
            return LocalTime.parse(text, TIME_FORMATTER);
        } catch (Exception ex) {
            errors.add("Time must follow HH:mm format.");
            return null;
        }
    }

    private boolean ensureDateSelected(DatePicker picker, String fieldName, StringJoiner errors) {
        if (picker.getValue() == null) {
            errors.add(fieldName + " is required.");
            return false;
        }
        return true;
    }

    private String messageOrDefault(OperationResult<?> result, String fallback) {
        String message = result.message();
        return (message == null || message.isBlank()) ? fallback : message;
    }

    private void showInfoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private String resolveDisplayName() {
        if (staff != null && staff.getFullName() != null && !staff.getFullName().isBlank()) {
            return staff.getFullName();
        }
        return user.getUsername();
    }

    private String formatPlayer(Player player) {
        String numberPart = player.getShirtNumber() > 0 ? "#" + player.getShirtNumber() + " " : "";
        String firstName = player.getFirstName() != null ? player.getFirstName() : "";
        String lastName = player.getLastName() != null ? player.getLastName() : "";
        String name = (firstName + " " + lastName).trim();
        if (name.isEmpty()) {
            name = "Player";
        }
        String position = player.getPosition() != null ? player.getPosition() : "-";
        String status = player.getStatus() != null ? player.getStatus() : "UNKNOWN";
        return numberPart + name + " (" + position + ") - " + status;
    }

    private String formatMatch(Match match) {
        String opponent = match.getOpponent() != null ? match.getOpponent() : "Opponent";
        if (match.getMatchDate() == null) {
            return opponent;
        }
        String date = match.getMatchDate().toLocalDateTime().format(DATE_TIME_FORMATTER);
        String competition = match.getCompetition() != null ? match.getCompetition() : "Competition";
        return date + " • vs " + opponent + " (" + competition + ")";
    }

    private String formatTraining(TrainingSession session) {
        if (session.getSessionDate() == null) {
            return session.getFocus() != null ? session.getFocus() : "Training";
        }
        String date = session.getSessionDate().toLocalDateTime().format(DATE_TIME_FORMATTER);
        String detail = session.getFocus() != null ? session.getFocus() : "Training";
        return date + " • " + detail;
    }
}


