package model;

public class Category {
    int idcategory;
    String name;
    Category parent;
    String path;
    boolean isLeaf;

    public Category(String name,Category parent){
        this.name = name;
        if (parent != null){
            this.parent = parent;
        }else {
            this.parent = this;
        }
        int tempId = Session.getSession().getMaxCategoryid();
        tempId++;
        this.idcategory = tempId;
        Session.getSession().setMaxCategoryid(tempId);
        String tempPath="";
        if (parent != null){
                tempPath = parent.path + "/" + parent.idcategory;
            parent.isLeaf = false;
        }else {
            tempPath = "/" ;
        }
        this.path = tempPath;
        this.isLeaf = true;
        //add to data base
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
        Category tempCat = new Category(name,parent);
        Session.getSession().allCategory.add(tempCat);
    }

}
