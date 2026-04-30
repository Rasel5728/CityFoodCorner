package com.first.mainfrontpage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public VBox leftScrool;
    public VBox rightScrool;
    @FXML
    private ScrollPane scrollbar;
    @FXML
    private GridPane menulist;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       removeScrollBar(scrollbar);
        try {
            addlist(13);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void addlist(int item) throws IOException {
        for(int i=0; i<item; i++){
            if(i%2 != 0){
                rightScrool.getChildren().add(loadList());
            }
            else {
                leftScrool.getChildren().add(loadList());
            }
        }

    }
    AnchorPane loadList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("foodCard.fxml"));
        AnchorPane card = loader.load();
        FoodCardController controller = loader.getController();
        return card;
    }

    void removeScrollBar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

}
