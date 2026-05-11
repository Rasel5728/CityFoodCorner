package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private Label user;

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

    //sign out button logic
    @FXML
    private void signout(ActionEvent event) throws IOException {
        SwitchScene switchScene = new SwitchScene();
        switchScene.switchscene(event,"SignupLogin.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user.setText(currentUser.userName);
        FontIcon icon = new FontIcon("fa-sign-out");
        icon.setIconSize(12);
        logoutButton.setGraphic(icon);

        try {
            loadingContent("dashBoard.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
