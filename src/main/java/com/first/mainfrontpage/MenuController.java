package com.first.mainfrontpage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private ScrollPane scrollbar;
    @FXML
    private GridPane menulist;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            addlist(12);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void addlist(int item) throws IOException {
        menulist.getChildren().clear();
        int column = 0; int row = 0;
        scrollbar.setFitToWidth(true);
        scrollbar.setFitToHeight(false);
        menulist.setMinHeight(600);
        for(int i=0; i<item; i++){
            loadList(column, row);
            column++;
            if(column==2){
                column=0;
                row++;
            }
        }
    }
    void loadList(int col, int row) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("foodCard.fxml"));
        AnchorPane card = loader.load();
        FoodCardController controller = loader.getController();
        menulist.add(card,col,row);
    }
}
