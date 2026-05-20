
module org.ornamentdesigner {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.ornamentdesigner to javafx.fxml;
    exports org.ornamentdesigner;
}