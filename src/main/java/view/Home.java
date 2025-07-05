package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Category;
import model.Session;

import java.io.IOException;

public class Home {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Button addFirst;

    public void initialize(){
        if(Session.getSession().allCategory.size()!=0){
            addFirst.setDisable(true);
            addFirst.setVisible(false);
        }
    }

    public void setAddFirst(ActionEvent event) throws IOException {
        TextField enterName = new TextField("enter name");
        Button add = new Button("add");
        add.setOnAction(e -> {
            String name = enterName.getText();
            Category.addCategory(name,null);
        });

        VBox layout = new VBox(10, enterName, add);
        Scene scene = new Scene(layout, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Name");
        stage.show();
    }
}
