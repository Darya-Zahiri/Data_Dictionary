package model;

import java.sql.SQLException;
import java.util.Comparator;

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
                 if (parent != null){

                     Session.database.executeQueryWithoutResult("insert into data (iddata,idcategory,name,description,parent) " +
                             "values (" + data.idData + "," + category.idcategory + ",'" + name + "','" + description + "'," +parent.idData+ ");");
                 }else {

                     Session.database.executeQueryWithoutResult("insert into data (iddata,idcategory,name,description,parent) " +
                             "values (" + data.idData + "," + category.idcategory + ",'" + name + "','" + description + "'," +null+ ");");
                 }
                Session.getSession().allData.add(data);
             }catch (SQLException e){
                System.out.println(e.toString());
            }

        }else {

            throw new IllegalArgumentException("category doesnt have data");
        }
    }
    public static void editData(Category category,String name,String description,Data parent) throws SQLException {
        //in view we have checked the parent for having data
        Data data = Session.getSession().currentData;
        if (category != null){
            data.category = category;
            try {
                Session.database.executeQueryWithoutResult("update data set idcategory="+ data.category.idcategory+" where (iddata="+ data.idData +");");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        data.name = name;
        data.description = description;
        try {
            Session.database.executeQueryWithoutResult("update data set name='"+ name +"', description='"+ description +"' where (iddata="+ data.idData +");");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (parent != null){
            data.parent = parent;
            try {
                Session.database.executeQueryWithoutResult("update data set parent="+ parent.idData +" where (iddata="+ data.idData +");");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }else {
            data.parent = null;
            Session.database.executeQueryWithoutResult("update data set parent="+ null +" where (iddata="+ data.idData +");");

        }

    }
    public String toString(){
        return this.name;
    }
}
