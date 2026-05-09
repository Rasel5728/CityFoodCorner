package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FoodCardController implements Initializable {
    @FXML
    private Label foodName;
    @FXML
    private Label foodPrice;
    @FXML
    private Label countFood;
    @FXML
    private ImageView foodImage;

    private MenuController controller;

    //for saving current state controller
    public void setMenuController(MenuController controller){
        this.controller = controller;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        countFood.setText("0");
    }

    public void decrementCount(ActionEvent actionEvent) {
        int x = Integer.parseInt(countFood.getText());
        if(x!=0) x=x-1;
        countFood.setText(Integer.toString(x));
    }

    public void increamentFoodCount(ActionEvent actionEvent) {
        int x = Integer.parseInt(countFood.getText());
        if(controller.getStck(foodName.getText())>x) x++;
        countFood.setText(Integer.toString(x));
    }

    public void setFoodValue(String name, String price, Image image){
        foodName.setText(name);
        foodPrice.setText(price);
        foodImage.setImage(image);
    }

    public void addFood(ActionEvent actionEvent) {
        //okaj
        String name = foodName.getText();
        String count = countFood.getText();
        String price = Double.toString(Double.parseDouble(foodPrice.getText()) * Integer.parseInt(count));
        controller.setToTable(name,count,price);
        countFood.setText("0");
    }

}
