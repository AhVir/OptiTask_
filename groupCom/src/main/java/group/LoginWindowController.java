package group;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWindowController {
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;

    private Connection connection;

    private Stage currentStage;

    public void passStage(Stage stage) {
        this.currentStage = stage;
    }

    // Set the database connection (method to inject connection)
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Handle the login action
    @FXML
    private void handleLoginAction() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        // Validate the login credentials
        int userId = validateLogin(username, password);
        if (userId != -1) {
            navigateToGroupWindow(userId);  // Navigate to Group Window
        } else {
            showMessage("Invalid username or password.");
        }
    }

    // Validate user credentials and retrieve the user ID
    private int validateLogin(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            // If the user exists, return the user ID
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            System.out.println("Error while validating login in validateLogin method of LoginWindowController class.");
            e.printStackTrace();
        }
        return -1;  // Return -1 if the user does not exist or credentials are invalid
    }

    // Navigate to the Group Window after successful login
    private void navigateToGroupWindow(int userId) {
        try {
            // Load the GroupWindow FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupWindow.fxml"));
            Parent root = loader.load();  // Load the FXML file

            // Initialize the groupService
            GroupService groupService = new GroupService(connection); // Make sure to initialize groupService

            // Set the connection, user ID, and groupService using setter methods
            GroupWindowController controller = loader.getController();
            controller.setConnection(connection);
            controller.setCurrentUserId(userId);
            controller.setGroupService(groupService); // Pass groupService to the controller

            // Create and show the scene
            Scene groupWindowScene = new Scene(root, 1080, 600);
            currentStage.setScene(groupWindowScene);
            currentStage.setTitle("Groups");
            currentStage.show();
        } catch (Exception e) {
            System.out.println("Error while navigating to GroupWindowController class in navigateToGroupWindow method in LoginWindowController class.");
            e.printStackTrace();
        }
    }

    // Display a message to the user
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
