package chatroom.server.FX;

// LoginWindow.java
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import org.json.JSONException;

public class LoginWindow {

    private final Model model;
    private final View view;
    private String token;

    private GridPane grid;

    public LoginWindow(Model model, View view) {
        this.model = model;
        this.view = view;

        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button registerButton = new Button("Register now");
        registerButton.setOnAction(event -> {
            Stage registerStage = new Stage();
            registerStage.setTitle("Register Window");

            RegisterWindow registerWindow = new RegisterWindow(this.model);

            Scene scene = new Scene(registerWindow.getGrid(), 300, 150);
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            registerStage.setScene(scene);

            registerStage.show();
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            try {
                String token = model.loginAndGetToken(username, password);
                if (token != null && !token.isEmpty()) {
                    System.out.println("Login successful. Token: " + token);
                    setToken(token);
                    onSuccessfulLogin(token);
                    ((Stage) submitButton.getScene().getWindow()).close();
                } else {
                    System.out.println("Login failed. Please check your credentials.");
                }
            } catch (JSONException e){
                System.out.println("Login failed. Please check your credentials.");
                showAlert("Login failed. Please check your credentials.");
            }
            ((Stage) submitButton.getScene().getWindow()).close();
        });


        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(registerButton, 1,2);
        grid.add(submitButton, 1, 3);


    }

    private void onSuccessfulLogin(String token) {
        view.onSuccessfulLogin(token);
    }
    public GridPane getGrid() {
        return grid;
    }

    private void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}



