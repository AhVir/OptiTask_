package group;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class DashboardWindowController {

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        try {
            // Get the current stage and close it
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Load the login window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 300);
            Stage loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to navigate to the Group UI
    @FXML
    private void openGroupWindow(ActionEvent event) {
        try {
            // Load Group UI (GroupWindow.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupWindow.fxml"));
            Scene groupScene = new Scene(loader.load(), 600, 400);  // Adjust dimensions as needed
            Stage groupStage = new Stage();
            groupStage.setScene(groupScene);
            groupStage.setTitle("Group Management");
            groupStage.show();

            // Optionally, you can hide the dashboard window after opening the group window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
