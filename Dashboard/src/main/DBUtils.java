package dashboard;

import javafx.animation.FadeTransition;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DBUtils {

//    private static final String DB_URL = "jdbc:mysql://localhost:3306/login";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/groupfeature";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";


    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, boolean isLoggedIn) {
    Parent root = null;

    try {
        if (username != null) {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            // Set the logged user info if needed
            DashboardController dashboardController = loader.getController();
        } else {
            root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(root, 1080, 600);

        // Check if user is logged in to decide whether to use the transition
        if (!isLoggedIn) {
            // Apply Fade Transition
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> {
                // Change the scene after fade-out is complete
                stage.setTitle(title);
                stage.setScene(newScene);

                // Apply fade-in transition after scene change
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newScene.getRoot());
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            fadeOut.play();
        } else {
            // Directly change the scene if user is logged in (no fade effect)
            stage.setTitle(title);
            stage.setScene(newScene);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
