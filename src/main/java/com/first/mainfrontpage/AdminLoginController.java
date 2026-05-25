package com.first.mainfrontpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

public class AdminLoginController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // turn off auto fucus
        Platform.runLater(() -> username.getParent().requestFocus());
    }
    // setting fixed value
    private static final String ADMIN_USERNAME = "cityfood";
    private static final String ADMIN_PASSWORD = "rabbit";
    // close

    @FXML private TextField username;
    @FXML private PasswordField password;


    void showAlert(Alert.AlertType type, String title, String message) {
        Alert al = new Alert(type);
        al.setTitle(title);
        al.setHeaderText(null);
        al.setContentText(message);
        al.showAndWait();
    }

    // admin login logic start
    @FXML
    void enterbtn(ActionEvent event) {

        // taking data from field
        String enteredUsername = username.getText().trim();
        String enteredPassword = password.getText();

        // check empty field
        if (enteredUsername.isEmpty() && enteredPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all of the fields.");
            return;
        }
        if (enteredUsername.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter admin username");
            return;
        }
        if (enteredPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter admin password");
            return;
        }

        // fixed vs taking
        if (enteredUsername.equals(ADMIN_USERNAME) && enteredPassword.equals(ADMIN_PASSWORD)) {
            username.clear();
            password.clear();

            // go to admin panel
            try {
                SwitchScene switchScene = new SwitchScene();
                switchScene.switchscene(event, "AdminPanel.fxml");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not open Admin Panel. " + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    // back btn
    @FXML
    void backtoLoginPage(ActionEvent event) {
        try {
            SwitchScene switchScene = new SwitchScene();
            switchScene.switchscene(event, "SignUpLogin.fxml");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not go back to Login page. " + e.getMessage());
        }
    }
}