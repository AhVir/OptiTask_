package chatgpt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        ChatApp chatApp = new ChatApp();
        Scene scene = new Scene(chatApp.getUI(), 600, 400);
        primaryStage.setTitle("ChatGPT Sentiment App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
