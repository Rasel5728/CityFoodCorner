package com.first.mainfrontpage;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class SignUpLoginController {

    @FXML
    private Button resisterBtn;
    @FXML
    private AnchorPane slidePane;

    boolean clicked = false;
    @FXML
    void slide(ActionEvent event) {
        anim();
        changeButtonText();
    }
    void anim(){
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(slidePane);
        transition.setDuration(Duration.millis(250));

        if(clicked==false){
            transition.setByX(380);
        }
        else{
            transition.setByX(-380);
        }
        transition.play();
        clicked = !clicked;
    }
    void changeButtonText(){
        if(clicked==false){
            resisterBtn.setText("Create New Account");
        }
        else{
            resisterBtn.setText("Sign In");
        }
    }
}
