package com.first.mainfrontpage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Customercontroller implements Initializable {

    @FXML
    private TableColumn<Customer, String> customers_col_cashier;

    @FXML
    private TableColumn<Customer, String> customers_col_customerProdutName;

    @FXML
    private TableColumn<Customer, String> customers_col_date;

    @FXML
    private TableColumn<Customer, Double> customers_col_total;

    @FXML
    private TableView<Customer> customers_tableView;

    public ObservableList<Customer>customerList=FXCollections.observableArrayList();
    private Connection connection;

    //CustomerController access any place
    private static Customercontroller instance;
    public static Customercontroller getInstance(){
        return instance;
    }

    //Add initialize method
    @Override
    public void initialize(URL url,ResourceBundle resourceBundle){
        instance=this;

        //Add connection with database
        connection=DatabaseConnection.getConnection();

        //Bind table coloum
        customers_col_customerProdutName.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customers_col_total.setCellValueFactory(new PropertyValueFactory<>("total"));
        customers_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customers_col_cashier.setCellValueFactory(new PropertyValueFactory<>("cashier"));
        loadCustomerData();
    }

    //Create customer load method(Load data from database)
    public void loadCustomerData(){
        customerList.clear();
        String sql="SELECT * FROM sellHistory";

        try(Statement stmt=connection.createStatement();
        ResultSet rs=stmt.executeQuery(sql)){
            while (rs.next()){
                customerList.add(new Customer(
                        rs.getString("product_name"),
                        rs.getDouble("total"),
                        rs.getString("date"),
                        rs.getString("cashier")
                ));
            }
            customers_tableView.setItems(customerList);
            System.out.println("Refresh,total customer:"+customerList.size());
        }catch (Exception e){
            System.out.println("Load failed"+e.getMessage());
        }
    }

    //check
    public void dummy(String name, int total, String date, String cashier){
        customerList.add(new Customer(name,total,date,cashier));
    }
    //Create Table Refresh table
    public void refreshTable(){
        loadCustomerData();
    }
}
