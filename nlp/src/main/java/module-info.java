module demo.nlp {
    requires javafx.controls;
    requires javafx.fxml;


    opens demo.nlp to javafx.fxml;
    exports demo.nlp;
}