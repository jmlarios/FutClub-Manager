package com.futclub.frontend.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterView {

    private final MainApp mainApp;

    public RegisterView(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public javafx.scene.Scene getScene() {
        VBox root = new VBox(20);
        root.setId("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("Create Account");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Register as coach, analyst or admin");
        subtitle.getStyleClass().add("subtitle");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full name");
        fullNameField.getStyleClass().add("text-input");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-input");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        confirmField.getStyleClass().add("text-input");

        ComboBox<String> roleBox = new ComboBox<>(
                FXCollections.observableArrayList("Coach", "Analyst", "Administrator")
        );
        roleBox.setPromptText("Select role");
        roleBox.getStyleClass().add("combo-input");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button registerButton = new Button("Create account");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        Button backButton = new Button("Back to login");
        backButton.getStyleClass().add("secondary-button");

        HBox actions = new HBox(10, backButton, registerButton);
        actions.setAlignment(Pos.CENTER);
        actions.setMaxWidth(280);

        VBox form = new VBox(10,
                fullNameField,
                usernameField,
                passwordField,
                confirmField,
                roleBox,
                errorLabel,
                actions
        );
        form.setMaxWidth(320);
        form.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, form);

        registerButton.setOnAction(e -> {
            String name = fullNameField.getText().trim();
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            String confirm = confirmField.getText();
            String role = roleBox.getValue();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || confirm.isEmpty() || role == null) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }
            if (!pass.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }

            // TODO: save user via backend
            errorLabel.setText("");
            mainApp.showLoginView();
        });

        backButton.setOnAction(e -> mainApp.showLoginView());

        return new javafx.scene.Scene(root, 480, 560);
    }
}
