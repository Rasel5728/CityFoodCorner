package com.first.mainfrontpage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    //TABLE VIEW
    @FXML private TableView<Product>            inventory_tableView;
    @FXML private TableColumn<Product, String>  inventory_col_productID;
    @FXML private TableColumn<Product, String>  inventory_col_productName;
    @FXML private TableColumn<Product, String>  inventory_col_type;
    @FXML private TableColumn<Product, Integer> inventory_col_stock;
    @FXML private TableColumn<Product, Double>  inventory_col_price;
    @FXML private TableColumn<Product, String>  inventory_col_status;
    @FXML private TableColumn<Product, String>  inventory_col_date;

    //FROM FEILD
    @FXML private AnchorPane       inventory_form;
    @FXML private TextField        inventory_productID;
    @FXML private TextField        inventory_productName;
    @FXML private TextField        inventory_stock;
    @FXML private TextField        inventory_price;
    @FXML private ComboBox<String> inventory_type;
    @FXML private ComboBox<String> inventory_status;
    @FXML private ImageView        inventory_imageView;

    //BUTTOM
    @FXML private Button inventory_addBtn;
    @FXML private Button inventory_updateBtn;
    @FXML private Button inventory_clearBtn;
    @FXML private Button inventory_importBtn;

    private final ObservableList<Product> productList= FXCollections.observableArrayList();
    private Connection connection;

    //INITIALIZE METHOD
    @Override
    public void initialize(URL url, ResourceBundle rb){

        //DATABASE CONNECT
        connection=DatabaseConnection.getConnection();
        //COMBO BOX SHOW
        inventory_type.setItems(FXCollections.observableArrayList("Drink","Meal"));
        inventory_status.setItems(FXCollections.observableArrayList("Available","Out Of Stock"));

        //BIND TABLE COLOUM
        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));

        //SET ITEM IN TABLE
        inventory_tableView.setItems(productList);

        //TABLE DATA CLICK AND FORM FILLUP
        inventory_tableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    if (selected != null) fillForm(selected);
                });

        //LOAD INITIAL PAGE
        loadAllProducts();
    }

    private void loadAllProducts(){
        productList.clear();
        String sql="SELECT * FROM product";
        try(Statement stmt=connection.createStatement();
        ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                productList.add(new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("type"),
                        rs.getInt("stock"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("date")
                ));
            }

        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR,"DataBase Error",e.getMessage());

        }

    }











    @FXML
    void inventoryAddBtn(ActionEvent event) {

    }

    @FXML
    void inventoryUpdateBtn(ActionEvent event) {

    }

    @FXML
    void inventoryClearBtn(ActionEvent event) {

    }

    @FXML
    void inventoryDeleteBtn(ActionEvent event) {

    }

    @FXML
    void inventoryImportBtn(ActionEvent event) {

    }


    //FILLFORM METHOD
    private void fillForm(Product p){
        inventory_productID.setText(p.getProductID());
        inventory_productName.setText(p.getProductName());
        inventory_type.setValue(p.getType());
        inventory_stock.setText(String.valueOf(p.getStock()));
        inventory_price.setText(String.valueOf(p.getPrice()));
        inventory_status.setValue(p.getStatus());
    }

    //ALERT METHOD
    private void showAlert(Alert.AlertType type,String title,String msg){
        Alert alert=new Alert(type);
        //alert.setTitle(title);
    }


}
