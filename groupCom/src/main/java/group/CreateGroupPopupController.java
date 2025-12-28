package group;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateGroupPopupController {

    @FXML
    private TextField groupNameField;
    @FXML
    private TextArea groupDescriptionField;
    @FXML
    private Label statusLabel;

    private int ownerId;
    private GroupService groupService;

    // Set the ownerId and GroupService instance
    public void setOwnerId(int ownerId, GroupService groupService) {
        this.ownerId = ownerId;
        this.groupService = groupService;
    }

    @FXML
    private void handleCreateGroup() {
        String groupName = groupNameField.getText();
        String groupDescription = groupDescriptionField.getText();

        // Validate input fields
        if (groupName.isEmpty() || groupDescription.isEmpty()) {
            statusLabel.setText("Group name and description cannot be empty.");
            return;
        }

        // Check if the group already exists
        if (groupService.groupExists(groupName)) {
            statusLabel.setText("Group already exists!");
        } else {
            // Create the group and get the join key
            String joinKey = groupService.createGroup(groupName, groupDescription, ownerId);

            if (joinKey != null) {
                // If group creation is successful, display the join key
                statusLabel.setText("Group created successfully! Join Key: " + joinKey);
            } else {
                statusLabel.setText("An error occurred while creating the group.");
            }
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) groupNameField.getScene().getWindow();
        stage.close(); // Close the Create Group window
    }
}
