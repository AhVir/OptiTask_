package dashboard;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class calendarWindowController implements Initializable {
    @FXML
    private Label monthLabel;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private GridPane calendarGrid;

    private Connection connection;
    private YearMonth currentYearMonth;

    @FXML
    private StackPane contentPane;
    @FXML
    private FontAwesomeIcon homeIcon;

    private boolean isHomePage = true;

    private Map<LocalDate, String> taskStatuses = new HashMap<>();

    // Placeholder for current user ID (should be set when user logs in)
    private int currentUserId = 1; // Placeholder



    public void showAddTaskDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Please enter the details of the new task:");

        // Modernizing the layout with a light blueish background
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f0f8ff;"); // Light blue background

        // Task Name field
        Label taskNameLabel = new Label("Task Name:");
        taskNameLabel.setStyle("-fx-text-fill: #4c4f66;"); // Dark grey text
        TextField taskNameField = new TextField();
        taskNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0c6e3; -fx-border-radius: 25px; -fx-padding: 10px; -fx-background-radius: 25px; -fx-focus-color: #a0c6e3;");
        grid.add(taskNameLabel, 0, 0);
        grid.add(taskNameField, 1, 0);

        // Duration field
        Label durationLabel = new Label("Duration (hours):");
        durationLabel.setStyle("-fx-text-fill: #4c4f66;");
        TextField durationField = new TextField();
        durationField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0c6e3; -fx-border-radius: 25px; -fx-padding: 10px; -fx-background-radius: 25px; -fx-focus-color: #a0c6e3;");
        grid.add(durationLabel, 0, 1);
        grid.add(durationField, 1, 1);

        // Checkbox for Breakable Task
        CheckBox breakableCheckbox = new CheckBox("Breakable Task");
        breakableCheckbox.setStyle("-fx-text-fill: #4c4f66;");
        grid.add(breakableCheckbox, 0, 2, 2, 1);

        // Radio buttons for Task Type
        ToggleGroup taskTypeGroup = new ToggleGroup();
        RadioButton mandatoryRadioButton = new RadioButton("Mandatory");
        mandatoryRadioButton.setToggleGroup(taskTypeGroup);
        mandatoryRadioButton.setStyle("-fx-text-fill: #4c4f66;");
        mandatoryRadioButton.setSelected(true);
        RadioButton optionalRadioButton = new RadioButton("Optional");
        optionalRadioButton.setToggleGroup(taskTypeGroup);
        optionalRadioButton.setStyle("-fx-text-fill: #4c4f66;");
        grid.add(mandatoryRadioButton, 0, 3);
        grid.add(optionalRadioButton, 1, 3);

        // Checkbox for Specific Time Slot
        CheckBox specificTimeSlotCheckbox = new CheckBox("Needs a specific time slot?");
        specificTimeSlotCheckbox.setStyle("-fx-text-fill: #4c4f66;");
        grid.add(specificTimeSlotCheckbox, 0, 4, 2, 1);

        // Start Time and End Time fields (hidden initially)
        Label startTimeLabel = new Label("Start Time:");
        startTimeLabel.setStyle("-fx-text-fill: #4c4f66;");
        TextField startTimeField = new TextField();
        startTimeField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0c6e3; -fx-border-radius: 25px; -fx-padding: 10px; -fx-background-radius: 25px; -fx-focus-color: #a0c6e3;");
        Label endTimeLabel = new Label("End Time:");
        endTimeLabel.setStyle("-fx-text-fill: #4c4f66;");
        TextField endTimeField = new TextField();
        endTimeField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0c6e3; -fx-border-radius: 25px; -fx-padding: 10px; -fx-background-radius: 25px; -fx-focus-color: #a0c6e3;");

        // Initially hide Start and End Time fields
        startTimeLabel.setVisible(false);
        startTimeField.setVisible(false);
        endTimeLabel.setVisible(false);
        endTimeField.setVisible(false);

        grid.add(startTimeLabel, 0, 5);
        grid.add(startTimeField, 1, 5);
        grid.add(endTimeLabel, 0, 6);
        grid.add(endTimeField, 1, 6);

        // Priority selection
        Label priorityLabel = new Label("Priority:");
        priorityLabel.setStyle("-fx-text-fill: #4c4f66;");
        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.setValue("Medium");
        priorityComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0c6e3; -fx-border-radius: 25px; -fx-padding: 10px; -fx-background-radius: 25px; -fx-focus-color: #a0c6e3;");
        grid.add(priorityLabel, 0, 7);
        grid.add(priorityComboBox, 1, 7);

        // Show/Hide time fields based on checkbox
        specificTimeSlotCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            startTimeLabel.setVisible(newValue);
            startTimeField.setVisible(newValue);
            endTimeLabel.setVisible(newValue);
            endTimeField.setVisible(newValue);
        });

        dialog.getDialogPane().setContent(grid);

        // Modern button styling with rounded corners and consistent background
        ButtonType okButtonType = new ButtonType("Add Task", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Updated button styling for both Add Task and Cancel with rounded corners and blueish theme
        dialog.getDialogPane().lookupButton(okButtonType).setStyle("-fx-background-color: #a0c6e3; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 25px; -fx-font-size: 14px; -fx-font-weight: bold;");
        dialog.getDialogPane().lookupButton(cancelButtonType).setStyle("-fx-background-color: #a0c6e3; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 25px; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Fix for button area to match background
        dialog.getDialogPane().setStyle("-fx-background-color: #f0f8ff;");

        dialog.setResultConverter(button -> {
            if (button == okButtonType) {
                String taskName = taskNameField.getText();
                double duration = Double.parseDouble(durationField.getText());
                boolean isBreakable = breakableCheckbox.isSelected();
                String taskType = mandatoryRadioButton.isSelected() ? "Mandatory" : "Optional";
                boolean needsSpecificTimeSlot = specificTimeSlotCheckbox.isSelected();
                String priority = priorityComboBox.getValue();

                // Start and end time if specific time slot is selected
                String startTime = needsSpecificTimeSlot ? startTimeField.getText() : "";
                String endTime = needsSpecificTimeSlot ? endTimeField.getText() : "";

                addNewTask(taskName, duration, isBreakable, taskType, needsSpecificTimeSlot, startTime, endTime, priority);
            }
            return null;
        });

        dialog.showAndWait();
    }



    @FXML
    public void addNewTask(String taskName, double duration, boolean isBreakable, String taskType,
                           boolean needsSpecificTimeSlot, String startTime, String endTime, String priority) {
        if (taskName.isEmpty() || duration <= 0) {
            showAlert("Invalid input", "Please provide a valid task name and duration.");
            return;
        }

        List<Task> existingPreplanTasks = getExistingPreplanTasks(currentUserId);
        List<Task> existingCalendarTasks = getExistingCalendarTasks(currentUserId);

        List<TimeSlot> availableSlots = getAvailableTimeSlots(existingPreplanTasks, existingCalendarTasks, duration);

        if (availableSlots.isEmpty()) {
            showAlert("No available slot", "No available time slot for this task.");
            return;
        }

        if (taskType.equals("Mandatory")) {
            TimeSlot bestSlot = findBestSlotForMandatoryTask(availableSlots);
            if (bestSlot != null) {
                scheduleTask(bestSlot, taskName, duration, isBreakable, needsSpecificTimeSlot, startTime, endTime, priority);
            } else {
                showAlert("Conflict", "Unable to schedule mandatory task due to conflicts.");
            }
        } else {
            TimeSlot bestSlot = availableSlots.get(0);
            scheduleTask(bestSlot, taskName, duration, isBreakable, needsSpecificTimeSlot, startTime, endTime, priority);
        }

        loadCalendar();
    }


    private List<Task> getExistingPreplanTasks(int userId) {
        String query = "SELECT * FROM Tasks WHERE user_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Extract task properties from the result set
                String taskName = rs.getString("task_name");
                float duration = rs.getFloat("duration");
                String priority = rs.getString("priority");
                boolean isBreakable = rs.getBoolean("is_breakable");
                LocalDateTime specificTime = rs.getTimestamp("specific_time") != null
                        ? rs.getTimestamp("specific_time").toLocalDateTime()
                        : null;
                String status = rs.getString("status");
                LocalDate deadline = rs.getDate("deadline") != null
                        ? rs.getDate("deadline").toLocalDate()
                        : null;

                // Add task to the list
                tasks.add(new Task(taskName, duration, priority, isBreakable, specificTime, status, deadline));
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
                // Get data from the result set
                String taskName = rs.getString("task_name");
                float duration = rs.getFloat("duration");
                String priority = rs.getString("priority");
                boolean isBreakable = rs.getBoolean("is_breakable");
                LocalDateTime specificTime = rs.getTimestamp("task_date").toLocalDateTime();
                String status = rs.getString("status");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();  // Assuming 'deadline' is stored as a DATE

                // Create a Task object with the retrieved data
                tasks.add(new Task(taskName, duration, priority, isBreakable, specificTime, status, deadline));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    private List<TimeSlot> getAvailableTimeSlots(List<Task> preplanTasks, List<Task> calendarTasks, double taskDuration) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        // Implement logic to find available time slots based on taskDuration
        return availableSlots;
    }

    private TimeSlot findBestSlotForMandatoryTask(List<TimeSlot> availableSlots) {
        return availableSlots.isEmpty() ? null : availableSlots.get(0);
    }

    private void scheduleTask(TimeSlot slot, String taskName, double duration, boolean isBreakable,
                              boolean needsSpecificTimeSlot, String startTime, String endTime, String priority) {
        LocalDateTime taskStartTime;
        LocalDateTime taskEndTime;

        // If specific time slot is needed, parse the start and end time
        if (needsSpecificTimeSlot) {
            taskStartTime = LocalDateTime.parse(startTime);
            taskEndTime = LocalDateTime.parse(endTime);
        } else {
            // If no specific time is needed, use the slot's start time
            taskStartTime = slot.getStartTime();
            taskEndTime = taskStartTime.plusHours((long) duration);  // Assuming duration is in hours
        }

        // Define deadline as the task's end time
        LocalDate deadline = taskEndTime.toLocalDate();

        String insertQuery = "INSERT INTO Tasks (user_id, task_name, duration, priority, is_breakable, specific_time, status, deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, currentUserId);  // Assuming currentUserId is defined elsewhere
            stmt.setString(2, taskName);
            stmt.setDouble(3, duration);
            stmt.setString(4, priority);  // Low, Medium, High
            stmt.setBoolean(5, isBreakable);
            stmt.setTimestamp(6, Timestamp.valueOf(taskStartTime));  // Specific start time
            stmt.setString(7, "Pending");  // Initial status
            stmt.setDate(8, Date.valueOf(deadline));  // Deadline as date
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showPreplanWindow() {
        try {
            // Load the Preplan FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Preplan.fxml")); // Update the path accordingly
            Scene preplanScene = new Scene(loader.load());

            // Create a new Stage for the Preplan window
            Stage preplanStage = new Stage();
            preplanStage.setTitle("Preplan Your Day");
            preplanStage.setScene(preplanScene);

            // Show the Preplan window
            preplanStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any potential errors while loading the Preplan window
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = connectDatabase();
        if (connection == null) {
            System.err.println("Database connection failed!");
            return;
        }
        currentYearMonth = YearMonth.now();
        loadTaskStatuses(currentUserId);
        loadCalendar();
        previousButton.setOnAction(e -> changeMonth(-1));
        nextButton.setOnAction(e -> changeMonth(1));
    }

    private void changeMonth(int increment) {
        // Create a slide-out and fade-out transition for the existing calendar
        TranslateTransition slideOutTransition = new TranslateTransition(Duration.millis(300), calendarGrid);
        slideOutTransition.setFromX(0);
        slideOutTransition.setToX(increment > 0 ? -calendarGrid.getWidth() : calendarGrid.getWidth());

        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(300), calendarGrid);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);

        // Play both transitions simultaneously
        ParallelTransition transitionOut = new ParallelTransition(slideOutTransition, fadeOutTransition);

        transitionOut.setOnFinished(event -> {
            // Change the month after the animations
            currentYearMonth = currentYearMonth.plusMonths(increment);

            // Update the calendar grid with the new month
            loadCalendar();

            // Create a slide-in and fade-in transition for the new calendar
            TranslateTransition slideInTransition = new TranslateTransition(Duration.millis(300), calendarGrid);
            slideInTransition.setFromX(increment > 0 ? calendarGrid.getWidth() : -calendarGrid.getWidth());
            slideInTransition.setToX(0);

            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(300), calendarGrid);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);

            // Play both transitions simultaneously
            ParallelTransition transitionIn = new ParallelTransition(slideInTransition, fadeInTransition);
            transitionIn.play();
        });

        transitionOut.play();
    }


    private void loadCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int lengthOfMonth = currentYearMonth.lengthOfMonth();
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Add day of the week headers (e.g., Mon, Tue, Wed, etc.)
        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayOfWeekLabel = new Label(weekDays[i]);
            dayOfWeekLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #444444; -fx-font-size: 14px;");
            calendarGrid.add(dayOfWeekLabel, i, 0);  // Add weekday labels to the first row
        }

        // Add empty cells before the 1st day of the month to align the calendar
        for (int i = 0; i < dayOfWeek; i++) {
            calendarGrid.add(new Label(), i, 1);  // Empty cells for first row
        }

        // Loop through the days of the month and create the day labels
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-padding: 10px; -fx-background-radius: 10px; -fx-cursor: hand;");

            // Check if today's date matches the current date
            if (date.equals(currentDate)) {
                dayLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10px; -fx-background-color: #007bff; -fx-background-radius: 10px; -fx-cursor: hand;");
            }

            // VBox to center the label and ensure alignment
            VBox dayBox = new VBox(dayLabel);
            dayBox.setStyle("-fx-alignment: center; -fx-spacing: 5px; -fx-padding: 10px;");
            VBox.setVgrow(dayLabel, Priority.ALWAYS);  // This ensures vertical growth if needed
            HBox.setHgrow(dayLabel, Priority.ALWAYS);  // Ensures horizontal growth if needed

            // Check if there are tasks for this date and determine the status
            String query = "SELECT status FROM Tasks WHERE user_id = ? AND DATE(specific_time) = ?";
            boolean hasPending = false;  // Flag for pending tasks
            boolean hasCompleted = false;  // Flag for completed tasks
            boolean hasInProgress = false;  // Flag for in-progress tasks

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Assuming 'currentUserId' is the logged-in user's ID
                statement.setInt(1, currentUserId);  // Set user_id
                statement.setDate(2, Date.valueOf(date));  // Set the date you're querying for (e.g., "2025-01-26")

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String taskStatus = resultSet.getString("status");
                    if (taskStatus.equals("Pending")) {
                        hasPending = true;
                    } else if (taskStatus.equals("Completed")) {
                        hasCompleted = true;
                    } else if (taskStatus.equals("In Progress")) {
                        hasInProgress = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            // Show a dot based on the task status
            if (hasPending) {
                Circle statusDot = new Circle(5);
                statusDot.setFill(Color.RED);  // Red dot for pending tasks
                dayBox.getChildren().add(statusDot);
            } else {
                if (hasCompleted) {
                    Circle statusDot = new Circle(5);
                    statusDot.setFill(Color.GREEN);  // Green dot for completed tasks
                    dayBox.getChildren().add(statusDot);
                }
                if (hasInProgress) {
                    Circle statusDot = new Circle(5);
                    statusDot.setFill(Color.BLUE);  // Blue dot for in-progress tasks
                    dayBox.getChildren().add(statusDot);
                }
            }

            // Add hover effect with animation (scaling and background change)
            dayBox.setOnMouseEntered(event -> {
                // Adding scaling animation without affecting position
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), dayLabel);
                scaleTransition.setToX(1.2);  // Slightly scale up the day number
                scaleTransition.setToY(1.2);  // Slightly scale up the day number
                scaleTransition.setCycleCount(1);
                scaleTransition.play();

                // Keep the label in place by translating it slightly (adjust if needed)
                dayLabel.setTranslateX(0);
                dayLabel.setTranslateY(0);

                dayBox.setStyle("-fx-background-color: #e0f7fa; -fx-border-radius: 10px; -fx-cursor: hand;");

                // Delay before showing task summary
                PauseTransition delay = new PauseTransition(Duration.millis(300));  // Delay of 300ms
                delay.setOnFinished(e -> showTaskSummary(date, dayBox, currentUserId));
                delay.play();
            });

            dayBox.setOnMouseExited(event -> {
                // Revert scaling back to normal size
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), dayLabel);
                scaleTransition.setToX(1.0);  // Reset to original scale
                scaleTransition.setToY(1.0);  // Reset to original scale
                scaleTransition.setCycleCount(1);
                scaleTransition.play();

                dayBox.setStyle("-fx-background-color: transparent;");
                hideTaskSummary(dayBox);
            });

            // Show all tasks on click
            dayBox.setOnMouseClicked(event -> showTaskDetails(date, currentUserId));

            // Add the dayBox to the calendar grid
            calendarGrid.add(dayBox, (day + dayOfWeek - 1) % 7, (day + dayOfWeek - 1) / 7 + 1);  // Add day box to the grid
        }
    }





    private Connection connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/optitask";
            String username = "root";
            String password = "";
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void loadTaskStatuses(int userId) {
        taskStatuses.clear();
        String query = "SELECT specific_time, status FROM Tasks WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);  // Set the user ID for the logged-in user
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    LocalDate date = resultSet.getTimestamp("specific_time").toLocalDateTime().toLocalDate();
                    String status = resultSet.getString("status");
                    taskStatuses.put(date, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









//    private void showTaskDetails(LocalDate date, int userId) {
//        // Query to fetch task details from both calendartasks and preplan
//        String query = "SELECT c.task_name, c.status, c.task_description, p.start_time, p.end_time, p.task_type " +
//                "FROM calendartasks c " +
//                "LEFT JOIN preplan p ON c.task_name = p.task_name AND c.user_id = p.user_id " +
//                "WHERE c.task_date = ? AND c.user_id = ?";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setDate(1, Date.valueOf(date));  // Set the date filter
//            statement.setInt(2, userId);  // Set the user_id filter
//            ResultSet resultSet = statement.executeQuery();
//
//            Dialog<String> taskDetailsDialog = new Dialog<>();
//            taskDetailsDialog.setTitle("Task Details for " + date);
//            taskDetailsDialog.getDialogPane().getStyleClass().add("modern-dialog");  // Add custom style class
//
//            VBox dialogContent = new VBox();
//            dialogContent.setSpacing(20);
//            dialogContent.setPadding(new Insets(20));
//            dialogContent.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 12px;");
//
//            // Fetch user information for context (optional)
//            String userQuery = "SELECT username FROM users WHERE user_id = ?";
//            try (PreparedStatement userStatement = connection.prepareStatement(userQuery)) {
//                userStatement.setInt(1, userId);
//                ResultSet userResultSet = userStatement.executeQuery();
//                if (userResultSet.next()) {
//                    String username = userResultSet.getString("username");
//                    Label userLabel = new Label("User: " + username);
//                    userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #3f51b5; -fx-font-weight: bold;");
//                    dialogContent.getChildren().add(userLabel);
//                }
//            }
//
//            Label taskLabel = new Label("Tasks for " + date + ":");
//            taskLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4caf50;");
//            dialogContent.getChildren().add(taskLabel);
//
//            // Loop through the result set and display the task details
//            while (resultSet.next()) {
//                String taskName = resultSet.getString("task_name");
//                String status = resultSet.getString("status");
//                String taskDescription = resultSet.getString("task_description");
//                Time startTime = resultSet.getTime("start_time");  // Start time from preplan
//                Time endTime = resultSet.getTime("end_time");  // End time from preplan
//                String taskType = resultSet.getString("task_type");  // Task type (Mandatory/Optional)
//
//                // Format the times as strings
//                String timeRange = (startTime != null && endTime != null) ?
//                        "From " + startTime.toString() + " to " + endTime.toString() : "Time not set";
//
//                // Create the task label with name, status, description, and time
//                Label taskItem = new Label(" - " + taskName + " (" + status + ")");
//                taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-padding: 10px; -fx-background-radius: 10px;");
//
//                // Add the task description if available
//                if (taskDescription != null && !taskDescription.trim().isEmpty()) {
//                    taskItem.setText(taskItem.getText() + "\n  Description: " + taskDescription);
//                }
//
//                // Add different styles based on the task status
//                if ("done".equalsIgnoreCase(status)) {
//                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #4caf50; -fx-font-weight: bold; -fx-background-color: #e8f5e9;");
//                    taskItem.setText(taskItem.getText() + "\n  " + timeRange + "\n  Type: " + taskType);
//                } else if ("pending".equalsIgnoreCase(status)) {
//                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336; -fx-font-weight: bold; -fx-background-color: #ffebee;");
//                    taskItem.setText(taskItem.getText() + "\n  " + timeRange + "\n  Type: " + taskType);
//                } else if ("in progress".equalsIgnoreCase(status)) {
//                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
//                    taskItem.setText(taskItem.getText() + "\n  " + timeRange + "\n  Type: " + taskType);
//                }
//
//                // Add the task label to the dialog content
//                dialogContent.getChildren().add(taskItem);
//            }
//
//            // Add modern-style buttons
//            ButtonType okButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
//            taskDetailsDialog.getDialogPane().getButtonTypes().setAll(okButtonType);
//            taskDetailsDialog.getDialogPane().setStyle("-fx-background-radius: 15px;");
//
//            // Styling the OK button
//            Button okButton = (Button) taskDetailsDialog.getDialogPane().lookupButton(okButtonType);
//            okButton.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
//            okButton.setOnAction(event -> taskDetailsDialog.close());
//
//            // Set up the dialog content and show the dialog
//            taskDetailsDialog.getDialogPane().setContent(dialogContent);
//
//            // Add some animation to fade the dialog in
//            taskDetailsDialog.getDialogPane().setOpacity(0);
//            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), taskDetailsDialog.getDialogPane());
//            fadeIn.setFromValue(0);
//            fadeIn.setToValue(1);
//            fadeIn.play();
//
//            // Use showAndWait() to show the dialog
//            taskDetailsDialog.showAndWait();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    private void showTaskDetails(LocalDate date, int userId) {
        // Query to fetch task details from the Tasks table
        String query = "SELECT t.task_name, t.status, t.duration, t.specific_time, t.priority, t.deadline " +
                "FROM tasks t " +
                "WHERE t.user_id = ? AND t.deadline = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);  // Set the user_id filter
            statement.setDate(2, Date.valueOf(date));  // Set the date filter (deadline)
            ResultSet resultSet = statement.executeQuery();

            Dialog<String> taskDetailsDialog = new Dialog<>();
            taskDetailsDialog.setTitle("Task Details for " + date);
            taskDetailsDialog.getDialogPane().getStyleClass().add("modern-dialog");  // Add custom style class

            VBox dialogContent = new VBox();
            dialogContent.setSpacing(20);
            dialogContent.setPadding(new Insets(20));
            dialogContent.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 12px;");

            // Fetch user information for context (optional)
            String userQuery = "SELECT username FROM users WHERE user_id = ?";
            try (PreparedStatement userStatement = connection.prepareStatement(userQuery)) {
                userStatement.setInt(1, userId);
                ResultSet userResultSet = userStatement.executeQuery();
                if (userResultSet.next()) {
                    String username = userResultSet.getString("username");
                    Label userLabel = new Label("User: " + username);
                    userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #3f51b5; -fx-font-weight: bold;");
                    dialogContent.getChildren().add(userLabel);
                }
            }

            Label taskLabel = new Label("Tasks for " + date + ":");
            taskLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4caf50;");
            dialogContent.getChildren().add(taskLabel);

            // Loop through the result set and display the task details
            while (resultSet.next()) {
                String taskName = resultSet.getString("task_name");
                String status = resultSet.getString("status");
                float duration = resultSet.getFloat("duration");
                Timestamp specificTime = resultSet.getTimestamp("specific_time");  // Specific time for task
                String priority = resultSet.getString("priority");
                Date deadline = resultSet.getDate("deadline");

                // Format the specific time as a string
                String timeString = (specificTime != null) ? specificTime.toLocalDateTime().toString() : "Time not set";

                // Create the task label with name, status, description, and time
                Label taskItem = new Label(" - " + taskName + " (" + status + ")");
                taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-padding: 10px; -fx-background-radius: 10px;");

                // Add the task details
                taskItem.setText(taskItem.getText() + "\n  Duration: " + duration + " hours");
                taskItem.setText(taskItem.getText() + "\n  Specific Time: " + timeString);
                taskItem.setText(taskItem.getText() + "\n  Priority: " + priority);
                taskItem.setText(taskItem.getText() + "\n  Deadline: " + deadline);

                // Add different styles based on the task status
                if ("Completed".equalsIgnoreCase(status)) {
                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #4caf50; -fx-font-weight: bold; -fx-background-color: #e8f5e9;");
                } else if ("Pending".equalsIgnoreCase(status)) {
                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336; -fx-font-weight: bold; -fx-background-color: #ffebee;");
                } else if ("In Progress".equalsIgnoreCase(status)) {
                    taskItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                }

                // Add the task label to the dialog content
                dialogContent.getChildren().add(taskItem);
            }

            // Add modern-style buttons
            ButtonType okButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            taskDetailsDialog.getDialogPane().getButtonTypes().setAll(okButtonType);
            taskDetailsDialog.getDialogPane().setStyle("-fx-background-radius: 15px;");

            // Styling the OK button
            Button okButton = (Button) taskDetailsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
            okButton.setOnAction(event -> taskDetailsDialog.close());

            // Set up the dialog content and show the dialog
            taskDetailsDialog.getDialogPane().setContent(dialogContent);

            // Add some animation to fade the dialog in
            taskDetailsDialog.getDialogPane().setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), taskDetailsDialog.getDialogPane());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Use showAndWait() to show the dialog
            taskDetailsDialog.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





