package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Category;
import model.Session;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Home {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Button add;

    public void initialize(){
        if (Session.getSession().allCategory.size() == 0){
            ResultSet categoryResultset;
            try {
                categoryResultset=Session.database.executeQueryWithResult("select max(idcategory) from category;");
                if(categoryResultset.next()) {
                    Session.getSession().setMaxCategoryid(categoryResultset.getInt("max(idcategory)"));
                }else {
                    Session.getSession().setMaxCategoryid(0);
                }
                System.out.println("maxid="+Session.getSession().getMaxCategoryid());


                categoryResultset=Session.database.executeQueryWithResult("select * from category;");
                if (categoryResultset != null){
                    while (categoryResultset.next()){
                        int categoryId,isLeaf,parentid;
                        String path,name;
                        categoryId = categoryResultset.getInt("idcategory");
                        isLeaf = categoryResultset.getInt("is leaf");
                        parentid = categoryResultset.getInt("parent");
                        Category parent = null;
                                path = categoryResultset.getString("path");
                        name = categoryResultset.getString("name");

                        if (categoryId == parentid){
                            parent = null;
                        }else {
                            for (Category index:Session.getSession().allCategory
                            ) {
                                if (index.getIdcategory() == parentid){
                                    parent = index;
                                    break;
                                }
                            }
                        }
                        boolean bleaf = true;
                        if (isLeaf == 0){
                            bleaf = false;
                        }
                        Category category = new Category(categoryId,name,parent,path,bleaf);
                        category.setLeaf(isLeaf);
                        category.setName(name);
                        category.setPath(path);
                        category.setParent(parent);
                        Session.getSession().allCategory.add(category);
                    }
                }
            }catch (SQLException e) {
                System.out.println(e.toString());
            }
            if (Session.getSession().allCategory.size() == 0){
                add.setText("add first category");
            }else{
                add.setText("add new category");
                Session.getSession().currentCategory = Session.getSession().allCategory.get(0);
                Session.getSession().currentCategory.button = new Button(Session.getSession().currentCategory.getName());
                anchor.getChildren().add(Session.getSession().currentCategory.button);
                Session.getSession().currentCategory.button.setOnAction(e -> {
                    for (Category index : Session.getSession().allCategory
                         ) {
                        if (index.getParent() == Session.getSession().currentCategory){
                            index.button = new Button(index.getName());
                            anchor.getChildren().add(index.button);
                        }
                    }
                });
            }
        }
    }

    public void setAdd(ActionEvent event) throws IOException {
        TextField enterName = new TextField("enter name");
        Button add = new Button("add");
        add.setOnAction(e -> {
            String name = enterName.getText();
            try {
                Category.addCategory(name, Session.getSession().currentCategory);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add category: " + ex.getMessage());
            }

        });

        VBox layout = new VBox(10, enterName, add);
        Scene scene = new Scene(layout, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Name");
        stage.show();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
