package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

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
        x=x+1;
        countFood.setText(Integer.toString(x));
    }

    public void addFood(ActionEvent actionEvent) {
        //okaj
        if(!Objects.equals(countFood.getText(), "0")) controller.setToTable(foodName.getText(),countFood.getText(),Integer.toString(Integer.parseInt(foodPrice.getText())*Integer.parseInt(countFood.getText())));

    }

}
