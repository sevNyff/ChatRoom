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
    protected HBox topHBox, centerBox, newChatBox;
    protected VBox allUsersVBox, sendChatVBox, receiveChatVBox;
    protected Label serverAddressLabel, allUsersTitleLabel, sendToLabel;
    protected TextField serverAddressTextField, newChatTextField;
    protected Button serverAddressSetButton, loginWindowButton, logoutButton, newChatButton;


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
        newChatTextField = new TextField();
        newChatButton = new Button("+");
        newChatBox = new HBox();
        newChatBox.getChildren().addAll(newChatTextField, newChatButton);

        sendChatVBox.getChildren().addAll(sendToLabel, newChatBox);

        //Right part of the application
        receiveChatVBox = new VBox();
        //receiveChatButton = new Button("Reload");

        //Hier einfügen


        centerBox.getChildren().addAll(sendChatVBox, receiveChatVBox);
        pane.setCenter(centerBox);



        // Set action for the "Set Server" button
        serverAddressSetButton.setOnAction(event -> onSetServerClicked());
        loginWindowButton.setOnAction(event -> showLoginWindow());


        logoutButton.setOnAction(event -> onLogoutClicked());
        newChatButton.setOnAction(event -> setupNewChat());

        Scene scene = new Scene(pane, 800, 600);
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }

    private void setupNewChat() {
        Button button = new Button();
        String name = newChatTextField.getText();
        button.setText(name);

        sendChatVBox.getChildren().add(button);

        button.setOnAction(e -> {
            HBox sendBox = new HBox();
            Label receiverName = new Label(name);
            Button receiveChatButton = new Button("Reload");
            TextField messageTextField = new TextField();
            messageTextField.setPromptText("New message");
            Button sendChatButton = new Button("Send");
            sendBox.getChildren().addAll(messageTextField, sendChatButton);
            TextArea chatTextArea = new TextArea();
            chatTextArea.setEditable(false);
            receiveChatVBox.getChildren().clear();
            receiveChatVBox.getChildren().addAll(receiverName, receiveChatButton, chatTextArea, sendBox);

            sendChatButton.setOnAction(event -> {
                chatTextArea.appendText(onSendButtonClicked(name, messageTextField.getText()+ "\n"));
            });
            receiveChatButton.setOnAction(event -> onReceiveChatClicked());
            //receiver im model setzen mit namen vom button



        });


    }
    private void createMessageField(String name){
        HBox sendBox = new HBox();
        Label receiverName = new Label(name);
        TextField messageTextField = new TextField();
        messageTextField.setPromptText("New message");
        Button sendChatButton = new Button("Send");
        sendBox.getChildren().addAll(messageTextField, sendChatButton);
        TextArea chatTextArea = new TextArea();
        chatTextArea.setEditable(false);
        receiveChatVBox.getChildren().addAll(receiverName, chatTextArea, sendBox);
    }

    private void onReceiveChatClicked() {
        List<String> receivedMessages = model.pollMessages();
        Platform.runLater(() -> {
            for (String message : receivedMessages) {
                System.out.println("Received Message: " + message);
                //chatTextArea.appendText("From " + message + "\n");
            }
        });
        System.out.println("Received Messages: " + receivedMessages);
    }

    private String onSendButtonClicked(String name, String inputMessage) {
        String receiver = name;
        String message = inputMessage; //receiver vom model holen

        if (receiver.isEmpty() || message.isEmpty()) {
            showAlert("Receiver and message cannot be empty.");
            return null;
        }
        boolean messageSent = model.sendMessage(receiver, message);

        if (messageSent) {
            String myMessage = "To " + receiver + ": " + message;

            showAlertMessage("Message sent successfully!");
            return myMessage;
        } else {
            showAlert("Message sending failed.");
            return null;
        }


    }

    public void start() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void updateUsersList(List<String> userList) {
        Platform.runLater(() -> {
            allUsersVBox.getChildren().clear();
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
        model.logout();
        updateLogoutButton(false);
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

        loginStage.show();
        }
    public void onSuccessfulLogin(String token) {
        updateLogoutButton(true);
    }

}
