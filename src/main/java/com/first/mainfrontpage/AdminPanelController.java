package com.first.mainfrontpage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private Label totalSalesman, pendingRequest, approved, blocked;
    @FXML private TableView<Salesman> regSalesmanList;

    @Override
    // setup and load table with data -> first enter
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadData();
    }
    // setup fields
    private void setupTable() {
        String[] fields = {"email", "mobile", "joinedOn", "status", "lastLogin"};
        for (int i = 0; i < fields.length; i++) {
            regSalesmanList.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(fields[i]));
        }
    }
    // load label data and table value
    private void loadData() {
        ObservableList<Salesman> list = FXCollections.observableArrayList();

        int total = 0, pending = 0, approvedCount = 0, blockedCount = 0;

        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs = con.prepareStatement("SELECT email, mobile, joined_on, status, last_login FROM users ORDER BY joined_on DESC").executeQuery();

            while (rs.next()) {
                // read each row from database result
                String status = rs.getString("status");
                String lastLogin = rs.getString("last_login");

                if (lastLogin == null) lastLogin = "Never";
                // build Salesman from row data -> add to list
                list.add(new Salesman(
                        rs.getString("email"),
                        rs.getString("mobile"),
                        rs.getString("joined_on"),
                        status, lastLogin)
                );
                // increase counts for label
                total++;
                if (status.equals("pending"))  pending++;
                if (status.equals("approved")) approvedCount++;
                if (status.equals("blocked"))  blockedCount++;
            }
            con.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
        // set data into the table
        regSalesmanList.setItems(list);

        // show counts on label
        totalSalesman.setText(String.valueOf(total));
        pendingRequest.setText(String.valueOf(pending));
        approved.setText(String.valueOf(approvedCount));
        blocked.setText(String.valueOf(blockedCount));
    }

    // access status manage
    private void handleAction(String requiredStatus, String newStatus, String successMessage) {
        Salesman s = regSalesmanList.getSelectionModel().getSelectedItem();

        // no selection warning
        if (s == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a salesman first.");
            return;
        }
        // status mismatch warning
        if (!s.getStatus().equals(requiredStatus)) {
            showAlert(Alert.AlertType.WARNING, "Warning", "This action requires the account status to be: " + requiredStatus);
            return;
        }
        // delete confirmation-> yes
        if (newStatus.equals("delete")) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Are you sure?");
            confirm.setHeaderText(null);
            confirm.setContentText("Permanently delete " + s.getEmail() + "? This cannot be undone");
            // delete confirmation-> no
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            // permanently remove user from database
            if (newStatus.equals("delete")) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE email = ?");
                ps.setString(1, s.getEmail());
                ps.executeUpdate();
            } else {
                // update user status
                PreparedStatement ps = con.prepareStatement("UPDATE users SET status = ? WHERE email = ?");
                ps.setString(1, newStatus);
                ps.setString(2, s.getEmail());
                ps.executeUpdate();
            }
            con.close();
            showAlert(Alert.AlertType.INFORMATION, "Done", s.getEmail() + " " + successMessage);

            // refresh the table and labels
            loadData();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // approve user
    @FXML
    void confirmbtn(ActionEvent event) {
        handleAction("pending", "approved", "has been approved!");
    }
    // block user
    @FXML
    void blockbtn(ActionEvent event) {
        handleAction("approved", "blocked", "has been blocked.");
    }
    // reactivate blocked user
    @FXML
    void rejoinbtn(ActionEvent event) {
        handleAction("blocked", "approved", "is active again!");
    }
    // delete blocked user
    @FXML
    void deletebtn(ActionEvent event) {
        handleAction("blocked", "delete", "has been permanently deleted.");
    }

    // back button
    @FXML
    void goTologinMainpagebtn(ActionEvent event) {
        try {
            new SwitchScene().switchscene(event, "SignUpLogin.fxml");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not go back. " + e.getMessage());
        }
    }
}