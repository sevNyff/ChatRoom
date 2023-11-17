package chatroom.server.FX;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatBox {
    private String name;

    public ChatBox(String name) {
        this.name = name;

        HBox sendBox = new HBox();
        Label receiverName = new Label(name);
        TextField messageTextField = new TextField();
        messageTextField.setPromptText("New message");
        Button sendChatButton = new Button("Send");
        sendBox.getChildren().addAll(messageTextField, sendChatButton);
        TextArea chatTextArea = new TextArea();
        chatTextArea.setEditable(false);
        VBox receiveChatVBox = new VBox();
        Button receiveChatButton = new Button("Reload");
        receiveChatVBox.getChildren().addAll(receiverName, receiveChatButton, chatTextArea, sendBox);

        sendChatButton.setOnAction(event -> onSendButtonClicked(name));

    }
    private void onSendButtonClicked(String name) {
        String receiver = name;
        String message = messageTextField.getText(); //receiver vom model holen

        if (receiver.isEmpty() || message.isEmpty()) {
            showAlert("Receiver and message cannot be empty.");
            return;
        }
        boolean messageSent = model.sendMessage(receiver, message);

        if (messageSent) {
            String myMessage = "To " + receiver + ": " + message;
            chatTextArea.appendText(myMessage + "\n");
            showAlertMessage("Message sent successfully!");
        } else {
            showAlert("Message sending failed.");
        }
        newChatTextField.clear();
        messageTextField.clear();
    }
}

