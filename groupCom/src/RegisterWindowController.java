package group;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterWindowController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    // Handle the registration action
    @FXML
    private void handleRegisterAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isUsernameAvailable(username)) {
            registerUser(username, password);
            showMessage("Registration Successful");
        } else {
            showMessage("Username already exists.");
        }
    }

    // Check if the username is available
    private boolean isUsernameAvailable(String username) {
        return UserService.isUsernameAvailable(username);
    }

    // Register a new user
    private void registerUser(String username, String password) {
        UserService.register(username, password);
    }

    // Display a message to the user
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
