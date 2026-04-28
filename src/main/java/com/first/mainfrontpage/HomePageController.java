package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    void customers(ActionEvent event) throws IOException {
        loadingContent("Customer.fxml");
    }

    @FXML
    void dashBoard(ActionEvent event) throws IOException {
        loadingContent("dashBoard.fxml");
    }

    @FXML
    void inventory(ActionEvent event) throws IOException {
        loadingContent("inventory.fxml");
    }

    @FXML
    void menu(ActionEvent event) throws IOException {
        loadingContent("Menu.fxml");
    }
    private void loadingContent (String fxmlPath) throws IOException {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(pane);

            AnchorPane.setBottomAnchor(pane,0.0);
            AnchorPane.setTopAnchor(pane,0.0);
            AnchorPane.setLeftAnchor(pane,0.0);
            AnchorPane.setRightAnchor(pane,0.0);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadingContent("dashBoard.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
