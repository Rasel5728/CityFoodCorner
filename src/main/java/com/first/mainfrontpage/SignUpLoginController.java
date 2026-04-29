package com.first.mainfrontpage;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

public class SignUpLoginController {

    // animation logic start
    @FXML
    private Button resisterBtn; //Create New Account
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
            transition.setByX(392);
        }
        else{
            transition.setByX(-392);
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
    // animation logic close

    // sign up page logic start
    @FXML
    private TextField TFRemail;

    @FXML
    private PasswordField PFRpassword;

    @FXML
    private ComboBox<?> CBRquestion;

    @FXML
    private TextField TFRrecovaryAnswer;

    public void register(ActionEvent event){

    }

    // sign up page logic close


    // sign in page logic start
    @FXML
    private TextField TFLemail;

    @FXML
    private Hyperlink HLLforgotPasswrd;

    @FXML
    private PasswordField PFLpassword;

    public void signIn(ActionEvent event) throws IOException {
        SwitchScene switchScene = new SwitchScene();
        switchScene.switchscene(event, "MainFront.fxml");
    }

    // sign up page logic close

}
