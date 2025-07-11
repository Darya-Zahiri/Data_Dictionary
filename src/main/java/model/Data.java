package model;

import java.sql.SQLException;

public class Data {
    int idData;
    Category category;
    String name;
    String description;
    Data parent;

    public Data(int idData,Category category,String name,String description,Data parent){
        this.idData = idData;
        this.category = category;
        this.name = name;
        this.description = description;
        this.parent = parent;
    }

    public int getIdData() {
        return idData;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Data getParent() {
        return parent;
    }

    public void setParent(Data parent) {
        this.parent = parent;
    }

    public static void addData(Category category,String name,String description,Data parent){
        if (category.isData){
            int tempId = Session.getSession().maxDataid;
            tempId++;
            Session.getSession().maxDataid = tempId;
            Data data = new Data(tempId,category,name,description,parent);
             try {
                 Session.database.executeQueryWithoutResult("insert into data (iddata,idcategory,name,description,parent) " +
                         "values (" + data.idData + "," + category.idcategory + ",'" + name + "','" + description + "'," +parent.idData+ ");");
                Session.getSession().allData.add(data);
             }catch (SQLException e){
                System.out.println(e.toString());
            }

        }else {

            throw new IllegalArgumentException("category doesnt have data");
        }
    }
    public String toString(){
        return this.name;
    }
}
