package chatroom.server.FX;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class View {
    private final Model model;
    private Stage stage;
    private static final String cssStyles = "/Users/Kevin/Desktop/styles.css";
    protected HBox topHBox, centerBox;
    protected VBox allUsersVBox, sendChatVBox, receiveChatVBox;
    protected Label serverAddressLabel, allUsersTitleLabel, sendToLabel, messageLabel;
    protected TextField serverAddressTextField, sendToTextField, messageTextField;
    protected Button serverAddressSetButton, loginWindowButton, sendChatButton, receiveChatButton, logoutButton;

    private TextArea chatTextArea;

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
        logoutButton = new Button("Logout");
        logoutButton.setDisable(true);
        logoutButton.getStyleClass().add("logout-button");
        topHBox.getChildren().addAll(serverAddressLabel, serverAddressTextField, serverAddressSetButton,
                loginWindowButton, logoutButton);
        pane.setTop(topHBox);

        //Left part of the application
        allUsersVBox = new VBox();
        allUsersTitleLabel = new Label("Online Users");
        allUsersVBox.getChildren().addAll(allUsersTitleLabel);
        updateUsersList(model.fetchUsersFromServer());
        pane.setLeft(allUsersVBox);

        //Center part of the application
        centerBox = new HBox();
        sendChatVBox = new VBox();
        sendToLabel = new Label("Send To:");
        sendToTextField = new TextField();
        messageLabel = new Label("Message:");
        messageTextField = new TextField();
        sendChatButton = new Button("Send");
        sendChatVBox.getChildren().addAll(sendToLabel, sendToTextField, messageLabel, messageTextField, sendChatButton);
        receiveChatVBox = new VBox();
        receiveChatButton = new Button("New Messages");
        chatTextArea = new TextArea();
        receiveChatVBox.getChildren().addAll(receiveChatButton, chatTextArea);
        centerBox.getChildren().addAll(sendChatVBox, receiveChatVBox);
        pane.setCenter(centerBox);



        // Set action for the "Set Server" button
        serverAddressSetButton.setOnAction(event -> onSetServerClicked());
        loginWindowButton.setOnAction(event -> showLoginWindow());
        sendChatButton.setOnAction(event -> onSendButtonClicked());
        receiveChatButton.setOnAction(event -> onReceiveChatClicked());
        logoutButton.setOnAction(event -> onLogoutClicked());

        Scene scene = new Scene(pane, 800, 600);
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }

    private void onReceiveChatClicked() {
        List<String> receivedMessages = model.pollMessages();
        // Process receivedMessages as needed (e.g., display in the UI)
        // For example, you can add them to a TextArea or ListView in your UI.
        Platform.runLater(() -> {
            // Process receivedMessages as needed (e.g., display in the UI)
            // For example, you can add them to a TextArea or ListView in your UI.
            for (String message : receivedMessages) {
                System.out.println("Received Message: " + message);
                chatTextArea.appendText("From " + message + "\n"); // Append the message to the TextArea
            }
        });
        System.out.println("Received Messages: " + receivedMessages);
    }

    private void onSendButtonClicked() {
        String receiver = sendToTextField.getText();
        String message = messageTextField.getText();

        // Überprüfen Sie, ob Empfänger und Nachricht nicht leer sind
        if (receiver.isEmpty() || message.isEmpty()) {
            showAlert("Receiver and message cannot be empty.");
            return;
        }

        // Nachricht senden
        boolean messageSent = model.sendMessage(receiver, message);

        if (messageSent) {
            String myMessage = "To " + receiver + ": " + message;
            chatTextArea.appendText(myMessage + "\n");

            // Erfolgreiche Nachrichtenübermittlung: Erfolgsmeldung anzeigen
            showAlertMessage("Message sent successfully!");
        } else {
            // Nachrichtenübermittlung fehlgeschlagen: Fehlermeldung anzeigen
            showAlert("Message sending failed.");
        }

        // Clear the text fields after sending the message
        sendToTextField.clear();
        messageTextField.clear();
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
        updateUsersList(model.fetchUsersFromServer());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void onLogoutClicked() {
        // Perform logout actions
        // For example, clear the token or perform any other necessary cleanup
        model.logout(); // You need to implement the logout method in your Model class
        updateLogoutButton(false); // Disable the logout button
        showAlertMessage("You are logged out!");
    }

    private void showAlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    public void updateLogoutButton(boolean loggedIn) {
        Platform.runLater(() -> logoutButton.setDisable(!loggedIn));
    }

    private void showLoginWindow(){
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Window");

        LoginWindow loginWindow = new LoginWindow(this.model, this);

        Scene scene = new Scene(loginWindow.getGrid(), 300, 200);
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        loginStage.setScene(scene);

        // Show the login window
        loginStage.show();
        }
    public void onSuccessfulLogin(String token) {
        // Update the UI or perform actions when a successful login occurs
        // For example, enable the logout button and perform any other necessary tasks
        updateLogoutButton(true); // Enable the logout button
    }

}
