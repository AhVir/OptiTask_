package chatgpt;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class ChatApp {
    private VBox chatBox;
    private TextField userInput;

    public VBox getUI() {
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));

        userInput = new TextField();
        userInput.setPromptText("Type your message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> handleUserInput());

        HBox inputArea = new HBox(5, userInput, sendButton);
        VBox layout = new VBox(10, chatBox, inputArea);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private void handleUserInput() {
        String userMessage = userInput.getText();
        if (userMessage.isEmpty()) return;

        displayMessage("You", userMessage);
        userInput.clear();

        // Call ChatGPT API and NLP analysis
        String response = ChatGPTAPI.getResponse(userMessage);
        displayMessage("GPT", response);

        String emotion = SentimentAnalyzer.analyze(userMessage);
        displayMessage("Emotion Analysis", "You seem " + emotion);
    }

    private void displayMessage(String sender, String message) {
        Text msg = new Text(sender + ": " + message);
        chatBox.getChildren().add(msg);
    }
}
