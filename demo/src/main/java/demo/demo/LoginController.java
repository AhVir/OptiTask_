package demo.demo;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private VBox loginPane;

    @FXML
    private VBox registerPane;

    // Handle Register Button Click
    @FXML
    private void handleRegisterClick() {
        loginPane.setVisible(false);  // Hide the login pane
        registerPane.setVisible(true); // Show the register pane
    }

    // Handle Back to Login Button Click
    @FXML
    private void handleBackToLoginClick() {
        registerPane.setVisible(false); // Hide the register pane
        loginPane.setVisible(true);     // Show the login pane
    }
}
