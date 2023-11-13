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

    private final Model model;
    private GridPane grid;

    public RegisterWindow(Model model) {
        this.model = model;
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Create labels, text fields, and a button for the login window
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // You can perform login validation here

            // For demonstration, let's just print the credentials
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            boolean registrationSuccess = model.registerUser(username, password);

            if (registrationSuccess) {
                // Close the registration window or navigate to the next screen
                ((Stage) submitButton.getScene().getWindow()).close();
            } else {
                System.out.println("User registration failed.");
                // Handle registration failure (display an error message, etc.)
            }
        });

        // Add components to the GridPane
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

