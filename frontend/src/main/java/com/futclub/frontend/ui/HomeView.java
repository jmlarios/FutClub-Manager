package com.futclub.frontend.ui;

import com.futclub.frontend.backend.BackendFacade;
import com.futclub.frontend.backend.DashboardSnapshot;
import com.futclub.model.Match;
import com.futclub.model.Player;
import com.futclub.model.Staff;
import com.futclub.model.TrainingSession;
import com.futclub.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeView {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.getDefault());

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

        center.getChildren().addAll(title, statsRow, staffSummary);
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


