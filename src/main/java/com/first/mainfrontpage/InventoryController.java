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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    private MenuController menuController;

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

    public void setController(MenuController controller){
        this.menuController = controller;
    }

    //INITIALIZE CLASS
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

    //LOAD METHOD, TO GET DATA FROM DB
    private void loadAllProducts(){
        productList.clear();
        String sql="SELECT * FROM products";
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

    //ADD ADD_BUTTON
    @FXML
    void inventoryAddBtn(ActionEvent event) throws IOException {
        if(!validateInputs()) return;
        String sql = "INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try(PreparedStatement ps=connection.prepareStatement(sql)){
            ps.setString(1, inventory_productID.getText().trim());
            ps.setString(2, inventory_productName.getText().trim());
            ps.setString(3,inventory_type.getValue());
            ps.setInt(4,Integer.parseInt(inventory_stock.getText().trim()));
            ps.setDouble(5,Double.parseDouble(inventory_price.getText().trim()));
            ps.setString(6,inventory_status.getValue());
            ps.setString(7,LocalDate.now().toString());
            ps.setBytes(8, selectedImageBytes); // image bytes here


            ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Success","Product added successfully");
            loadAllProducts();
            clearForm();
        }catch (SQLIntegrityConstraintViolationException e){
            showAlert(Alert.AlertType.WARNING,"Duplicate ID","Product id already exists");
        }catch (Exception e){
            showAlert(Alert.AlertType.ERROR,"DataBase error",e.getMessage());
        }
    }

    //UPDATE NUTTON
    @FXML
    void inventoryUpdateBtn(ActionEvent event) {
        if(inventory_tableView.getSelectionModel().getSelectedItem()==null){
            showAlert(Alert.AlertType.WARNING,"No selection","please select a row to update");
            return;
        }
    if (!validateInputs()) return;
    String sql="UPDATE products SET product_name=?,type=?,stock=?,price=?,status=?,date=? WHERE product_id = ?";

        try(PreparedStatement ps=connection.prepareStatement(sql)){
            ps.setString(1, inventory_productName.getText().trim());
            ps.setString(2,inventory_type.getValue());
            ps.setInt(3,Integer.parseInt(inventory_stock.getText().trim()));
            ps.setDouble(4,Double.parseDouble(inventory_price.getText().trim()));
            ps.setString(5,inventory_status.getValue());
            ps.setString(6,LocalDate.now().toString());
            ps.setString(7, inventory_productID.getText().trim());

            ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Success","Product added successfully");
            loadAllProducts();
            clearForm();
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR,"DataBase error",e.getMessage());
        }
    }

    //ADD CLEAR_BUTTON
    @FXML
    void inventoryClearBtn(ActionEvent event) {
        clearForm();

    }

    //DELETE BUTTON
    @FXML
    void inventoryDeleteBtn(ActionEvent event) {
        Product selected=inventory_tableView.getSelectionModel().getSelectedItem();
        if (selected==null){
            showAlert(Alert.AlertType.WARNING,"No Selecton","Please select a row to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getProductName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle(("Confirm Delete"));
        confirm.showAndWait().ifPresent(btn ->{
            if(btn==ButtonType.YES){
                String sql= "DELETE FROM products WHERE product_id=?";
                try(PreparedStatement ps=connection.prepareStatement(sql)){
                    ps.setString(1,selected.getProductID());
                    ps.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION,"Deleted","Product Deleted");
                    loadAllProducts();
                    clearForm();
                }catch (Exception e){
                    showAlert(Alert.AlertType.ERROR,"DataBase Error",e.getMessage());
                }
            }
        });
    }

    //IMPORT IMAGE METHOD
    private byte[] selectedImageBytes; // class-level field to use when saving to DB
    @FXML
    void inventoryImportBtn(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose image file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file", "*.png", "*.jpg", "*.gif", "*.jpeg"));
        File file = fc.showOpenDialog(inventory_importBtn.getScene().getWindow());

        if (file != null) {
            // Display image in ImageView
            inventory_imageView.setImage(new Image(file.toURI().toString()));

            // Read image as bytes for DB storage
            try {
                selectedImageBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    //CLEAR METHOD
    private void clearForm(){
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_type.setValue(null);
        inventory_stock.clear();
        inventory_price.clear();
        inventory_status.setValue(null);
        inventory_imageView.setImage(null);
        inventory_tableView.getSelectionModel().clearSelection();

    }

    //ALERT METHOD
    private void showAlert(Alert.AlertType type,String title,String msg){
        Alert alert=new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    //ADD VALIDATE INPUT METHOD
    private boolean validateInputs(){
        if (inventory_productID.getText().trim().isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Validation","Product id cannot be empty");
            return false;
        }
        if (inventory_productName.getText().trim().isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Validation","Product name cannot be empty");
            return false;
        }
        if (inventory_type.getValue()==null){
            showAlert(Alert.AlertType.ERROR, "Validation","Pleace choice a type");
            return false;
        }
        if (inventory_status.getValue()==null){
            showAlert(Alert.AlertType.ERROR, "Validation","Pleace choice a status");
            return false;
        }
        try {
            Integer.parseInt(inventory_stock.getText().trim());
        }catch (Exception e){
            showAlert(Alert.AlertType.ERROR, "Validation", "Stock must be a number");
            return false;
        }try {
            Double.parseDouble(inventory_price.getText().trim());
        }catch (Exception e){
            showAlert(Alert.AlertType.ERROR,"Validation", "Price must be a number");
            return false;
        }
        return true;
    }

}
