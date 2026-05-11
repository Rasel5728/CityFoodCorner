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
            transition.setByX(420);
        }
        else{
            transition.setByX(-420);
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
        // taking data from each field
        String email = TFRemail.getText().trim();
        String password = PFRpassword.getText();
        String question = CBRquestion.getValue();
        String answer = TFRrecovaryAnswer.getText().trim();

        // checking if all fields are filled or not
        if (email.isEmpty() && password.isEmpty() && question == null && answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields");
            return;
        }
        // checking individual fields filled or not
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your email");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter a password");
            return;
        }
        if (question == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Security question is not selected");
            return;
        }
        if (answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your recovery answer");
            return;
        }

        // checking email and password validity
        String validEmail = "^[a-z0-9_]+@gmail\\.com$";
        if (!email.matches(validEmail)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid Gmail address (e.g: example@gmail.com)");
            return;
        }
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must be contain at least 6 character");
            return;
        }
        String strongPass = ".*[!@#$%&*_?].*";
        if (!password.matches(strongPass)) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must be contain at least one special character");
            return;
        }
        // checked
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
        // taking data from each field
        String email = TFLemail.getText().trim();
        String password = PFLpassword.getText();
        currentUser.email = email;
        currentUser.userName = email.substring(0,email.indexOf('@'));

        // checking if all fields are filled or not
        if (email.isEmpty() && password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields.");
            return;
        }
        // checking every field filled or not
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your email");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your password");
            return;
        }
        // checked
        try {// building connection with database
            Connection cnt = DatabaseConnection.getConnection();

            // checking if the email and password are matched or not
            String chkData = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement chkSmt = cnt.prepareStatement(chkData);
            chkSmt.setString(1, email);
            chkSmt.setString(2, password);
            ResultSet rs = chkSmt.executeQuery();

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

        // taking email
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Forgot Password");
        emailDialog.setHeaderText(null);
        emailDialog.setContentText("Enter your registered email:");

        String email = emailDialog.showAndWait().orElse("").trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Email field cannot be empty");
            return;
        }

        try { // building connection with database
            Connection cnt = DatabaseConnection.getConnection();

            // checking if the email matched or not
            String chkData = "SELECT security_question FROM users WHERE email = ?";
            PreparedStatement chkSmt = cnt.prepareStatement(chkData);
            chkSmt.setString(1, email);
            ResultSet rs = chkSmt.executeQuery();
            // no: no access
            if (!rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No account found with this email.");
                cnt.close();
                return;
            }
            // yes: find security question by email
            String securityQuestion = rs.getString("security_question");

            TextInputDialog ansDialog = new TextInputDialog();
            ansDialog.setTitle("Forgot Password");
            ansDialog.setHeaderText(null);
            ansDialog.setContentText(securityQuestion);

            String answer = ansDialog.showAndWait().get();
            // no answer alert
            if (answer.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Answer field cannot be empty.");
                cnt.close();
                return;
            }

            // find pass by ans
            String chkAns = "SELECT password FROM users WHERE security_answer = ?";
            PreparedStatement chkStmt = cnt.prepareStatement(chkAns);
            chkStmt.setString(1, answer);
            ResultSet rs1 = chkStmt.executeQuery();

            if (rs1.next()) {
                String storedPassword = rs1.getString("password");
                showAlert(Alert.AlertType.INFORMATION, "Your Password", "Your password is: " + storedPassword);
            } else {
                showAlert(Alert.AlertType.ERROR, "Wrong Answer", "Security answer does not match.");
            }
            cnt.close();

        } catch (SQLException e) {
            // Database connection issue
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
// forgot password logic close
}
