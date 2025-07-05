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
        String tempPath = parent.path + "/" + parent.idcategory;
        this.path = tempPath;
        parent.isLeaf = false;
        this.isLeaf = true;
    }

    public void addCategory(String name,Category parent){
        Category tempCat = new Category(name,parent);
        Session.getSession().allCategory.add(tempCat);
    }

}
