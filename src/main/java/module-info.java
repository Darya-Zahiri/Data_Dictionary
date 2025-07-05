module com.example.poddic {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.poddic to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
}