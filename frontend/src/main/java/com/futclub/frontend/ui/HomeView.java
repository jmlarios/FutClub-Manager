package com.futclub.frontend.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HomeView {

    private final MainApp mainApp;
    private final String username;

    public HomeView(MainApp mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;
    }

    public javafx.scene.Scene getScene() {
        BorderPane root = new BorderPane();
        root.setId("root");
        root.setPadding(new Insets(20));

        // Top bar
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Label welcome = new Label("Welcome, " + username);
        welcome.getStyleClass().add("welcome-label");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("secondary-button");

        topBar.getChildren().addAll(welcome, logoutButton);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));
        root.setTop(topBar);

        // Center section
        VBox center = new VBox(15);
        center.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Club Overview");
        title.getStyleClass().add("section-title");

        center.getChildren().add(title);
        root.setCenter(center);

        // Bottom quick actions
        HBox bottom = new HBox(15);
        bottom.setAlignment(Pos.CENTER);

        Button playersButton = new Button("Manage Players");
        playersButton.getStyleClass().add("primary-button");

        Button matchesButton = new Button("Manage Matches");
        matchesButton.getStyleClass().add("primary-button");

        Button trainingButton = new Button("Training & Attendance");
        trainingButton.getStyleClass().add("primary-button");

        bottom.getChildren().addAll(playersButton, matchesButton, trainingButton);
        BorderPane.setMargin(bottom, new Insets(20, 0, 0, 0));
        root.setBottom(bottom);

        logoutButton.setOnAction(e -> mainApp.showLoginView());

        playersButton.setOnAction(e -> System.out.println("Players screen TODO"));
        matchesButton.setOnAction(e -> System.out.println("Matches screen TODO"));
        trainingButton.setOnAction(e -> System.out.println("Training screen TODO"));

        return new javafx.scene.Scene(root, 800, 500);
    }
}


