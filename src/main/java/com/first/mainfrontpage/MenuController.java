package com.first.mainfrontpage;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
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

    private static double currentPrice= 0 ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        removeScrollBar(scrollbar);

      //  setToTable("Burger","5","400"); // value works
        try {
            addlist(13);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void addlist(int item) throws IOException {
        for (int i = 0; i < item; i++) {
            int col = i % 2;
            int row = i / 2;
            menuGrid.add(loadList(), col, row);
        }
    }

    AnchorPane loadList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("foodCard.fxml"));
        AnchorPane card = loader.load();
        FoodCardController controller = loader.getController();
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
        currentPrice+=Integer.parseInt(price);
        totalField.setText(Double.toString(currentPrice));
   }

   @FXML
    private void  remove(){
        tablevVew.getItems().clear();
        currentPrice = currentPrice - currentPrice;
        totalField.setText(Double.toString(currentPrice));
        changes.setText("0.0");
        ammountField.clear();
   }

   @FXML
    private void pay(){
        changes.setText(Double.toString(Double.parseDouble(ammountField.getText()) - Double.parseDouble(totalField.getText())));
   }


}