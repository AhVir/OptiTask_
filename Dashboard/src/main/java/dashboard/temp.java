/*
package dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class temp implements Initializable {
    @FXML
    private Label monthLabel;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private AnchorPane sidebar;
    private boolean isSidebarVisible = true;
    private Connection connection;
    private YearMonth currentYearMonth;

    private Map<LocalDate, String> taskStatuses = new HashMap<>();

    @FXML
    private Button addTaskButton;

    @FXML
    public void showAddTaskDialog() {
        // Create a dialog window for adding a new task
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Please enter the details of the new task:");

        // Create a grid pane for task inputs
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        // Task Name Label and TextField
        Label taskNameLabel = new Label("Task Name:");
        TextField taskNameField = new TextField();
        grid.add(taskNameLabel, 0, 0);
        grid.add(taskNameField, 1, 0);

        // Duration Label and TextField
        Label durationLabel = new Label("Duration (hours):");
        TextField durationField = new TextField();
        grid.add(durationLabel, 0, 1);
        grid.add(durationField, 1, 1);

        // Breakable Task Checkbox
        CheckBox breakableCheckbox = new CheckBox("Breakable Task");
        grid.add(breakableCheckbox, 0, 2, 2, 1); // Span across two columns

        // Task Type Radio Buttons (Mandatory or Optional)
        ToggleGroup taskTypeGroup = new ToggleGroup();
        RadioButton mandatoryRadioButton = new RadioButton("Mandatory");
        mandatoryRadioButton.setToggleGroup(taskTypeGroup);
        mandatoryRadioButton.setSelected(true); // Default option
        RadioButton optionalRadioButton = new RadioButton("Optional");
        optionalRadioButton.setToggleGroup(taskTypeGroup);
        grid.add(mandatoryRadioButton, 0, 3);
        grid.add(optionalRadioButton, 1, 3);

        // Add grid to the dialog
        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("Add Task", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Set result converter to handle user input
        dialog.setResultConverter(button -> {
            if (button == okButtonType) {
                String taskName = taskNameField.getText();
                double duration = Double.parseDouble(durationField.getText());
                boolean isBreakable = breakableCheckbox.isSelected();
                String taskType = mandatoryRadioButton.isSelected() ? "Mandatory" : "Optional";
                addNewTask(taskName, duration, isBreakable, taskType); // Call addNewTask with the inputs
            }
            return null;
        });

        // Show the dialog
        dialog.showAndWait();
    }

    @FXML
    public void addNewTask(String taskName, double duration, boolean isBreakable, String taskType) {
        // Step 1: Input validation
        if (taskName.isEmpty() || duration <= 0) {
            showAlert("Invalid input", "Please provide a valid task name and duration.");
            return;
        }

        // Step 2: Retrieve existing tasks from Preplan and CalendarTasks
        List<Task> existingPreplanTasks = getExistingPreplanTasks(currentUserId);
        List<Task> existingCalendarTasks = getExistingCalendarTasks(currentUserId);

        // Step 3: Check for available time slots for the new task
        List<TimeSlot> availableSlots = getAvailableTimeSlots(existingPreplanTasks, existingCalendarTasks, duration);

        if (availableSlots.isEmpty()) {
            showAlert("No available slot", "No available time slot for this task.");
            return;
        }

        // Step 4: Handle conflicts based on task type
        if (taskType.equals("Mandatory")) {
            // For mandatory tasks, find the best slot or reject if no suitable slot is found
            TimeSlot bestSlot = findBestSlotForMandatoryTask(availableSlots);
            if (bestSlot != null) {
                scheduleTask(bestSlot, taskName, duration, isBreakable);
            } else {
                showAlert("Conflict", "Unable to schedule mandatory task due to conflicts.");
            }
        } else {
            // For optional tasks, pick the first available slot
            TimeSlot bestSlot = availableSlots.get(0);
            scheduleTask(bestSlot, taskName, duration, isBreakable);
        }

        // Step 5: Update the calendar and database
        loadCalendar(); // Refresh the calendar UI
    }

    private List<Task> getExistingPreplanTasks(int userId) {
        String query = "SELECT * FROM Preplan WHERE user_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                tasks.add(new Task(startTime, endTime, rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private List<Task> getExistingCalendarTasks(int userId) {
        String query = "SELECT * FROM CalendarTasks WHERE user_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime taskDate = rs.getTimestamp("task_date").toLocalDateTime();
                tasks.add(new Task(taskDate, taskDate.plusHours(1), rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private List<TimeSlot> getAvailableTimeSlots(List<Task> preplanTasks, List<Task> calendarTasks, double taskDuration) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        // Implement logic to find available time slots based on taskDuration
        // Check preplanTasks and calendarTasks to find gaps
        return availableSlots;
    }

    private TimeSlot findBestSlotForMandatoryTask(List<TimeSlot> availableSlots) {
        // Find the best time slot for mandatory tasks (e.g., prioritize morning slots)
        return availableSlots.isEmpty() ? null : availableSlots.get(0);
    }

    private void scheduleTask(TimeSlot slot, String taskName, double duration, boolean isBreakable) {
        // Schedule the task in the selected time slot
        LocalDateTime startTime = slot.getStartTime();
        LocalDateTime endTime = startTime.plusHours((long) duration);

        // Insert the task into the database (Preplan table)
        String insertQuery = "INSERT INTO Preplan (user_id, task_name, task_type, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, currentUserId);
            stmt.setString(2, taskName);
            stmt.setString(3, isBreakable ? "Optional" : "Mandatory");
            stmt.setTimestamp(4, Timestamp.valueOf(startTime));
            stmt.setTimestamp(5, Timestamp.valueOf(endTime));
            stmt.setString(6, "Pending");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        // Display an alert to the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = connectDatabase();
        if (connection == null) {
            System.err.println("Database connection failed!");
            return;
        }

        currentYearMonth = YearMonth.now();
        loadTaskStatuses();
        loadCalendar();

        previousButton.setOnAction(e -> changeMonth(-1));
        nextButton.setOnAction(e -> changeMonth(1));
    }

    private Connection connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/taskAlgo";
            String username = "root";
            String password = "";
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadTaskStatuses() {
        taskStatuses.clear();
        String query = "SELECT task_date, status FROM calendartasks";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("task_date").toLocalDate();
                String status = resultSet.getString("status");
                taskStatuses.put(date, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCalendar() {
        calendarGrid.getChildren().clear();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        int daysInMonth = currentYearMonth.lengthOfMonth();
        int row = 1, col = (dayOfWeek % 7);

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);

            VBox dayBox = new VBox();
            dayBox.getStyleClass().add("day-box");

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("day-label");
            dayBox.getChildren().add(dayLabel);


            if (taskStatuses.containsKey(date)) {
                String status = taskStatuses.get(date);
                Label statusIndicator = new Label();
                statusIndicator.getStyleClass().add("status-indicator");
                if ("pending".equalsIgnoreCase(status)) {
                    statusIndicator.setStyle("-fx-background-color: #FF5722;");
                } else if ("done".equalsIgnoreCase(status)) {
                    statusIndicator.setStyle("-fx-background-color: #4CAF50;");
                }
                dayBox.getChildren().add(statusIndicator);
            }

            // Add hover functionality to show task summary
            dayBox.setOnMouseEntered(e -> showTaskSummary(date, dayBox));
            dayBox.setOnMouseExited(e -> hideTaskSummary(dayBox));

            // Add click functionality to open task details
            dayBox.setOnMouseClicked(e -> showTaskDetails(date));

            calendarGrid.add(dayBox, col, row);
            col++;

            if (col == 7) {
                col = 0;
                row++;
            }
        }

        monthLabel.setText(currentYearMonth.getMonth() + " " + currentYearMonth.getYear());
    }

    private void showTaskSummary(LocalDate date, VBox dayBox) {
        Tooltip summaryTooltip = new Tooltip();
        String query = "SELECT task_name, status FROM calendartasks WHERE task_date = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();

            StringBuilder summary = new StringBuilder("Tasks:\n");
            while (resultSet.next()) {
                String taskName = resultSet.getString("task_name");
                String status = resultSet.getString("status");
                summary.append("- ").append(taskName).append(" (").append(status).append(")\n");
            }

            if (summary.toString().equals("Tasks:\n")) {
                summaryTooltip.setText("No tasks available");
            } else {
                summaryTooltip.setText(summary.toString());
            }

            Tooltip.install(dayBox, summaryTooltip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hideTaskSummary(VBox dayBox) {
        Tooltip.uninstall(dayBox, null);
    }

    private void showTaskDetails(LocalDate date) {
        String query = "SELECT task_name, status FROM calendartasks WHERE task_date = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();

            Dialog<String> taskDetailsDialog = new Dialog<>();
            taskDetailsDialog.setTitle("Task Details for " + date);

            VBox dialogContent = new VBox();
            dialogContent.setSpacing(10);
            dialogContent.getStyleClass().add("task-dialog");

            Label taskLabel = new Label("Tasks for " + date + ":");
            taskLabel.getStyleClass().add("task-dialog-title");
            dialogContent.getChildren().add(taskLabel);

            while (resultSet.next()) {
                String taskName = resultSet.getString("task_name");
                String status = resultSet.getString("status");

                Label taskItem = new Label(" - " + taskName + " (" + status + ")");
                taskItem.getStyleClass().add("task-dialog-item");
                if ("done".equalsIgnoreCase(status)) {
                    taskItem.getStyleClass().add("done");
                } else if ("pending".equalsIgnoreCase(status)) {
                    taskItem.getStyleClass().add("pending");
                }
                dialogContent.getChildren().add(taskItem);
            }

            taskDetailsDialog.getDialogPane().setContent(dialogContent);
            taskDetailsDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            taskDetailsDialog.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeMonth(int increment) {
        currentYearMonth = currentYearMonth.plusMonths(increment);
        loadTaskStatuses();
        loadCalendar();
    }

    @FXML
    private void toggleSidebar() {
        if (isSidebarVisible) {
            sidebar.setVisible(false);
            isSidebarVisible = false;
        } else {
            sidebar.setVisible(true);
            isSidebarVisible = true;
        }
    }
}


*/