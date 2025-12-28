package dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

    @FXML
    private AnchorPane sidebar;
    @FXML
    private FontAwesomeIcon menuButton;
    @FXML
    private FontAwesomeIcon homeIcon;
    @FXML
    private StackPane contentPane;
    @FXML
    private Label homeLabel;
    @FXML
    private Label welcomeLabel;

    private boolean isSidebarExpanded = false;
    private boolean isHomePage = true;

    // Method to toggle the sidebar visibility
    public void toggleSidebar() {
        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(300), sidebar);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), menuButton);

        if (isSidebarExpanded) {
            sidebarTransition.setToX(-200);  // Move sidebar off-screen
            rotateTransition.setByAngle(-90); // Rotate the menu icon
            isSidebarExpanded = false;
        } else {
            sidebarTransition.setToX(0);     // Bring sidebar into view
            rotateTransition.setByAngle(90);  // Rotate the menu icon
            isSidebarExpanded = true;
        }

        sidebarTransition.play();
        rotateTransition.play();
    }

    @FXML
    public void initialize() {
        // Initial setup for sidebar
        sidebar.setTranslateX(-200);
        updateHomeIconColor();
    }

    // Method to update the color of the home icon
    public void updateHomeIconColor() {
        homeIcon.setFill(isHomePage ? Color.web("#0862d0") : Color.web("#F3F3F3"));
    }

    // Method to load content dynamically into the content pane
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent newContent = loader.load();

            // Fade out existing content
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), contentPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(newContent);

                // Fade in the new content
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), contentPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // Methods for sidebar button clicks
    @FXML
    private void onHomeButtonClick() {
        isHomePage = true;
        updateHomeIconColor();
        loadContent("Dashboard.fxml");
    }

    @FXML
    private void onCalendarIconClick() {
        isHomePage = false;
        updateHomeIconColor();
        loadContent("calenderWindow.fxml");
    }

    @FXML
    private void onGroupIconClick() {
        isHomePage = false;
        updateHomeIconColor();
        loadContent("GroupWindow.fxml");
    }

    @FXML
    private void onSettingsIconClick() {
        isHomePage = false;
        updateHomeIconColor();
        loadContent("SettingsWindow.fxml");
    }
}
