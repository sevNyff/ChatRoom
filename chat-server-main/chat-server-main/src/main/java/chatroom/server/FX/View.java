package chatroom.server.FX;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class View {
    private final Model model;
    private Stage stage;
    protected Label lblNumber;
    protected HBox topHBox;
    protected VBox allUsersVBox;
    protected Label serverAddressLabel, allUsersTitleLabel;
    protected TextField serverAddressTextField;
    protected Button serverAddressSetButton, loginWindowButton;

    public View(Stage stage, Model model) {
        this.model = model;
        this.stage = stage;
        stage.setTitle("Chat Room");

        BorderPane pane = new BorderPane();

        //Top Part of the Application
        topHBox = new HBox();
        serverAddressLabel = new Label("Server Address:");
        serverAddressTextField = new TextField(model.getServerAddress() + ":" + model.getServerPort());
        serverAddressSetButton = new Button("Set Server");
        loginWindowButton = new Button(("Login"));
        topHBox.getChildren().addAll(serverAddressLabel, serverAddressTextField, serverAddressSetButton,
                loginWindowButton);
        pane.setTop(topHBox);

        //Left part of the application
        allUsersVBox = new VBox();
        allUsersTitleLabel = new Label("Online Users");
        allUsersVBox.getChildren().addAll(allUsersTitleLabel);
        updateUsersList(model.fetchUsersFromServer());
        pane.setLeft(allUsersVBox);

        //Center part of the application
        lblNumber = new Label();
        lblNumber.setText("Hello");
        pane.setCenter(lblNumber);

        // Set action for the "Set Server" button
        serverAddressSetButton.setOnAction(event -> onSetServerClicked());
        loginWindowButton.setOnAction(event -> showLoginWindow());

        Scene scene = new Scene(pane, 800, 600);
        stage.setScene(scene);
    }

    public void start() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void updateUsersList(List<String> userList) {
        Platform.runLater(() -> {
            allUsersVBox.getChildren().clear(); // Clear existing content

            // Add the updated user list
            allUsersVBox.getChildren().addAll(allUsersTitleLabel);
            for (String user : userList) {
                Label userLabel = new Label(user);
                allUsersVBox.getChildren().add(userLabel);
            }
        });
    }

    private void onSetServerClicked() {
        try {
            model.setServerPort(Integer.parseInt(serverAddressTextField.getText().split(":")[1]));
            if (model.getServerPort() > 0 && model.getServerPort() < 65536) {
                model.setServerAddress(serverAddressTextField.getText());
                model.pingServer(serverAddressTextField.getText(), model.getServerPort());
            } else {
                showAlert("Invalid port number!");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid port number!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void showLoginWindow(){
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Window");

        LoginWindow loginWindow = new LoginWindow();

        Scene scene = new Scene(loginWindow.getGrid(), 300, 200);
        loginStage.setScene(scene);

        // Show the login window
        loginStage.show();
        }

}
