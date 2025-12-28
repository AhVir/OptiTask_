package group;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JoinGroupPopupController {

    @FXML
    private TextField groupNameField;
    @FXML
    private TextField groupJoinKeyField;
    @FXML
    private Label errorMessageLabel;

    private GroupService groupService;

    // Set the GroupService instance
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    // Handle the join group button click
    @FXML
    private void handleJoinGroup() {
        String groupName = groupNameField.getText();
        String groupJoinKey = groupJoinKeyField.getText();

        // Validate input
        if (groupName.isEmpty() || groupJoinKey.isEmpty()) {
            errorMessageLabel.setText("Group name and join key must not be empty.");
            return;
        }

        // Check if the group exists and the join key is correct
        if (groupService.isValidGroup(groupName, groupJoinKey)) {
            // Proceed to add user to the group
            int currentUserId = getCurrentUserId(); // Assume a method exists to fetch the current user's ID
            boolean success = groupService.addMemberToGroup(currentUserId, groupName, groupJoinKey);

            if (success) {
                errorMessageLabel.setText("You have successfully joined the group!");
                closeWindow();
            } else {
                errorMessageLabel.setText("Failed to join the group. Please try again.");
            }
        } else {
            errorMessageLabel.setText("Invalid group name or join key.");
        }
    }

    private int getCurrentUserId() {
        // Replace this with the actual logic to fetch the current logged-in user's ID
        return 1; // Placeholder
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) groupNameField.getScene().getWindow();
        stage.close(); // Close the popup
    }
}
