package dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class PreplanController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextField taskStartTimeField;

    @FXML
    private TextField taskEndTimeField;

    @FXML
    private ListView<String> taskListView;

    @FXML
    private Spinner<Integer> mealSpinner;

    @FXML
    private Spinner<Integer> gymHourSpinner;

    @FXML
    private Spinner<Integer> gymMinuteSpinner;

    @FXML
    private Spinner<Integer> sleepHourSpinner;

    @FXML
    private Spinner<Integer> sleepMinuteSpinner;

    private final ObservableList<String> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the ListView
        taskListView.setItems(taskList);

        // Set Spinner factories
        mealSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));
        gymHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 1));
        gymMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        sleepHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 8));
        sleepMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    private void addTask() {
        String taskName = taskNameField.getText();
        String startTime = taskStartTimeField.getText();
        String endTime = taskEndTimeField.getText();

        if (taskName.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill out all fields for the task.");
            return;
        }

        // Add task to the list
        String task = taskName + " (" + startTime + " - " + endTime + ")";
        taskList.add(task);

        // Clear input fields
        taskNameField.clear();
        taskStartTimeField.clear();
        taskEndTimeField.clear();
    }

    @FXML
    private void savePreplan() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showAlert(Alert.AlertType.WARNING, "Date Not Selected", "Please select a date for the preplan.");
            return;
        }

        if (taskList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Tasks", "Please add at least one task to save the preplan.");
            return;
        }

        int meals = mealSpinner.getValue();
        int gymHours = gymHourSpinner.getValue();
        int gymMinutes = gymMinuteSpinner.getValue();
        int sleepHours = sleepHourSpinner.getValue();
        int sleepMinutes = sleepMinuteSpinner.getValue();

        // Simulate saving to the database
        System.out.println("Saving Preplan for " + selectedDate);
        System.out.println("Tasks: " + taskList);
        System.out.println("Number of Meals: " + meals);
        System.out.println("Gym Time: " + gymHours + " hours " + gymMinutes + " minutes");
        System.out.println("Sleep Time: " + sleepHours + " hours " + sleepMinutes + " minutes");

        showAlert(Alert.AlertType.INFORMATION, "Preplan Saved", "Your preplan has been saved successfully!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
