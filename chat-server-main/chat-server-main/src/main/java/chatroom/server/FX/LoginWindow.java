package chatroom.server.FX;

// LoginWindow.java
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginWindow {

    private GridPane grid;

    public LoginWindow() {
        grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Create labels, text fields, and a button for the login window
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            // You can perform login validation here

            // For demonstration, let's just print the credentials
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            // Close the login window
            ((Stage) submitButton.getScene().getWindow()).close();
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

