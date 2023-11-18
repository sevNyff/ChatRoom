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
import java.util.HashMap;
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
    protected Button serverAddressSetButton, loginWindowButton, logoutButton, newChatButton, checknewMessage;
    protected HashMap<String, String> chats;


    public View(Stage stage, Model model) {
        this.model = model;
        this.stage = stage;
        stage.setTitle("Chat Room");
        chats = new HashMap<>();

        BorderPane pane = new BorderPane();

        //Top Part of the Application
        topHBox = new HBox();
        topHBox.getStyleClass().add("header-part");
        serverAddressLabel = new Label("Server Address:");
        serverAddressTextField = new TextField(model.getServerAddress() + ":" + model.getServerPort());
        serverAddressSetButton = new Button("Set Server");
        loginWindowButton = new Button(("Login"));
        logoutButton = new Button("Logout");
        logoutButton.setDisable(true);
        logoutButton.getStyleClass().add("logout-button");
        topHBox.getChildren().addAll(serverAddressLabel, serverAddressTextField, serverAddressSetButton,
                loginWindowButton);
        pane.setTop(topHBox);

        //Left part of the application
        allUsersVBox = new VBox();
        allUsersVBox.getStyleClass().add("allUsers-VBox");
        allUsersTitleLabel = new Label("Online Users");
        allUsersTitleLabel.getStyleClass().add("allUsers-label");
        allUsersVBox.getChildren().addAll(allUsersTitleLabel);
        pane.setLeft(allUsersVBox);

        //Center part of the application
        centerBox = new HBox();
        sendChatVBox = new VBox();
        sendToLabel = new Label("Send To:");
        newChatTextField = new TextField();
        newChatTextField.getStyleClass().add("newchat-tf");
        newChatButton = new Button("+");
        checknewMessage = new Button("Reload All");
        newChatBox = new HBox();
        newChatBox.getStyleClass().add("chats-VBox");
        newChatBox.getChildren().addAll(newChatTextField, newChatButton, checknewMessage);
        sendChatVBox.getChildren().addAll(sendToLabel, newChatBox);

        //Right part of the application
        receiveChatVBox = new VBox();
        centerBox.getChildren().addAll(sendChatVBox, receiveChatVBox);
        pane.setCenter(centerBox);

        Scene scene = new Scene(pane, 1200, 800);
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }

    public void start() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }


}
