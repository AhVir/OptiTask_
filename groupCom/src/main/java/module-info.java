module group.groupcom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens group to javafx.fxml;
    exports group;
}