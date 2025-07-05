package model;

public class Category {
    int idcategory;
    String name;
    Category parent;
    String path;
    boolean isLeaf;

    public Category(String name,Category parent){
        this.name = name;
        this.parent = parent;
        int tempId = Session.getSession().getMaxCategoryid();
        tempId++;
        this.idcategory = tempId;
        Session.getSession().setMaxCategoryid(tempId);
        String tempPath = parent.path + "/" + parent.idcategory;
        this.path = tempPath;
        this.isLeaf = true;
        parent.isLeaf = false;
    }

}
