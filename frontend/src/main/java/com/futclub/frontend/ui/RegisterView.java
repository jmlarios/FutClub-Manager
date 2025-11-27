package com.futclub.frontend.ui;

import com.futclub.frontend.backend.RegistrationResult;
import com.futclub.model.enums.UserRole;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Locale;

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
                showError(errorLabel, "Please fill in all fields.");
                return;
            }
            if (!pass.equals(confirm)) {
                showError(errorLabel, "Passwords do not match.");
                return;
            }

            UserRole roleEnum;
            try {
                roleEnum = UserRole.valueOf(role.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                showError(errorLabel, "Unsupported role selected.");
                return;
            }

            RegistrationResult result = mainApp.getBackendFacade().register(name, user, pass, roleEnum);
            if (!result.success()) {
                showError(errorLabel, result.message());
                return;
            }

            clearMessage(errorLabel);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration successful");
            alert.setHeaderText("Welcome to FutClub Manager");
            alert.setContentText("Account created successfully. You can now log in.");
            alert.showAndWait();
            mainApp.showLoginView();
        });

        backButton.setOnAction(e -> mainApp.showLoginView());

        return new javafx.scene.Scene(root, 480, 560);
    }

    private void showError(Label label, String message) {
        if (!label.getStyleClass().contains("error-label")) {
            label.getStyleClass().add("error-label");
        }
        label.setText(message);
    }

    private void clearMessage(Label label) {
        label.setText("");
    }
}
