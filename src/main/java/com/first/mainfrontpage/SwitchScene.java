package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SwitchScene {
    public void switchscene(ActionEvent e, String path) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow(); //usages current stage rather than creating another
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}