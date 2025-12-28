package chatgpt;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatAppController {

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField inputField;

    @FXML
    public void initialize() {
        inputField.setOnAction(e -> handleUserInput(inputField.getText()));
    }

    private void handleUserInput(String userInput) {
        if (userInput.trim().isEmpty()) return;

        // Add user message to the chat
        addMessageToChat(userInput, Pos.CENTER_RIGHT, "#DCF8C6");

        // Process sentiment analysis
        String sentiment = analyzeSentiment(userInput);

        // Get ChatGPT response (dummy response for now)
        String response = getChatGPTResponse(userInput);

        // Add ChatGPT response to the chat
        addMessageToChat("Sentiment: " + sentiment + "\n" + response, Pos.CENTER_LEFT, "#FFFFFF");

        inputField.clear();

        // Scroll to the bottom
        scrollPane.setVvalue(1.0);
    }

    private void addMessageToChat(String message, Pos alignment, String backgroundColor) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(alignment);

        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.setPadding(new Insets(10));
        textFlow.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 10;");
        textFlow.setMaxWidth(300);

        messageBox.getChildren().add(textFlow);
        chatBox.getChildren().add(messageBox);
    }

    private String analyzeSentiment(String text) {
        // Dummy sentiment analysis (replace with actual NLP logic)
        if (text.contains("happy")) return "Happy";
        if (text.contains("sad")) return "Sad";
        if (text.contains("depressed")) return "Depressed";
        return "Neutral";
    }

    private String getChatGPTResponse(String userInput) {
        // Dummy response (replace with actual API call)
        return "This is a response from ChatGPT.";
    }
}
