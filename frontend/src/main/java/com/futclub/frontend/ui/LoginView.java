package com.futclub.frontend.ui;

import com.futclub.frontend.backend.LoginResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {

    private final MainApp mainApp;

    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public javafx.scene.Scene getScene() {
        VBox root = new VBox(20);
        root.setId("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("FutClub Manager");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Sign in to manage your club");
        subtitle.getStyleClass().add("subtitle");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-input");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Hyperlink registerLink = new Hyperlink("New here? Create an account");
        registerLink.getStyleClass().add("link");

        VBox form = new VBox(10,
                usernameField,
                passwordField,
                errorLabel,
                loginButton,
                registerLink
        );
        form.setMaxWidth(280);
        form.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, form);

        loginButton.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                ensureErrorLabel(errorLabel, "Please fill in both fields.");
                return;
            }

            LoginResult result = mainApp.getBackendFacade().login(user, pass);
            if (!result.success()) {
                ensureErrorLabel(errorLabel, result.message());
                return;
            }

            clearMessage(errorLabel);
            mainApp.handleLogin(result);
        });

        registerLink.setOnAction(e -> mainApp.showRegisterView());

        return new javafx.scene.Scene(root, 420, 520);
    }

    private void ensureErrorLabel(Label label, String message) {
        if (!label.getStyleClass().contains("error-label")) {
            label.getStyleClass().add("error-label");
        }
        label.setText(message);
    }

    private void clearMessage(Label label) {
        label.setText("");
    }
}
