package com.first.mainfrontpage;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpLoginController implements Initializable{

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

    // alert showing method start
    void showAlert(Alert.AlertType type, String title, String message) {
        Alert al = new Alert(type);
        al.setTitle(title);
        al.setHeaderText(null);
        al.setContentText(message);
        al.showAndWait();
    }
    // alert showing method close

    // sign up page logic start
    @FXML
    private TextField TFRemail;
    @FXML
    private PasswordField PFRpassword;
    @FXML
    private ComboBox<String> CBRquestion;
    @FXML
    private TextField TFRrecovaryAnswer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // adding comment box question
        CBRquestion.getItems().add("What is your favorite food?");
        CBRquestion.getItems().add("What is your laptop brand name?");
        CBRquestion.getItems().add("What is your mobile brand name?");
        CBRquestion.getItems().add("What is your least favorite food?");

        Platform.runLater(() -> slidePane.requestFocus());
    }

    @FXML
    private void register(ActionEvent event){
        // taking data for each field
        String email = TFRemail.getText();
        String password = PFRpassword.getText();
        String question = CBRquestion.getValue();
        String answer = TFRrecovaryAnswer.getText();

        // checking every field filled or not
        if (email.isEmpty() || password.isEmpty() || answer.isEmpty() || question== null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields.");
            return;
        }
        // checking if the password is less than 4 characters
        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Password must be at least 4 characters.");
            return;
        }

        try{ // building connection with database
            Connection ctn = DatabaseConnection.getConnection();

            // checking if the email is already registered or not
            String chkData = "SELECT email FROM users WHERE email = ?";
            PreparedStatement chkSmt = ctn.prepareStatement(chkData);
            chkSmt.setString(1, email);
            ResultSet rs = chkSmt.executeQuery();

            // yes: stop registration process
            if (rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "This email is already registered.");
                ctn.close();
                return;
            }

            // no: add new data into database
            String inData = "INSERT INTO users (email, password, security_question, security_answer) " + "VALUES (?, ?, ?, ?)";
            PreparedStatement inSmt = ctn.prepareStatement(inData);
            inSmt.setString(1, email);
            inSmt.setString(2, password);
            inSmt.setString(3, question);
            inSmt.setString(4, answer);
            inSmt.executeUpdate();

            // account successfully created info
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! Please log in.");

            // clearing all field for new registration
            TFRemail.clear();
            PFRpassword.clear();
            CBRquestion.setValue(null);
            TFRrecovaryAnswer.clear();

            // slide pane animation for sign_in
            if (clicked == true) {
                anim();
                changeButtonText();
            }
            ctn.close();

        }catch (SQLException e) {
            // Database connection issue
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
    // sign up page logic close


    // sign in page logic start
    @FXML
    private TextField TFLemail;
    @FXML
    private PasswordField PFLpassword;

    public void signIn(ActionEvent event) throws IOException {
        // taking data for each field
        String email = TFLemail.getText();
        String password = PFLpassword.getText();

        // checking every field filled or not
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter your email and password.");
            return;
        }

        try {// building connection with database
            Connection cnt = DatabaseConnection.getConnection();

            // checking if the email is already registered or not
            String loginSQL = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = cnt.prepareStatement(loginSQL);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            // yes: go to main page
            if (rs.next()) {
                // clearing all field from log_in
                TFLemail.clear();
                PFLpassword.clear();
                cnt.close();

                SwitchScene switchScene = new SwitchScene();
                switchScene.switchscene(event, "MainFront.fxml");

            } else {
                // no: login failed
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
                cnt.close();
            }
        } catch (SQLException e) {
            // Database connection issue
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
    // sign in page logic close


    // forgot password logic start
    @FXML
    void forgotPassword(ActionEvent event) {
    }
    // forgot password logic close
}
