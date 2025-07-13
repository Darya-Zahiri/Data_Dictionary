package view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
import java.util.Optional;

public class Home {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private BorderPane anchor;
    @FXML
    private Button addCategory;
    @FXML
    private Button addData;
    @FXML
    private Button seeData;
    @FXML
    private TextArea info;
    @FXML
    private TreeView<Category> treeView;
    @FXML private MenuItem addCategoryItem;
    @FXML private MenuItem updateCategoryItem;
    @FXML private MenuItem addDataItem;
    @FXML private MenuItem seeDataItem;
    @FXML private MenuItem refreshItem;
    @FXML private MenuItem deleteCategoryItem;

    Font farsiFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Vazir.ttf"), 16);

    public void initialize() {
        init_cat();
        init_data();
        addCategoryItem.setOnAction(e -> {
            try {
                setAddCategory(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        updateCategoryItem.setOnAction(e -> {
            try {
                setUpdateCategory(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        deleteCategoryItem.setOnAction(e -> {
            try {
                setDeleteCategory(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        addDataItem.setOnAction(e -> {
            try {
                setAddData(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        seeDataItem.setOnAction(e -> {
            try {
                setSeeData(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        refreshItem.setOnAction(e -> initialize());
    }


    public void setAddCategory(ActionEvent event) throws IOException {
        TextField enterName = new TextField("اسم را بنویسید");
        enterName.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        enterName.setFont(farsiFont);
        Button add = new Button("اضافه کن");
        add.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CheckBox isLeafCheck = new CheckBox("آیا برگ است؟");
        isLeafCheck.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CheckBox isDataCheck = new CheckBox("آیا دیتا دارد؟");
        isDataCheck.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
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
                showAlert(Alert.AlertType.INFORMATION, "موفقیت آمیز", "کتگوری با موفقیت اضافه شد!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "ارور", "کتگوری اضافه نشد!: " + ex.getMessage());
            }
            initialize();
        });

        VBox layout = new VBox(10, enterName,isLeafCheck,isDataCheck, add);
        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("اضافه کردن کتگوری");
        stage.show();
    }

    public void setUpdateCategory(ActionEvent event) throws IOException {
        TextField enterName = new TextField(Session.session.currentCategory.getName());
        enterName.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        enterName.setFont(farsiFont);
        Button add = new Button("به روز رسانی");
        add.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CheckBox isLeafCheck = new CheckBox("آیا برگ است؟");
        if (Session.session.currentCategory.isLeaf){
            isLeafCheck.setSelected(true);
        }
        isLeafCheck.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CheckBox isDataCheck = new CheckBox("آیا دیتا دارد؟");
        if (Session.session.currentCategory.isData){
            isLeafCheck.setSelected(true);
        }
        isDataCheck.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        ToggleGroup group = new ToggleGroup();
        final Category[] parent = new Category[1];
        VBox root = new VBox(10);
        for (Category category : Session.session.allCategory) {
            if(category != Session.session.currentCategory ){
                RadioButton radioButton = new RadioButton(category.toString());
                radioButton.setToggleGroup(group);
                radioButton.setOnAction(e -> {
                    parent[0] = category;
                });
                root.getChildren().addAll(radioButton);
            }
        }

        ScrollPane scrollPane = new ScrollPane(root);
        Label title = new Label("پدر را انتخاب کنید.");
        title.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
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
                Category.editCategory(name, parent[0],isLeaf,isData);
                showAlert(Alert.AlertType.INFORMATION, "موفقیت آمیز", "کتگوری با موفقیت ویرایش شد!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "ارور", "کتگوری ویرایش نشد!: " + ex.getMessage());
            }
            initialize();
        });

        VBox layout = new VBox(10, enterName,isLeafCheck,isDataCheck, scrollPane, add);
        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("ویرایش کتگوری");
        stage.show();
    }
    public void setDeleteCategory(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تایید حذف");
        alert.setHeaderText("آیا مطمئن هستید؟");
        alert.setContentText("در صورت حذف، کتگوری و فرزندان قابل بازیابی نیست!");

        // Add custom buttons if needed (optional)
        ButtonType yesButton = new ButtonType("بله", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("خیر", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            Category.deleteCategory();
            initialize(); // Refresh the view or data
        } else {
            // Optionally do something if the user cancels
            System.out.println("حذف لغو شد.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setAddData(ActionEvent event) throws IOException{
        TextField enterName = new TextField("اسم را بنویسید");
        enterName.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextField enterDes = new TextField("توضیحات را بنویسید");
        enterDes.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Button add = new Button("اضافه کن");
        ToggleGroup group = new ToggleGroup();
        final Data[] parent = new Data[1];
        VBox root = new VBox(10);
        for (Data data : Session.session.allData) {
            if(data.getCategory() == Session.session.currentCategory.getParent() ){
                RadioButton radioButton = new RadioButton(data.toString());
                radioButton.setToggleGroup(group);
                radioButton.setOnAction(e -> {
                    parent[0] = data;
                });
                root.getChildren().addAll(radioButton);
            }
        }

        ScrollPane scrollPane = new ScrollPane(root);
        Label title = new Label("پدر را انتخاب کنید.");
        title.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        add.setOnAction(e -> {
                try {
                    String name = enterName.getText();
                    if (name == null){
                        throw new IllegalArgumentException("نام نمیتواند خالی باشد!");
                    }
                    String des = enterDes.getText();
                    Data.addData(Session.getSession().currentCategory,name,des, parent[0]);

                    showAlert(Alert.AlertType.INFORMATION, "موفقیت آمیز!", "دیتا با موفقیت اضافه شد!");
                }catch (Exception ex){

                    showAlert(Alert.AlertType.ERROR, "ارور!", "دیتا اضافه نشد!: " + ex.getMessage());
                }
                initialize();
            });

        VBox layout = new VBox(10, enterName, enterDes, title, scrollPane, add);
        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("اضافه کردن دیتا");
        stage.show();
    }
    public void setSeeData(ActionEvent event) throws IOException{
        info.setText("");
        if (Session.getSession().currentCategory.isData()){
            for (Data date:Session.session.allData
            ) {
                if (date.getCategory() == Session.getSession().currentCategory){
                    String temp = info.getText();
                    temp += "\n"+date.getName();
                    info.setText(temp);
                }
            }
        }else {
            info.setText("");
            info.setText("دیتایی وجود ندارد!");
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
            if (Session.getSession().allCategory.size() != 0){
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
        Platform.runLater(() -> treeView.requestFocus());
        // listen for user clicks/selections
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, newlySelected) -> {
            if (newlySelected != null) {
                Category clicked = newlySelected.getValue();
                Session.getSession().currentCategory = clicked;
                // now you can do whatever you want when a category is selected…
            }
        });
        treeView.setOnKeyPressed(event -> {


            if (event.getCode() == KeyCode.ENTER) {


                TreeItem<Category> selectedItem = treeView.getSelectionModel().getSelectedItem();


                if (selectedItem != null) {


                    Category clicked = selectedItem.getValue();


                    Session.getSession().currentCategory = clicked;


                    if (selectedItem.isExpanded()){





                        selectedItem.setExpanded(false);


                    }else {





                        selectedItem.setExpanded(true);


                    }


                }


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
