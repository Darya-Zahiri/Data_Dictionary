module com.example.poddic {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.poddic to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
}