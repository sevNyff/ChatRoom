package chatroom.server.FX;

import chatroom.server.Server;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View {
    private Server server;
    private final Model model;
    private Stage stage;
    protected Label lblNumber;
    protected HBox topHBox;
    protected VBox allChatsVBox;
    protected Label serverAddressLabel, allChatsTitleLabel;
    protected TextField serverAddressTextField;
    protected Button serverAddressSetButton;



    public View(Stage stage, Model model) {
        this.model = model;
        this.stage = stage;
        stage.setTitle("Chat Room");

        BorderPane pane = new BorderPane();

        //Top Part of the Application
        topHBox = new HBox();
        serverAddressLabel = new Label("Server Address:");
        serverAddressTextField = new TextField("javaprojects.ch:50001");
        serverAddressSetButton = new Button("Set Server");
        topHBox.getChildren().addAll(serverAddressLabel, serverAddressTextField, serverAddressSetButton);
        pane.setTop(topHBox);

        //Left part of the application
        allChatsVBox = new VBox();
        allChatsTitleLabel = new Label("Chats");
        allChatsVBox.getChildren().addAll(allChatsTitleLabel);
        pane.setLeft(allChatsVBox);

        //Center part of the application
        lblNumber = new Label();
        lblNumber.setText("Hello");
        pane.setCenter(lblNumber);


        Scene scene = new Scene(pane, 800, 600);
        //scene.getStylesheets().add(getClass().getResource("ChatRoomMVC.css").toExternalForm());
        stage.setScene(scene);
    }

    public void start() {
        stage.show();
    }

    public Stage getStage(){
        return stage;
    }
}
