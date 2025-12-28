module demo.chatgpt {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires json;
    requires javafx.graphics;
    requires org.json;
    requires java.desktop;


    opens chatgpt to javafx.fxml;
    exports chatgpt;
}