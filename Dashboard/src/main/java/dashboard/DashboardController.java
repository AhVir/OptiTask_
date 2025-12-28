//package dashboard;
//
//import javafx.animation.FadeTransition;
//import javafx.animation.RotateTransition;
//import javafx.animation.TranslateTransition;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.StackPane;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import javafx.scene.paint.Color;
//import javafx.util.Duration;
//
//import java.io.IOException;
//
//public class DashboardController {
//
//    @FXML
//    private AnchorPane sidebar;
//    @FXML
//    private FontAwesomeIcon menuButton, homeIcon, calendarIcon, groupIcon, settingsIcon;
//    @FXML
//    private StackPane contentPane;
//
//    private boolean sidebarVisible = false;
//    private String activePage = "home";
//
//    @FXML
//    public void initialize() {
//        sidebar.setTranslateX(-200); // Initially hide sidebar
//        updateActiveIcon();
//    }
//
//    public void toggleSidebar() {
//        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(300), sidebar);
//        RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), menuButton);
//
//        if (sidebarVisible) {
//            sidebarTransition.setToX(-200);
//            rotateTransition.setByAngle(-90);
//        } else {
//            sidebarTransition.setToX(0);
//            rotateTransition.setByAngle(90);
//        }
//
//        sidebarVisible = !sidebarVisible;
//        sidebarTransition.play();
//        rotateTransition.play();
//    }
//
//    private void updateActiveIcon() {
//        homeIcon.setFill(Color.web("#F3F3F3"));
//        calendarIcon.setFill(Color.web("#F3F3F3"));
//        groupIcon.setFill(Color.web("#F3F3F3"));
//        settingsIcon.setFill(Color.web("#F3F3F3"));
//
//        switch (activePage) {
//            case "home":
//                homeIcon.setFill(Color.web("#0862d0"));
//                break;
//            case "calendar":
//                calendarIcon.setFill(Color.web("#0862d0"));
//                break;
//            case "group":
//                groupIcon.setFill(Color.web("#0862d0"));
//                break;
//            case "settings":
//                settingsIcon.setFill(Color.web("#0862d0"));
//                break;
//        }
//    }
//
//    private void loadContent(String fxmlFile) {
//        try {
//            // Load the new FXML content
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
//            Parent newContent = loader.load();
//
//            // Look for the contentPane StackPane directly from the loaded FXML
//            StackPane newStackPane = (StackPane) newContent.lookup("#contentPane");
//
//            // If the contentPane is found
//            if (newStackPane != null) {
//                // Fade out existing content inside the StackPane
//                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), contentPane);
//                fadeOut.setFromValue(1.0);
//                fadeOut.setToValue(0.0);
//
//                fadeOut.setOnFinished(event -> {
//                    contentPane.getChildren().clear(); // Clear out the StackPane content
//                    contentPane.getChildren().addAll(newStackPane.getChildren()); // Add only the StackPane's children
//
//                    // Fade in the new content
//                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), contentPane);
//                    fadeIn.setFromValue(0.0);
//                    fadeIn.setToValue(1.0);
//                    fadeIn.play();
//
//                    // Automatically hide the sidebar
//                    toggleSidebar();
//                });
//
//                fadeOut.play();
//            } else {
//                System.out.println("contentPane not found in the loaded FXML.");
//            }
//
//        } catch (IOException e) {
//            System.out.println("Error loading FXML: " + fxmlFile);
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void onHomeButtonClick() {
//        activePage = "home";
//        updateActiveIcon();
//        loadContent("Dashboard.fxml");
//    }
//
//    @FXML
//    private void onCalendarIconClick() {
//        activePage = "calendar";
//        updateActiveIcon();
//        loadContent("calenderWindow.fxml");
//    }
//
//    @FXML
//    private void onGroupIconClick() {
//        activePage = "group";
//        updateActiveIcon();
//        loadContent("Group.fxml");
//    }
//
//    @FXML
//    private void onSettingsIconClick() {
//        activePage = "settings";
//        updateActiveIcon();
//        loadContent("Settings.fxml");
//    }
//}

package dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

    @FXML
    private AnchorPane sidebar;
    @FXML
    private FontAwesomeIcon menuButton, homeIcon, calendarIcon, groupIcon, settingsIcon;
    @FXML
    private StackPane contentPane;

    private boolean sidebarVisible = false;
    private String activePage = "home";

    @FXML
    public void initialize() {
        sidebar.setTranslateX(-200); // Initially hide sidebar
        updateActiveIcon();
    }

    public void toggleSidebar() {
        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(300), sidebar);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), menuButton);

        if (sidebarVisible) {
            sidebarTransition.setToX(-200);
            rotateTransition.setByAngle(-90);
        } else {
            sidebarTransition.setToX(0);
            rotateTransition.setByAngle(90);
        }

        sidebarVisible = !sidebarVisible;
        sidebarTransition.play();
        rotateTransition.play();
    }

