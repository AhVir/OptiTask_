package group;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

public class Main extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection (replace with your actual method)
            connection = DatabaseConnection.getConnection();

            // Load the login screen FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));

            // Set the connection to the controller after loading the FXML
            Parent root = loader.load();  // Load the FXML file

            // Set the connection in the controller after it's loaded
            LoginWindowController controller = loader.getController();
            controller.setConnection(connection); // Set the connection
            controller.passStage(primaryStage);

            // Create a scene with the loaded root
            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");

            // Show the login window
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
