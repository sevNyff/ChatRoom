package chatroom.server.FX;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class ChatApp extends Application {
    private View view;
    private Controller controller;
    private Model model;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        model = new Model();
        view = new View(primaryStage, model);
        controller = new Controller(model, view);
        view.start();
    }
}
