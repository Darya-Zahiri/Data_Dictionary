package model;

import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.Objects;

public class Category {
    int idcategory;
    String name;
    Category parent;
    String path;
    boolean isLeaf;
    boolean isData;
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
        if (!parent.isLeaf){
            int tempId = Session.getSession().getMaxCategoryid();
            tempId++;
            Session.getSession().setMaxCategoryid(tempId);
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
            Category tempCat = new Category(tempId,name,parent,tempPath,isLeaf,isData);
            try {
                Session.database.executeQueryWithoutResult("insert into category (idcategory,parent,path,name,isleaf,isdata) values " +
                        "(" + tempCat.idcategory + "," + tempCat.parent.idcategory + ",'" + tempCat.path + "','" + tempCat.name +
                        "'," +tempCat.getLeaf()+ "," +tempCat.isData+ ");");
            }catch (SQLException e){
                System.out.println(e.toString());
            }
            Session.getSession().allCategory.add(tempCat);
        }else {
            throw new IllegalArgumentException("پدر برگ است.");
        }

    }

    public boolean isData() {
        return isData;
    }

    @Override
    public String toString() {
        return name;
    }


}
