package chatroom.server.FX;


import chatroom.server.Server;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;

public class Controller {
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;

        view.serverAddressSetButton.setOnAction(event -> onSetServerClicked());

    }

    public void onSetServerClicked() {
        try {
            int port = Integer.parseInt(getPortNumber());
            String serverAddress = getServerAddress();

            // Print statements for debugging
            System.out.println("Server Address: " + serverAddress);
            System.out.println("Port: " + port);

            // Send ping to confirm connection
            if (model.pingServer(serverAddress, port)) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ping successful!", ButtonType.OK);
                    alert.showAndWait();
                });
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to ping the server!", ButtonType.OK);
                    alert.showAndWait();
                });
            }
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid port number!", ButtonType.OK);
                alert.showAndWait();
            });
        }
    }

    public String getPortNumber(){
        return view.serverAddressTextField.getText().split(":")[1];
    }
    public String getServerAddress(){return view.serverAddressTextField.getText().split(":")[0];}
}
