package chatroom.server.FX;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    protected BorderPane pane;
    protected HBox topHBox, centerBox, newChatBox, serverHBox;
    protected VBox onlineUsersVBox, sendChatVBox, receiveChatVBox, navigationVBox, allUsersVBox;
    protected Label serverAddressLabel, onlineUsersTitleLabel, sendToLabel, titleLabel, allUsersTitleLabel,
    welcomeLabel;
    protected TextField serverAddressTextField;
    protected Button serverAddressSetButton, loginWindowButton, logoutButton, newChatButton, checknewMessage,
                    pingServerTab, allUserTab, onlineUserTab, chatsTab;
    protected  ComboBox comboBox;


    public View(Stage stage, Model model) {
        this.model = model;
        this.stage = stage;
        stage.setTitle("Chat Room");

        pane = new BorderPane();

        //Top Part of the Application
        topHBox = new HBox();
        topHBox.getStyleClass().add("top-hbox");
        titleLabel = new Label("Chat App");
        titleLabel.getStyleClass().add("title-label");
        loginWindowButton = new Button(("Login"));
        loginWindowButton.getStyleClass().add("login-button");
        logoutButton = new Button("Logout");
        logoutButton.setDisable(true);
        logoutButton.getStyleClass().add("logout-button");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        topHBox.getChildren().addAll(titleLabel, space, loginWindowButton);
        pane.setTop(topHBox);

        //Left part of the application
        navigationVBox = new VBox();
        navigationVBox.getStyleClass().add("navigation-vbox");
        pingServerTab = new Button("Server");
        pingServerTab.getStyleClass().add("tab-buttons");
        allUserTab = new Button("All Users");
        allUserTab.getStyleClass().add("tab-buttons");
        onlineUserTab = new Button("Online Users");
        onlineUserTab.getStyleClass().add("tab-buttons");
        chatsTab = new Button("Chats");
        chatsTab.getStyleClass().add("tab-buttons");
        navigationVBox.getChildren().addAll(pingServerTab, allUserTab, onlineUserTab, chatsTab);
        pane.setLeft(navigationVBox);

        //Server Tab
        serverHBox = new HBox();
        serverHBox.getStyleClass().add("chat-boxes");
        serverAddressLabel = new Label("Server Address:");
        serverAddressTextField = new TextField(model.getServerAddress() + ":" + model.getServerPort());
        serverAddressSetButton = new Button("Set Server");
        serverHBox.getChildren().addAll(serverAddressLabel, serverAddressTextField, serverAddressSetButton);




        //Online Users Tab
        onlineUsersVBox = new VBox();
        onlineUsersVBox.getStyleClass().add("users-VBox");
        onlineUsersTitleLabel = new Label("Online Users");
        onlineUsersTitleLabel.getStyleClass().add("users-label");
        onlineUsersVBox.getChildren().addAll(onlineUsersTitleLabel);

        //Online Users Tab
        allUsersVBox = new VBox();
        allUsersVBox.getStyleClass().add("users-VBox");
        allUsersTitleLabel = new Label("All Users");
        allUsersTitleLabel.getStyleClass().add("users-label");
        allUsersVBox.getChildren().addAll(allUsersTitleLabel);


        //Chats Tab
        centerBox = new HBox();
        centerBox.getStyleClass().add("chat-boxes");
        sendChatVBox = new VBox();
        sendChatVBox.getStyleClass().add("chat-boxes");
        sendToLabel = new Label("Send To:");
        comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("combo-box");
        newChatButton = new Button("+");
        checknewMessage = new Button("Reload All");
        newChatBox = new HBox();
        newChatBox.getStyleClass().add("chats-VBox");
        newChatBox.getChildren().addAll(comboBox, newChatButton, checknewMessage);
        sendChatVBox.getChildren().addAll(sendToLabel, newChatBox);
        receiveChatVBox = new VBox(); //Gets Content from setupNewChat Method
        receiveChatVBox.getStyleClass().add("chat-boxes");
        centerBox.getChildren().addAll(sendChatVBox, receiveChatVBox);

        welcomeLabel = new Label("Welcome");
        pane.setCenter(welcomeLabel);

        //Setup Scene
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
