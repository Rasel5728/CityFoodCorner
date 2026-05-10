package com.first.mainfrontpage;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private TextField ammountField;
    @FXML
    private Label changes;
    @FXML
    private Label totalField;
    @FXML
    private TableView tablevVew;
    @FXML
    private TableColumn<String[],String> tableColumn;
    @FXML
    private TableColumn<String[],String> quantity;
    @FXML
    private TableColumn<String[],String> price;
    @FXML
    private ScrollPane scrollbar;
    @FXML
    private GridPane menuGrid;
    private int currentFoodCount=-1;
    private static double currentPrice= 0 ;

    private int stock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        removeScrollBar(scrollbar);
        loadAllProducts();
    }
    //food count

    public void addFoodToMenu(String name, String price,byte[]image,int count) throws IOException {
        System.out.println(name+ " "+price);
        int col = count%2;
        int row = count/2;
        menuGrid.add(loadList(name,price,image),col,row);
    }

    AnchorPane loadList(String foodName, String foodPrice, byte[]imageBytes) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("foodCard.fxml"));
        AnchorPane card = loader.load();
        FoodCardController controller = loader.getController();
        controller.setFoodValue(foodName,foodPrice,getImg(imageBytes));
        controller.setMenuController(this); // passing controller to food card for saving current state -_-
        return card;
    }

    void removeScrollBar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    //start
   void indexMaping(){
        tableColumn.setCellValueFactory(nameData->
             new ReadOnlyStringWrapper(nameData.getValue()[0])
        );
        quantity.setCellValueFactory((quantity->new ReadOnlyStringWrapper(quantity.getValue()[1])));
        price.setCellValueFactory(prc -> new ReadOnlyStringWrapper(prc.getValue()[2]));
   }

   public void setToTable(String name, String quantity, String price){
        indexMaping();
        tablevVew.getItems().add(new String[]{name,quantity,price});
        currentPrice+=Double.parseDouble(price);
        totalField.setText(Double.toString(currentPrice));

   }

   @FXML
    private void  remove(){
        tablevVew.getItems().remove(tablevVew.getSelectionModel().getSelectedItem());
        currentPrice = currentPrice - currentPrice;
        totalField.setText(Double.toString(currentPrice));
        changes.setText("0.0");
        ammountField.clear();
   }

   @FXML
    private void pay(){
        updateStock();
        changes.setText(Double.toString(Double.parseDouble(ammountField.getText()) - Double.parseDouble(totalField.getText())));
   }

    private void loadAllProducts(){
       menuGrid.getChildren().clear();
        String sql= "SELECT * FROM products";
        try(Statement stmt=DatabaseConnection.getConnection().createStatement();
            ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                currentFoodCount++;
                System.out.println(rs.getString("product_name"));
                addFoodToMenu(
                        rs.getString("product_name"),
                        String.valueOf(rs.getDouble("price")),
                        rs.getBytes("image"),
                        currentFoodCount
                );
            }
        }catch (SQLException e){
            System.out.println("error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getProductStock(String name) {
        String sql = "SELECT stock FROM products WHERE product_name = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public void updateStock(String name,int n) {
        String sql = "UPDATE products SET stock = ? WHERE product_name = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, n);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }
    }


    public int getStck(String s){
        getProductStock(s);
        return stock;
    }

    Image getImg(byte[]imageBytes){
        Image image = null;
        if (imageBytes != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = new Image(bis);
        }

        // Set image to your ImageView
        ImageView imageView = new ImageView();
        imageView.setFitWidth(187);
        imageView.setFitHeight(92);
        imageView.setPreserveRatio(true);
        if (image != null) {
            imageView.setImage(image);
        }
        return image;
    }

    private void updateStock() {
        for (Object item : tablevVew.getItems()) {
            String[] row = (String[]) item;
            String name     = row[0];
            int quantity    = Integer.parseInt(row[1]);

            int currentStock = getStck(name);
            int newStock     = currentStock - quantity;

            updateStock(name, Math.max(newStock, 0)); // prevent negative stock
        }
    }

    //query
    public void insertSellHistory(String productName, int quantity, double total, String date, String cashier) {
        // Include the 'quantity' parameter in the SQL
        String sql = "INSERT INTO sellHistory (product_name, quantity, total, date, cashier) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, productName);
            pstmt.setInt(2, quantity); // Set the quantity
            pstmt.setDouble(3, total);
            pstmt.setString(4, date);
            pstmt.setString(5, cashier); // The index is now 5

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert successful, rows affected: " + rowsAffected);
            }
        } catch (Exception e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }
    public void processAndSave() {
       String date = LocalDateTime.now().toString(); // current date and time

        for (Object item : tablevVew.getItems()) {
            String[] row = (String[]) item;           // cast Object to String[]
            String productName = row[0];
            String quantity = row[1];
            double price       = Double.parseDouble(row[2]);

            insertSellHistory(productName,Integer.parseInt(quantity),price,date,currentUser.userName);


        }
    }


    @FXML
    private void receipt(){
        processAndSave();
        tablevVew.getItems().clear();
    }
}