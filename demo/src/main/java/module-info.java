module demo.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens demo.demo to javafx.fxml;
    exports demo.demo;
}