package model;

import utility.Database;

import java.sql.SQLException;
import java.util.ArrayList;

public class Session {
    public static Session session;
    public int maxCategoryid;
    public Category currentCategory;
    ArrayList<Category> allCategory = new ArrayList<>();
    public static Database database;

    private Session(){
        try {
            database = new Database("127.0.0.1",3306,"pod","root","Aminabadi1!");
            System.out.println("|||connected to database.|||");
        } catch (ClassNotFoundException e) {
            /*throw new RuntimeException(e);*/
            System.out.println(e.toString());
        } catch (SQLException e) {
            /*throw new RuntimeException(e);*/
            System.out.println(e.toString());
        }
    }

    public static Session getSession(){
        if(session == null) {
            session = new Session();

        }
        return session;
    }

    public int getMaxCategoryid(){
        return maxCategoryid;
    }
    public Category getCurrentCategory(){
        return currentCategory;
    }
    public void setMaxCategoryid(int newID){
        maxCategoryid = newID;
    }
    public void setCurrentCategory(Category newCat){
        currentCategory = newCat;
    }

}
