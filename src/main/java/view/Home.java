package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Category;
import model.Data;
import model.Session;
import javafx.scene.control.TreeView;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Home {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Button addCategory;
    @FXML
    private Button addData;
    @FXML
    private Button seeData;
    @FXML
    private Label info;
    @FXML
    private TreeView<Category> treeView;

    public void initialize(){
       init_cat();
       init_data();
    }

    public void setAddCategory(ActionEvent event) throws IOException {
        TextField enterName = new TextField("enter name");
        Button add = new Button("add");
        CheckBox isLeafCheck = new CheckBox("is this node leaf?");
        CheckBox isDataCheck = new CheckBox("does this node have data?");
        add.setOnAction(e -> {
            String name = enterName.getText();
            boolean isLeaf;
            boolean isData;
            if(isLeafCheck.isSelected()){
                isLeaf = true;
            }else {
                isLeaf = false;
            }
            if(isDataCheck.isSelected()){
                isData = true;
            }else {
                isData = false;
            }
            try {
                Category.addCategory(name, Session.getSession().currentCategory,isLeaf,isData);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add category: " + ex.getMessage());
            }

        });

        VBox layout = new VBox(10, enterName,isLeafCheck,isDataCheck, add);
        Scene scene = new Scene(layout, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add category");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setAddData(ActionEvent event) throws IOException{
        TextField enterName = new TextField("enter name");
        TextField enterDes = new TextField("enter description");
        Button add = new Button("add");

        add.setOnAction(e -> {
                try {
                    String name = enterName.getText();
                    if (name == null){
                        throw new IllegalArgumentException("name can not be null");
                    }
                    String des = enterDes.getText();
                    Data.addData(Session.getSession().currentCategory,name,des);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully!");
                }catch (Exception ex){

                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add category: " + ex.getMessage());
                }

            });

        VBox layout = new VBox(10, enterName,enterDes, add);
        Scene scene = new Scene(layout, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Data");
        stage.show();
    }
    public void setSeeData(ActionEvent event) throws IOException{
        if (Session.getSession().currentCategory.isData()){
            for (Data date:Session.session.allData
            ) {
                if (date.getCategory() == Session.getSession().currentCategory){
                    info.setText("");
                    String temp = info.getText();
                    temp += "\n"+date.getName();
                    info.setText(temp);
                }
            }
        }else {
            info.setText("");
            info.setText("no data!");
        }
    }

    public void init_cat(){
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
                        int categoryId,isLeaf,isData,parentid;
                        String path,name;
                        categoryId = categoryResultset.getInt("idcategory");
                        isLeaf = categoryResultset.getInt("isleaf");
                        isData = categoryResultset.getInt("isdata");
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
                        boolean bdata = true;
                        if (isLeaf == 0){
                            bleaf = false;
                        }
                        if (isData == 0){
                            bdata = false;
                        }
                        Category category = new Category(categoryId,name,parent,path,bleaf,bdata);
                        Session.getSession().allCategory.add(category);
                    }
                }
            }catch (SQLException e) {
                System.out.println(e.toString());
            }
            if (Session.getSession().allCategory.size() == 0){
                addCategory.setText("add first category");
            }else{
                addCategory.setText("add new category");
                Session.getSession().currentCategory = Session.getSession().allCategory.get(0);
            }
        }
        Map<Integer, TreeItem<Category>> items = new HashMap<>();
        for (Category c : Session.getSession().allCategory) {
            items.put(c.getIdcategory(), new TreeItem<>(c));
        }
        // wire up parent/child relationships
        TreeItem<Category> root = null;
        for (Category c : Session.getSession().allCategory) {
            TreeItem<Category> item = items.get(c.getIdcategory());
            if (c.getParent() == null) {
                root = item;               // or collect multiple roots under an “All” item
            } else {
                items.get(c.getParent().getIdcategory()).getChildren().add(item);
            }
        }
        treeView.setRoot(root);
        treeView.setShowRoot(true);

        // listen for user clicks/selections
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, newlySelected) -> {
            if (newlySelected != null) {
                Category clicked = newlySelected.getValue();
                Session.getSession().currentCategory = clicked;
                // now you can do whatever you want when a category is selected…
            }
        });
    }
    public void init_data(){
        if (Session.getSession().allData.size() == 0){
            ResultSet dataResultset;
            try {
                dataResultset=Session.database.executeQueryWithResult("select max(iddata) from data;");
                if(dataResultset.next()) {
                    Session.getSession().maxDataid = dataResultset.getInt("max(iddata)");
                }else {
                    Session.getSession().maxDataid = 0;
                }
                System.out.println("maxdataid="+Session.getSession().maxDataid);


                dataResultset=Session.database.executeQueryWithResult("select * from data;");
                if (dataResultset != null){
                    while (dataResultset.next()){
                        int iddata,idcat,idparent;
                        String des,name;
                        iddata = dataResultset.getInt("iddata");
                        idcat = dataResultset.getInt("idcategory");
                        idparent = dataResultset.getInt("parent");
                        des = dataResultset.getString("description");
                        name = dataResultset.getString("name");
                        Category tempCat = null;
                        for (Category cat:Session.getSession().allCategory
                             ) {
                            if (cat.getIdcategory() == idcat){
                                tempCat = cat;
                                break;
                            }
                        }
                        Data parent = null;
                        for (Data dat:Session.getSession().allData
                             ) {
                            if (dat.getIdData() == idparent){
                                parent = dat;
                                break;
                            }
                        }
                        Data data = new Data(iddata,tempCat,name,des,parent);
                        Session.getSession().allData.add(data);
                    }
                }
            }catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
}
