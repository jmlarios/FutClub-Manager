package com.futclub.frontend.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
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

    public void showHomeView(String username) {
        HomeView view = new HomeView(this, username);
        javafx.scene.Scene scene = view.getScene();
        attachStylesheet(scene);
        primaryStage.setTitle("FutClub Manager - Dashboard");
        primaryStage.setScene(scene);
    }

    private void attachStylesheet(javafx.scene.Scene scene) {
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("styles.css not found – make sure it is in src/main/resources.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
