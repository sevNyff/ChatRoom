package chatroom.server.FX;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONException;

public class LoginWindow {
    private final Controller controller;
    private Label usernameLabel, passwordLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button registerButton, submitButton;
    private Stage registerStage;
    private RegisterWindow registerWindow;
    private GridPane grid;

    public LoginWindow(Controller controller) {
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

        registerButton = new Button("Register now");
        registerButton.getStyleClass().add("register-button");
        registerButton.setOnAction(event -> {
            registerStage = new Stage();
            registerStage.setTitle("Register Window");

            registerWindow = new RegisterWindow(this.controller);

            Scene scene = new Scene(registerWindow.getGrid(), 300, 150);
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            registerStage.setScene(scene);

            registerStage.show();
        });

        submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            try {
                String token = controller.loginAndGetToken(usernameField.getText(), passwordField.getText());
                if (token != null && !token.isEmpty()) {
                    System.out.println("Login successful. Token: " + token);
                    controller.setUserTokenFromLogin(token);

                    ((Stage) submitButton.getScene().getWindow()).close();
                    controller.deactivateLoginButton();
                } else {
                    System.out.println("Login failed. Please check your credentials.");
                }
            } catch (JSONException e){
                System.out.println("Login failed. Please check your credentials.");
                controller.showAlert("Login failed. Please check your credentials.");
            }
            controller.updateOnlineUsersList(controller.fetchOnlineUsersFromServer());
            ((Stage) submitButton.getScene().getWindow()).close();

        });


        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(registerButton, 1,2);
        grid.add(submitButton, 1, 3);


    }


    public GridPane getGrid() {return grid;}
}