//    private void updateActiveIcon() {
//        // Remove 'active' style from all icons
//        homeIcon.getStyleClass().remove("active");
//        calendarIcon.getStyleClass().remove("active");
//        groupIcon.getStyleClass().remove("active");
//        settingsIcon.getStyleClass().remove("active");
//
//        // Add 'active' style to the current icon
//        switch (activePage) {
//            case "home" -> homeIcon.getStyleClass().add("active");
//            case "calendar" -> calendarIcon.getStyleClass().add("active");
//            case "group" -> groupIcon.getStyleClass().add("active");
//            case "settings" -> settingsIcon.getStyleClass().add("active");
//        }
//    }

    private void updateActiveIcon() {
        // Mark all icons as inactive
        homeIcon.getStyleClass().removeAll("active");
        calendarIcon.getStyleClass().removeAll("active");
        groupIcon.getStyleClass().removeAll("active");
        settingsIcon.getStyleClass().removeAll("active");

        homeIcon.getStyleClass().add("inactive");
        calendarIcon.getStyleClass().add("inactive");
        groupIcon.getStyleClass().add("inactive");
        settingsIcon.getStyleClass().add("inactive");

        // Add 'active' style to the current icon
        switch (activePage) {
            case "home" -> {
                homeIcon.getStyleClass().add("active");
                homeIcon.getStyleClass().remove("inactive");
            }
            case "calendar" -> {
                calendarIcon.getStyleClass().add("active");
                calendarIcon.getStyleClass().remove("inactive");
            }
            case "group" -> {
                groupIcon.getStyleClass().add("active");
                groupIcon.getStyleClass().remove("inactive");
            }
            case "settings" -> {
                settingsIcon.getStyleClass().add("active");
                settingsIcon.getStyleClass().remove("inactive");
            }
        }
    }


    private void loadContent(String fxmlFile) {
        try {
            // Load the new FXML content
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent newContent = loader.load();

            // Look for the contentPane StackPane directly from the loaded FXML
            StackPane newStackPane = (StackPane) newContent.lookup("#contentPane");

            // If the contentPane is found
            if (newStackPane != null) {
                // Fade out existing content inside the StackPane
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), contentPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                fadeOut.setOnFinished(event -> {
                    contentPane.getChildren().clear(); // Clear out the StackPane content
                    contentPane.getChildren().addAll(newStackPane.getChildren()); // Add only the StackPane's children

                    // Fade in the new content
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), contentPane);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();

                    // Automatically hide the sidebar
                    toggleSidebar();
                });

                fadeOut.play();
            } else {
                System.out.println("contentPane not found in the loaded FXML.");
            }

        } catch (IOException e) {
            System.out.println("Error loading FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void onHomeButtonClick() {
        activePage = "home";
        updateActiveIcon();
        loadContent("Dashboard.fxml");
    }

    @FXML
    private void onCalendarIconClick() {
        activePage = "calendar";
        updateActiveIcon();
        loadContent("calenderWindow.fxml");
    }

    @FXML
    private void onGroupIconClick() {
        activePage = "group";
        updateActiveIcon();
        loadContent("Group.fxml");
    }

    @FXML
    private void onSettingsIconClick() {
        activePage = "settings";
        updateActiveIcon();
        loadContent("Settings.fxml");
    }
}
