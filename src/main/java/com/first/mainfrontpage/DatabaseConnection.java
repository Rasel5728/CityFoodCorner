package com.first.mainfrontpage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL= "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER= "root";
    private static final String PASSWORD="binary5728";

    private static Connection connection= null;

    public static Connection getConnection(){
        try {
            if (connection== null || connection.isClosed()){
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection=DriverManager.getConnection(URL,USER,PASSWORD);
                System.out.println("Database Connected");
            }

        }catch (ClassNotFoundException e){
            System.out.println("Driver not found"+e.getMessage());
        }catch (SQLException e){
            System.out.println("Connection failed"+e.getMessage());
        }
        return connection;

    }

    public static void closeConnection(){
        try{
            if (connection!= null && !connection.isClosed()){
                connection.isClosed();
                System.out.println("Connetion closed");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}