module com.example.poddic {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports view;
    opens view to javafx.fxml;

    exports controller;
    opens controller to javafx.fxml;
}