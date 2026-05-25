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

public class SignUpLoginController implements Initializable {

    // animation logic start
    @FXML private Button     resisterBtn; // "Create New Account" button
    @FXML private AnchorPane slidePane;

    boolean clicked = false;
    @FXML
    void slide(ActionEvent event) {
        anim();
        changeButtonText();
    }

    void anim() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(slidePane);
        transition.setDuration(Duration.millis(250));

        if (clicked == false) {
            transition.setByX(420);
        } else {
            transition.setByX(-420);
        }
        transition.play();
        clicked = !clicked;
    }

    void changeButtonText() {
        if (clicked == false) {
            resisterBtn.setText("Create New Account");
        } else {
            resisterBtn.setText("Sign In");
        }
    }
    // animation logic close

    // alert helper method
    void showAlert(Alert.AlertType type, String title, String message) {
        Alert al = new Alert(type);
        al.setTitle(title);
        al.setHeaderText(null);
        al.setContentText(message);
        al.showAndWait();
    }
    // helper close
    // turn off auto fucus
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> slidePane.requestFocus());
    }

    // sign up logic start

    @FXML private TextField     TFRemail;
    @FXML private PasswordField PFRpassword;
    @FXML private PasswordField PFRrepassword;
    @FXML private TextField     TFRmobile;

    @FXML
    private void register(ActionEvent event) {
        // taking all data from filled
        String email     = TFRemail.getText().trim();
        String password  = PFRpassword.getText();
        String repassword = PFRrepassword.getText();
        String mobile    = TFRmobile.getText();

        // checking empty filled (total)
        if (email.isEmpty() && password.isEmpty() && repassword.isEmpty() && mobile.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields");
            return;
        }

        // checking empty filled (individually)
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your email");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter a password");
            return;
        }
        if (repassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Re-type your password");
            return;
        }
        if (mobile.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your mobile number");
            return;
        }

        // pass vs re-pass
        if (!password.equals(repassword)) {
            showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match");
            return;
        }

        // checking valid email
        String validEmail = "^[a-z0-9_]+@gmail\\.com$";
        if (!email.matches(validEmail)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid Gmail address");
            return;
        }

        // password exception
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must contain at least 6 characters");
            return;
        }
        String strongPass = ".*[!@#$%&*_?].*";
        if (!password.matches(strongPass)) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must contain at least one special character");
            return;
        }

        // checking valid mobile number
        if (!mobile.matches("^[0-9]{11}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Mobile", "Mobile number must be 11 digits");
            return;
        }

        try { // taking database connection
            Connection ctn = DatabaseConnection.getConnection();

            // checking already existed email
            String chkData = "SELECT email FROM users WHERE email = ?";
            PreparedStatement chkSmt = ctn.prepareStatement(chkData);
            chkSmt.setString(1, email);
            ResultSet rs = chkSmt.executeQuery();

            // exist -> no reg
            if (rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "This email is already registered.");
                ctn.close();
                return;
            }
            // not exist -> reg -> add new user as "pending"
            String inData = "INSERT INTO users (email, password, mobile, status) VALUES (?, ?, ?, 'pending')";
            PreparedStatement inSmt = ctn.prepareStatement(inData);
            inSmt.setString(1, email);
            inSmt.setString(2, password);
            inSmt.setString(3, mobile);
            inSmt.executeUpdate();

            // new account created
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! Please wait for admin approval before logging in.");

            TFRemail.clear();
            PFRpassword.clear();
            PFRrepassword.clear();
            TFRmobile.clear();

            if (clicked == true) {
                anim();
                changeButtonText();
            }
            ctn.close(); // closing database connection

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
    // signup logic close

    // sign in logic start
    @FXML private TextField  TFLemail;
    @FXML private PasswordField PFLpassword;

    @FXML
    public void signIn(ActionEvent event) throws IOException {
        // taking data from fields
        String email = TFLemail.getText().trim();
        String password = PFLpassword.getText();

        // checking empty filled (total)
        if (email.isEmpty() && password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields.");
            return;
        }

        // checking empty filled (individually)
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your email");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter your password");
            return;
        }

        try {// taking database connection
            Connection cnt = DatabaseConnection.getConnection();

            // checking email & password in database
            String chkData = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement chkSmt = cnt.prepareStatement(chkData);
            chkSmt.setString(1, email);
            chkSmt.setString(2, password);
            ResultSet rs = chkSmt.executeQuery();

            if (rs.next()) { // checked -> check status
                String status = rs.getString("status");

                if (status.equals("pending")) {
                    showAlert(Alert.AlertType.WARNING, "Pending Approval", "Please wait for admin approval");
                    cnt.close();
                    return;
                }

                if (status.equals("blocked")) {
                    showAlert(Alert.AlertType.ERROR, "Account Blocked", "Your account has been blocked");
                    cnt.close();
                    return;
                }

                // status == approved
                currentUser.email = email;
                currentUser.userName = email.substring(0, email.indexOf('@'));

                // last_login time update
                String updateLogin = "UPDATE users SET last_login = NOW() WHERE email = ?";
                PreparedStatement updateSmt = cnt.prepareStatement(updateLogin);
                updateSmt.setString(1, email);
                updateSmt.executeUpdate();

                TFLemail.clear();
                PFLpassword.clear();

                cnt.close();// database connection close

                SwitchScene switchScene = new SwitchScene();
                switchScene.switchscene(event, "MainFront.fxml");

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
                cnt.close();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
    // sign in logic close

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

        // taking mobile number
        TextInputDialog mobileDialog = new TextInputDialog();
        mobileDialog.setTitle("Forgot Password");
        mobileDialog.setHeaderText(null);
        mobileDialog.setContentText("Enter your registered mobile number:");
        String mobile = mobileDialog.showAndWait().orElse("").trim();

        if (mobile.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Mobile number field cannot be empty");
            return;
        }

        try {// taking database connection
            Connection cnt = DatabaseConnection.getConnection();

            // check email and mobile im database
            String chkData = "SELECT * FROM users WHERE email = ? AND mobile = ?";
            PreparedStatement chkSmt = cnt.prepareStatement(chkData);
            chkSmt.setString(1, email);
            chkSmt.setString(2, mobile);
            ResultSet rs = chkSmt.executeQuery();
            // not exist -> out
            if (!rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No account found");
                cnt.close();
                return;
            }

            // exist -> take new pass
            TextInputDialog newPassDialog = new TextInputDialog();
            newPassDialog.setTitle("Reset Password");
            newPassDialog.setHeaderText(null);
            newPassDialog.setContentText("Enter your new password:");
            String newPassword = newPassDialog.showAndWait().orElse("").trim();

            if (newPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Password field cannot be empty");
                cnt.close();
                return;
            }

            if (newPassword.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must contain at least 6 characters");
                cnt.close();
                return;
            }
            String strongPass = ".*[!@#$%&*_?].*";
            if (!newPassword.matches(strongPass)) {
                showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must contain at least one special character");
                cnt.close();
                return;
            }
            // update database for new pass
            String updatePass = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement updateSmt = cnt.prepareStatement(updatePass);
            updateSmt.setString(1, newPassword);
            updateSmt.setString(2, email);
            updateSmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Password has been reset successfully! You can now log in with your new password.");

            cnt.close(); // close connection

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. " + e.getMessage());
        }
    }
    // forgot pass logic close

    // Admin btn
    @FXML
    void administratorbtn(ActionEvent event) {
        try {
            SwitchScene switchScene = new SwitchScene();
            switchScene.switchscene(event, "AdminLogin.fxml");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Admin Login page. " + e.getMessage());
        }
    }
}