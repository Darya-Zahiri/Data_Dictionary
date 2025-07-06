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
import java.sql.ResultSet;
import java.sql.SQLException;

public class Home {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Button addFirst;

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
                        categoryId = categoryResultset.getInt("categoryid");
                        isLeaf = categoryResultset.getInt("is leaf");
                        parentid = categoryResultset.getInt("parent");
                        Category parent = null;
                                path = categoryResultset.getString("path");
                        name = categoryResultset.getString("name");
                        for (Category index:Session.getSession().allCategory
                             ) {
                            if (index.getIdcategory() == parentid){
                                parent = index;
                                break;
                            }
                        }
                        Category category = new Category(name,parent);
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
        }else {
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