//    private void showTaskSummary(LocalDate date, VBox dayBox) {
//        Tooltip summaryTooltip = new Tooltip();
//        String query = "SELECT task_name, status FROM calendartasks WHERE task_date = ?";
//
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setDate(1, Date.valueOf(date));
//            ResultSet resultSet = statement.executeQuery();
//
//            StringBuilder summary = new StringBuilder("Tasks:\n");
//            while (resultSet.next()) {
//                String taskName = resultSet.getString("task_name");
//                String status = resultSet.getString("status");
//                summary.append("- ").append(taskName).append(" (").append(status).append(")\n");
//            }
//
//            if (summary.toString().equals("Tasks:\n")) {
//                summaryTooltip.setText("No tasks available");
//            } else {
//                summaryTooltip.setText(summary.toString());
//            }
//
//            Label dayLabel = (Label) dayBox.getChildren().get(0); // Get the Label from the VBox
//            Tooltip.install(dayLabel, summaryTooltip);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private void showTaskSummary(LocalDate date, VBox dayBox, int userId) {
        Tooltip summaryTooltip = new Tooltip();

        // Query to fetch task details from the "Tasks" table for a specific user and date
        String query = "SELECT task_name, status FROM tasks WHERE user_id = ? AND DATE(specific_time) = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);  // Set the user_id filter
            statement.setDate(2, Date.valueOf(date));  // Set the date filter
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

            // Get the Label from the VBox, assuming it's the first child of the VBox
            Label dayLabel = (Label) dayBox.getChildren().get(0);
            Tooltip.install(dayLabel, summaryTooltip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void hideTaskSummary(VBox dayBox) {
        Label dayLabel = (Label) dayBox.getChildren().get(0); // Get the Label from the VBox
        Tooltip.uninstall(dayLabel, null);
    }

    public static class TimeSlot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }
    }

    public class Task {
        private String name;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private float duration;
        private String priority;
        private boolean isBreakable;
        private LocalDateTime specificTime;
        private LocalDate deadline;

        // Constructor for creating a new task
        public Task(String name, float duration, String priority, boolean isBreakable,
                    LocalDateTime specificTime, String status, LocalDate deadline) {
            this.name = name;
            this.duration = duration;
            this.priority = priority;
            this.isBreakable = isBreakable;
            this.specificTime = specificTime;
            this.status = status;
            this.deadline = deadline;
        }

        // Getter for task name
        public String getName() {
            return name;
        }

        // Setter for task name
        public void setName(String name) {
            this.name = name;
        }

        // Getter for start time
        public LocalDateTime getStartTime() {
            return startTime;
        }

        // Setter for start time
        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        // Getter for end time
        public LocalDateTime getEndTime() {
            return endTime;
        }

        // Setter for end time
        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        // Getter for task status
        public String getStatus() {
            return status;
        }

        // Setter for task status
        public void setStatus(String status) {
            this.status = status;
        }

        // Getter for task duration
        public float getDuration() {
            return duration;
        }

        // Setter for task duration
        public void setDuration(float duration) {
            this.duration = duration;
        }

        // Getter for task priority
        public String getPriority() {
            return priority;
        }

        // Setter for task priority
        public void setPriority(String priority) {
            this.priority = priority;
        }

        // Getter for isBreakable
        public boolean isBreakable() {
            return isBreakable;
        }

        // Setter for isBreakable
        public void setBreakable(boolean breakable) {
            isBreakable = breakable;
        }

        // Getter for specific time
        public LocalDateTime getSpecificTime() {
            return specificTime;
        }

        // Setter for specific time
        public void setSpecificTime(LocalDateTime specificTime) {
            this.specificTime = specificTime;
        }

        // Getter for deadline
        public LocalDate getDeadline() {
            return deadline;
        }

        // Setter for deadline
        public void setDeadline(LocalDate deadline) {
            this.deadline = deadline;
        }
    }
}