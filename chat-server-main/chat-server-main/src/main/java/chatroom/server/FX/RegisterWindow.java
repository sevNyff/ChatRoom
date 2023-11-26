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
    private Label usernameLabel, passwordLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    Button submitButton;

    private GridPane grid;

    public RegisterWindow(Controller controller) {
        this.controller = controller;
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.getStyleClass().add("grid-pane");

        usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.getStyleClass().add("loginRegister-textfields");

        passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("loginRegister-textfields");

        submitButton = new Button("Submit");

        submitButton.setOnAction(event -> {
            boolean registrationSuccess = controller.registerUser(usernameField.getText(), passwordField.getText());

            if (registrationSuccess) {
                ((Stage) submitButton.getScene().getWindow()).close();
            } else {
                System.out.println("User registration failed.");
            }
            controller.updateAllUserList(controller.fetchAllUsersFromServer());
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

