package model;

import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Category {
    int idcategory;
    String name;
    Category parent;
    String path;
    public boolean isLeaf;
    public boolean isData;
    public Button button;

    public Category(int idcategory,String name,Category parent,String path,boolean isLeaf,boolean isData){
        this.idcategory = idcategory;
        this.name = name;
        this.parent = parent;
        this.path = path;
        this.isLeaf = isLeaf;
        this.isData = isData;
        button = new Button(this.name);
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setIdcategory(int id){
        idcategory = id;
    }
    public int getIdcategory(){
        return idcategory;
    }
    public void setParent(Category parent){
        this.parent = parent;
    }
    public Category getParent(){
        return parent;
    }
    public void setPath(String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }
    public void setLeaf(int leaf){
        if (leaf == 1){
            isLeaf = true;
        }else {
            isLeaf = false;
        }
    }
    public int getLeaf(){
        if (isLeaf){
            return 1;
        }else {
            return 0;
        }
    }

    public static void addCategory(String name,Category parent,boolean isLeaf,boolean isData){
        for (Category cat:Session.session.allCategory
             ) {
            if (cat.parent == parent){
                if (Objects.equals(cat.name, name)){
                    throw new IllegalArgumentException("نام تکراری است.");
                }
            }
        }

        int tempId = Session.getSession().getMaxCategoryid();
        String tempPath="";
        if (parent != null){
            if (!parent.isLeaf){
                tempId++;
                Session.getSession().setMaxCategoryid(tempId);
                    if (parent.idcategory == 1){

                        tempPath = parent.path + parent.idcategory;
                    }else {

                        tempPath = parent.path + "/" + parent.idcategory;
                    }

            }else {
                throw new IllegalArgumentException("پدر برگ است.");
            }
        }else{
            tempId++;
            Session.getSession().setMaxCategoryid(tempId);
            tempPath = "/" ;
        }

        Category tempCat = new Category(tempId,name,parent,tempPath,isLeaf,isData);
        Connection conn = null;
        boolean success = false;
        try {
            conn = Session.database.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO category (idcategory, parent, path, name, isleaf, isdata) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, tempCat.idcategory);
                if (parent != null){
                    stmt.setInt(2, tempCat.parent.idcategory);
                }else {
                    stmt.setInt(2, tempCat.idcategory);
                }
                stmt.setString(3, tempCat.path);
                stmt.setString(4, tempCat.name);
                stmt.setInt(5, tempCat.getLeaf());
                stmt.setInt(6, tempCat.isData());
                stmt.executeUpdate();
            }
            Session.getSession().allCategory.add(tempCat);
            conn.commit();  // Commit if all went well
            success = true;
        }catch (SQLException e){
            System.out.println(e.toString());
        }finally {
            try {
                conn.setAutoCommit(true);
            }catch (SQLException e){

            }
        }

    }
    public static void editCategory(String name,Category parent,boolean isLeaf,boolean isData){
        Category category = Session.session.currentCategory;
        Connection con = Session.database.getConnection();

        try {
            con.setAutoCommit(false);
            if (name != null){
                category.name = name;
            }
            if (parent != null){
                category.parent = parent;
                Category.checkParentRec(category,parent);
            }
            category.isLeaf = isLeaf;
            int leaf = 0;
            if (isLeaf){
                leaf = 1;
            }
            category.isData =isData;
            int data = 0;
            if (isData){
                data = 1;
            }
            String sql = "update category set name=? , isleaf=? , isdata=? where idcategory=?";
            try(PreparedStatement stm = Session.database.getConnection().prepareStatement(sql)) {
                stm.setString(1, name);
                stm.setInt(2, leaf);
                stm.setInt(3, data);
                stm.setInt(4, category.idcategory);
                stm.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
    }
    public static void deleteCategory(){
        Category category = Session.session.currentCategory;
        if (category.idcategory == 1){
            Connection con = Session.database.getConnection();
            try {
                con.setAutoCommit(false);
                try (Statement stmt = con.createStatement()){
                    stmt.executeUpdate("ALTER TABLE category DROP FOREIGN KEY fk_category_category1;");
                    stmt.executeUpdate(
                            "ALTER TABLE category " +
                                    "ADD CONSTRAINT fk_category_category1 " +
                                    "FOREIGN KEY (parent) " +
                                    "REFERENCES category (idcategory) " +
                                    "ON DELETE CASCADE;"
                    );
                }
                String sql = "DELETE FROM category WHERE (idcategory = ?)";
                try (PreparedStatement stmt = con.prepareStatement(sql)){
                    stmt.setInt(1, 1);
                    stmt.executeUpdate();
                }
                try (Statement stmt = con.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE category DROP FOREIGN KEY fk_category_category1;");
                    stmt.executeUpdate(
                            "ALTER TABLE category " +
                                    "ADD CONSTRAINT fk_category_category1 " +
                                    "FOREIGN KEY (parent) " +
                                    "REFERENCES category (idcategory) " +
                                    "ON DELETE RESTRICT;"
                    );
                }
                Session.getSession().allCategory.clear();
                con.commit();
            }catch (SQLException e){
                throw new RuntimeException(e.toString());
            }finally {
                try {
                    con.setAutoCommit(true);
                }catch (SQLException e){
                    throw new RuntimeException(e.toString());
                }
            }

        }else {

            Session.session.allCategory.sort(Comparator.comparingInt(cat -> cat.path.length()));
            int id = category.idcategory;
            String sub_1 = "/"+id+"/";
            String sub_2 = "/"+id;
            System.out.println(sub_1);
            for (int i=Session.session.allCategory.size()-1;i>-1;i--){
                category = Session.session.allCategory.get(i);
                if (category.path.contains(sub_1)||category.path.endsWith(sub_2)){
                    //delete data from database
                    try {
                        Session.database.executeQueryWithoutResult("delete from data where (idcategory="+category.idcategory+");");
                        System.out.println(category.idcategory);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.toString());
                    }
                    //delete data from all data
                    ArrayList<Data> remove_data = new ArrayList<>();
                    for (Data data:Session.session.allData
                    ) {
                        if (data.getCategory() == category){
                            remove_data.add(data);
                        }
                    }
                    Session.getSession().allData.removeAll(remove_data);
                    //delete category from database
                    try {
                        Session.database.executeQueryWithoutResult("delete from category where (idcategory="+category.idcategory+");");
                    } catch (SQLException e) {
                        throw new RuntimeException(e.toString());
                    }

                    //delete category from all category
                    Session.getSession().allCategory.remove(category);
                }
            }
            //for deleted node
            category = Session.session.currentCategory;
            try {
                //delete data from database
                Session.database.executeQueryWithoutResult("delete from data where (idcategory="+category.idcategory+");");
                //delete data from all data
                ArrayList<Data> remove_data = new ArrayList<>();
                for (Data data:Session.session.allData
                ) {
                    if (data.getCategory() == Session.session.currentCategory){
                        remove_data.add(data);
                    }
                }
                Session.getSession().allData.removeAll(remove_data);
                //delete category from database
                Session.database.executeQueryWithoutResult("delete from category where (idcategory="+category.idcategory+");");

                //delete category from all category

                Session.getSession().allCategory.remove(category);
            }catch (SQLException e){
                throw new RuntimeException(e.toString());
            }
        }
    }

    public int isData() {
        if (isData){
            return (1);
        }else {
            return (0);
        }
    }
    public boolean getIsData(){
        return isData;
    }

    @Override
    public String toString() {
        return name;
    }
    public static void checkParentRec(Category category,Category parent){
        if (category == null){
            return;
        }
        String tempPath="";
        if (parent != null){
            if (parent.idcategory == 1){

                tempPath = parent.path + parent.idcategory;
            }else {

                tempPath = parent.path + "/" + parent.idcategory;
            }
        }else {
            tempPath = "/" ;
        }
        category.path = tempPath;
        try {
            Session.database.executeQueryWithoutResult("update category set parent="+category.parent.idcategory+", path='"+category.path+"' where (idcategory="+category.idcategory+");");
        } catch (SQLException e) {
            throw new RuntimeException(e.toString());
        }
        for (Category child:Session.session.allCategory
             ) {
            if (child.parent == category){
                Category.checkParentRec(child,category);
            }
        }
    }

}
