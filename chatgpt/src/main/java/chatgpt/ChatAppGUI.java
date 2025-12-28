package chatgpt;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

public class ChatAppGUI extends Application {

    private VBox chatBox;
    private TextField userInput;
    private static final String GPT_API_KEY = "your_openai_api_key";  // Replace with your actual key
    private static final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String NLP_API_URL = "http://localhost:5000/analyze";  // Replace if needed

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane root = new BorderPane();

        // Chat display area
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc;");
        ScrollPane chatScrollPane = new ScrollPane(chatBox);
        chatScrollPane.setFitToWidth(true);

        // Input area
        userInput = new TextField();
        userInput.setPromptText("Type your message...");
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(e -> handleUserInput());

        // Input layout
        HBox inputLayout = new HBox(10, userInput, sendButton);
        inputLayout.setPadding(new Insets(10));
        inputLayout.setStyle("-fx-background-color: #eee;");

        // Add components to root layout
        root.setCenter(chatScrollPane);
        root.setBottom(inputLayout);

        // Stage setup
        primaryStage.setTitle("ChatGPT with Emotion Analysis");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    private void handleUserInput() {
        String userMessage = userInput.getText();
        if (userMessage.isEmpty()) return;

        // Display user message in the chat
        displayMessage("You", userMessage, Color.BLUE);
        userInput.clear();

        // Call GPT and NLP APIs in a background thread
        new Thread(() -> {
            String gptResponse = getGPTResponse(userMessage);
            String emotion = getEmotionAnalysis(userMessage);

            // Update GUI on the JavaFX thread
            javafx.application.Platform.runLater(() -> {
                displayMessage("GPT", gptResponse, Color.GREEN);
                displayMessage("Emotion Analysis", "You seem " + emotion, Color.ORANGE);
            });
        }).start();
    }

    private String getGPTResponse(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();

            String jsonPayload = String.format("""
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [{"role": "user", "content": "%s"}]
                }
                """, prompt);

            RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(GPT_API_URL)
                    .header("Authorization", "Bearer " + GPT_API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            response.close();  // Close the response body to avoid resource leaks

            return new JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("content");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Unable to get GPT response.";
        }
    }

    private String getEmotionAnalysis(String message) {
        try {
            OkHttpClient client = new OkHttpClient();

            String jsonPayload = String.format("""
                {
                    "message": "%s"
                }
                """, message);

            RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(NLP_API_URL)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            response.close();  // Close the response body to avoid resource leaks

            return new JSONObject(responseBody).getString("emotion");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Unable to analyze emotion.";
        }
    }

    private void displayMessage(String sender, String message, Color color) {
        Text msg = new Text(sender + ": " + message);
        msg.setFont(Font.font(14));
        msg.setFill(color);
        chatBox.getChildren().add(msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

