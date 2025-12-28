package group;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class GroupWindowController {
    @FXML private ListView<String> groupListView;
    @FXML private Label groupNameLabel;
    @FXML private TextArea chatArea;
    @FXML private TextField messageInputField;
    @FXML private Button sendMessageButton;
    @FXML private Button joinGroupButton;

    private int currentUserId;
    private int selectedGroupId;
    private Connection connection;
    private GroupService groupService;

    // For socket programming
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread listenerThread;

    // Constructor
    public GroupWindowController() throws SQLException {
        this.connection = DatabaseConnection.getConnection(); // Replace with your actual connection logic
        this.groupService = new GroupService(connection); // Pass the connection to GroupService
    }

    // Initialize the GroupService and User ID
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadUserGroups();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private void connectToServer(int groupId) {
        try {
            clientSocket = new Socket("localhost", 12345);
            System.out.println("Client connected to server on port 12345"); // Debugging line
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Send groupId and userId to the server
            writer.println(groupId + "," + currentUserId);

            listenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        final String receivedMessage = message;
                        System.out.println("Received message: " + receivedMessage); // Debugging line
                        Platform.runLater(() -> chatArea.appendText(receivedMessage + "\n"));
                    }
                } catch (IOException e) {
                    if (e instanceof SocketException) {
                        System.out.println("Socket closed");
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();
            System.out.println("Connected to server for group " + groupId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void joinGroupButtonClicked() {
        String groupName = groupListView.getSelectionModel().getSelectedItem();
        if (groupName != null) {
            // Ask user to enter the group join key
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Group Join Key");
            dialog.setHeaderText("Enter the join key for group: " + groupName);
            dialog.setContentText("Join Key:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(joinKey -> {
                if (groupService.isValidGroup(groupName, joinKey)) {
                    loadGroupChat(groupName);
                } else {
                    showError("Invalid join key");
                }
            });
        }
    }

    public void openCreateGroupWindow() {
        try {
            // Load the FXML for the Create Group window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateGroupPopup.fxml"));
            StackPane createGroupRoot = loader.load();

            // Create a new Stage (window) for Create Group
            Stage createGroupStage = new Stage();
            createGroupStage.setTitle("Create Group");

            // Set the scene for the new window
            Scene createGroupScene = new Scene(createGroupRoot);
            createGroupStage.setScene(createGroupScene);

            // Show the new window
            createGroupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening Create Group window: " + e.getMessage());
        }
    }

    private void loadUserGroups() {
        if (groupService != null) {
            // Fetch the groups from the database for the current user
            List<String> groups = groupService.listExistingGroupsForUser(currentUserId);
            groupListView.setItems(FXCollections.observableArrayList(groups));
        } else {
            System.out.println("Error: GroupService is not initialized.");
        }
    }

    public void sendMessageButtonClicked() {
        String message = messageInputField.getText();
        if (!message.isEmpty()) {
            writer.println(message);
            messageInputField.clear();
        }
    }

    private void loadGroupChat(String groupName) {
        this.selectedGroupId = groupService.getGroupIdByName(groupName);  // Make sure to implement this in GroupService
        chatArea.clear();
        connectToServer(selectedGroupId);
    }

    private void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
