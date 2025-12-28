module dashboard.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires fontawesomefx;
    requires java.sql;


    opens dashboard to javafx.fxml;
    exports dashboard;
}