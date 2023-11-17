package chatroom.server.FX;

// LoginWindow.java

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterWindow {

    private final Controller controller;
    private GridPane grid;

    public RegisterWindow(Controller controller) {
        this.controller = controller;
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button submitButton = new Button("Submit");

        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            boolean registrationSuccess = controller.registerUser(username, password);

            if (registrationSuccess) {
                ((Stage) submitButton.getScene().getWindow()).close();
            } else {
                System.out.println("User registration failed.");
            }
        });
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(submitButton, 1, 2);


    }

    public GridPane getGrid() {
        return grid;
    }
}

