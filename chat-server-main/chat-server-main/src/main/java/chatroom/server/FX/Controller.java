package chatroom.server.FX;


import chatroom.server.Server;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        updateOnlineUsersList(fetchOnlineUsersFromServer());

        view.pingServerTab.setOnAction(event -> {
            view.pane.setCenter(null);
            view.pane.setCenter(view.serverHBox);
        });
        view.allUserTab.setOnAction(event -> {
            view.pane.setCenter(null);
            view.pane.setCenter(view.allUsersVBox);
        });
        view.onlineUserTab.setOnAction(event -> {
            view.pane.setCenter(null);
            view.pane.setCenter(view.onlineUsersVBox);
        });
        view.chatsTab.setOnAction(event -> {
            view.pane.setCenter(null);
            view.pane.setCenter(view.centerBox);
        });

        view.serverAddressSetButton.setOnAction(event -> onSetServerClicked());
        // Set action for the buttons
        view.loginWindowButton.setOnAction(event -> showLoginWindow());
        view.logoutButton.setOnAction(event -> {
            onLogoutClicked();
            view.topHBox.getChildren().remove(view.logoutButton);
            view.topHBox.getChildren().add(view.loginWindowButton);
        });

        view.newChatButton.setOnAction(event -> {
            String name = view.newChatTextField.getText();
            setupNewChat(name);
            view.newChatTextField.clear();
        });
        view.checknewMessage.setOnAction(event -> createFromMessage());
    }

    public void onSetServerClicked() {
        try {
            if (getPortNumberFromTextField() > 0 && getPortNumberFromTextField() < 65536) {
                model.setServerPort(getPortNumberFromTextField());
                model.setServerAddress(getServerAddressFromTextField());
                pingServer(model.getServerAddress(), model.getServerPort());
                try {
                    int port = model.getServerPort();
                    String serverAddress = model.getServerAddress();
                    System.out.println("Server Address: " + serverAddress);
                    System.out.println("Port: " + port);

                    if (pingServer(serverAddress, port)) {
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
            } else {
                showAlert("Invalid port number!");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid port number!");
        }
        updateOnlineUsersList(fetchOnlineUsersFromServer());

    }
    public int getPortNumberFromTextField(){
        return Integer.parseInt(view.serverAddressTextField.getText().split(":")[1]);
    }
    public String getServerAddressFromTextField(){return view.serverAddressTextField.getText().split(":")[0];}

    private void setupNewChat(String name) {
        Button button = new Button();
        //String name = view.newChatTextField.getText();
        button.setText(name);
        button.getStyleClass().add("chat-button");
        view.chats.put(name, "");

        view.sendChatVBox.getChildren().add(button);

        button.setOnAction(e -> {setupChatWindow(name);});



    }
    private void setupChatWindow(String name){
            HBox sendBox = new HBox();
            Label receiverName = new Label(name);
            Button receiveChatButton = new Button("Reload");
            TextField messageTextField = new TextField();
            messageTextField.setPromptText("New message");
            messageTextField.getStyleClass().add("message-tf");

            Image image = new Image("/img.png");
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(15);
            imageView.setFitWidth(15);
            Button sendChatButton = new Button();
            sendChatButton.setGraphic(imageView);
            sendChatButton.getStyleClass().add("sendChat-button");
            sendBox.getChildren().addAll(messageTextField, sendChatButton);
            TextArea chatTextArea = new TextArea(view.chats.get(name));
            chatTextArea.getStyleClass().add("chat-ta");
            chatTextArea.setEditable(false);
            view.receiveChatVBox.getChildren().clear();
            view.receiveChatVBox.getChildren().addAll(receiverName, receiveChatButton, chatTextArea, sendBox);

            sendChatButton.setOnAction(event -> {
                chatTextArea.appendText(onSendButtonClicked(name, messageTextField.getText()+ "\n"));
                view.chats.replace(name, chatTextArea.getText());
                messageTextField.clear();
            });
            receiveChatButton.setOnAction(event -> {

                List<String> receivedMessages = pollMessages();
                Platform.runLater(() -> {
                    for (String message : receivedMessages) {
                        System.out.println("Received Message: " + message);
                        chatTextArea.appendText("From " + message + "\n");
                        view.chats.replace(name, chatTextArea.getText());
                    }
                });
                System.out.println("Received Messages: " + receivedMessages);
            });
    }


    private void createFromMessage() {
        List<String> receivedMessages = pollMessages();
        System.out.println(receivedMessages);
        if (receivedMessages.isEmpty()){
            showAlertMessage("No new Messages");
        } else {
            Platform.runLater(() -> {
                for (String message : receivedMessages) {
                    System.out.println("Received Message: " + message);
                    String[] msg = message.split(":");
                    String name = msg[0];
                    Button button = new Button();
                    button.getStyleClass().add("chat-button");
                    button.setText(name);
                    view.chats.put(name, "");

                    view.sendChatVBox.getChildren().add(button);

                    button.setOnAction(e -> {setupChatWindow(name);
                    });
                    //break;
                }
            });

        }
    }

    private String onSendButtonClicked(String name, String inputMessage) {
        String receiver = name;
        String message = inputMessage;

        if (receiver.isEmpty() || message.isEmpty()) {
            showAlert("Receiver and message cannot be empty.");
            return null;
        }
        boolean messageSent = sendMessage(receiver, message);

        if (messageSent) {
            String myMessage = "To " + receiver + ": " + message;
            return myMessage;
        } else {
            showAlert("Message sending failed.");
            return null;
        }


    }

    public void updateOnlineUsersList(List<String> userList) {
        Platform.runLater(() -> {
            view.onlineUsersVBox.getChildren().clear();
            view.onlineUsersVBox.getChildren().addAll(view.onlineUsersTitleLabel);
            for (String user : userList) {
                Label userLabel = new Label(user);
                view.onlineUsersVBox.getChildren().add(userLabel);
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void onLogoutClicked() {
        logout();
        updateLogoutButton(false);
        showAlertMessage("You are logged out!");
        updateOnlineUsersList(fetchOnlineUsersFromServer());
    }

    private void showAlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    public void updateLogoutButton(boolean loggedIn) {
        Platform.runLater(() -> view.logoutButton.setDisable(!loggedIn));
    }

    private void showLoginWindow(){
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Window");

        LoginWindow loginWindow = new LoginWindow(this, this.view);

        Scene scene = new Scene(loginWindow.getGrid(), 300, 200);
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        loginStage.setScene(scene);

        loginStage.show();
    }
    public void onSuccessfulLogin(String token) {updateLogoutButton(true);
    }

    public boolean isLoggedIn() {
        System.out.println(model.getUserToken());
        return model.getUserToken() != null && !model.getUserToken().isEmpty();
    }

    /* Wir haben bei sämtlichen Server Anfragen den Code und die Dokumentation von Digital Ocean und StackOverflow genommen:
        https://www.digitalocean.com/community/tutorials/java-httpurlconnection-example-java-http-request-get-post
     */
    public boolean pingServer(String serverAddress, int port) {
        try {
            URL url = new URL("http://" + serverAddress + ":" + port + "/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(1000);

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> fetchOnlineUsersFromServer() {
        String serverEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/users/online";

        try {
            URL url = new URL(serverEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseOnlineUserList(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> parseOnlineUserList(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray onlineUsers = json.getJSONArray("online");

            List<String> userList = new ArrayList<>();
            for (int i = 0; i < onlineUsers.length(); i++) {
                userList.add(onlineUsers.getString(i));
            }
            return userList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String loginAndGetToken(String username, String password) {
        String loginEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/user/login";

        try {
            URL url = new URL(loginEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
            connection.setDoOutput(true);

            connection.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String token = extractTokenFromResponse(connection);
                connection.disconnect();
                model.setUserToken(token);
                return token;
            } else {
                System.out.println("Login failed. HTTP Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractTokenFromResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        JSONObject json = new JSONObject(response.toString());
        return json.getString("token");
    }

    public boolean registerUser(String username, String password) {
        String registerEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/user/register";

        try {
            URL url = new URL(registerEndpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            System.out.println("Response code Register: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("User registered successfully!");
                return true;
            } else {
                System.out.println("User registration failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void logout() {
        String serverLogoutEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/user/logout";
        if (isLoggedIn()) {
            try {
                URL url = new URL(serverLogoutEndpoint);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                String jsonInputString = "{\"token\": \"" + model.getUserToken() + "\"}";
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                System.out.println("Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Logout successful!");
                } else {
                    System.out.println("Logout failed. HTTP Response Code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Logout failed due to an exception: " + e.getMessage());
            } finally {
                model.setUserToken(null);
            }
        }
    }
    public boolean sendMessage(String receiver, String message) {
        try {
            String token = model.getUserToken();
            if (token == null || token.isEmpty()) {
                System.out.println("You need to log in first.");
                return false;
            }
            String sendMessageEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/chat/send";
            String jsonInputString = String.format("{\"token\": \"%s\", \"username\": \"%s\", \"message\": \"%s\"}",
                    token, receiver, message);

            URL url = new URL(sendMessageEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode); //For debugging

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
                return true;
            } else {
                System.out.println("Message sending failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Message sending failed due to an exception: " + e.getMessage());
            return false;
        }
    }
    public List<String> pollMessages() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }

        try {
            String pollEndpoint = "http://" + model.getServerAddress() + ":" + model.getServerPort() + "/chat/poll";
            URL url = new URL(pollEndpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"token\": \"" + model.getUserToken() + "\"}";

            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseMessageList(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> parseMessageList(String response) {
        try {
            JSONObject json = new JSONObject(response);

            if (json.has("messages")) {
                JSONArray messages = json.getJSONArray("messages");

                List<String> messageList = new ArrayList<>();
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject messageObject = messages.getJSONObject(i);
                    String username = messageObject.getString("username");
                    String message = messageObject.getString("message");
                    messageList.add(username + ": " + message);
                }
                return messageList;
            } else {
                System.out.println("No messages array in the response");
                return new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }




}
