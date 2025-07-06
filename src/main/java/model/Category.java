package model;

import javafx.scene.control.Button;

import java.sql.SQLException;

public class Category {
    int idcategory;
    String name;
    Category parent;
    String path;
    boolean isLeaf;
    public Button button;

    public Category(int idcategory,String name,Category parent,String path,boolean isLeaf){
        this.idcategory = idcategory;
        this.name = name;
        this.parent = parent;
        this.path = path;
        this.isLeaf = isLeaf;
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

    public static void addCategory(String name,Category parent){
        int tempId = Session.getSession().getMaxCategoryid();
        tempId++;
        Session.getSession().setMaxCategoryid(tempId);
        String tempPath="";
        if (parent != null){
            tempPath = parent.path + "/" + parent.idcategory;
            parent.isLeaf = false;
        }else {
            tempPath = "/" ;
        }
        boolean isLeaf = true;
        //add to data base
        Category tempCat = new Category(tempId,name,parent,tempPath,isLeaf);
        try {
            Session.database.executeQueryWithoutResult("insert into category (idcategory,parent,path,name,is leaf) values (" + tempCat.idcategory + "," + tempCat.parent.idcategory + ",'" + tempCat.path + "','" + tempCat.name + "'," +tempCat.getLeaf()+ ");");
        }catch (SQLException e){
            System.out.println(e.toString());
        }
        Session.getSession().allCategory.add(tempCat);
        System.out.println(tempCat.getIdcategory());

    }

}
