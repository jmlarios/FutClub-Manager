package com.futclub.frontend.ui;

import com.futclub.frontend.backend.BackendFacade;
import com.futclub.frontend.backend.LoginResult;
import com.futclub.model.Staff;
import com.futclub.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BackendFacade backendFacade;
    private User loggedInUser;
    private Staff loggedInStaff;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            this.backendFacade = new BackendFacade();
            showLoginView();
        } catch (IllegalStateException ex) {
            showStartupError(ex);
        }
    }

    public BackendFacade getBackendFacade() {
        return backendFacade;
    }

    public void showLoginView() {
        LoginView view = new LoginView(this);
        javafx.scene.Scene scene = view.getScene();
        attachStylesheet(scene);
        primaryStage.setTitle("FutClub Manager - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showRegisterView() {
        RegisterView view = new RegisterView(this);
        javafx.scene.Scene scene = view.getScene();
        attachStylesheet(scene);
        primaryStage.setTitle("FutClub Manager - Register");
        primaryStage.setScene(scene);
    }

    public void handleLogin(LoginResult result) {
        this.loggedInUser = result.user();
        this.loggedInStaff = result.staff();
        showHomeView();
    }

    public void showHomeView() {
        if (loggedInUser == null) {
            showLoginView();
            return;
        }
        HomeView view = new HomeView(this, loggedInUser, loggedInStaff);
        javafx.scene.Scene scene = view.getScene();
        attachStylesheet(scene);
        primaryStage.setTitle("FutClub Manager - Dashboard");
        primaryStage.setScene(scene);
    }

    public void logout() {
        this.loggedInUser = null;
        this.loggedInStaff = null;
        showLoginView();
    }

    User getLoggedInUser() {
        return loggedInUser;
    }

    Staff getLoggedInStaff() {
        return loggedInStaff;
    }

    private void attachStylesheet(javafx.scene.Scene scene) {
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("styles.css not found – make sure it is in src/main/resources.");
        }
    }

    private void showStartupError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Startup error");
        alert.setHeaderText("Unable to initialize the backend");
        alert.setContentText(throwable.getMessage());
        alert.showAndWait();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
