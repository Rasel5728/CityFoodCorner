package com.first.mainfrontpage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashBoardcontroller implements Initializable {

    // top cards
    @FXML private Label dashboard_totalCustomer;
    @FXML private Label dashboard_todayIncome;
    @FXML private Label dashboard_totalIncome;
    @FXML private Label dashboard_solditems;

    // charts
    @FXML private LineChart<String, Number> dashboard_incomeOverview;
    @FXML private BarChart<String, Number>  dashboard_customerOverview;

    // quick summary
    @FXML private Label dashboard_totalOrder;
    @FXML private Label dashboard_menuItems;
    @FXML private Label dashboard_inventoryItems;

    // top 3 selling items
    @FXML private Label     topFoodNameOne;
    @FXML private Label     topFoodNameTwo;
    @FXML private Label     topFoodNameThree;
    @FXML private ImageView pictopOne;
    @FXML private ImageView pictopTwo;
    @FXML private ImageView pictopThree;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = DatabaseConnection.getConnection();

        loadTotalCustomers();
        loadTodayIncome();
        loadTotalIncome();
        loadSoldItems();
        loadTotalOrders();
        loadMenuItems();
        loadInventoryItems();
        loadIncomeOverviewChart();
        loadCustomerOverviewChart();
        loadTop3SellingItems();
    }

    // total customer count - each unique payment session = one customer
    private void loadTotalCustomers() {
        String sql = "SELECT COUNT(DISTINCT date) FROM sellHistory";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_totalCustomer.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            dashboard_totalCustomer.setText("0");
            System.out.println("loadTotalCustomers error: " + e.getMessage());
        }
    }

    // today income using date LIKE match
    private void loadTodayIncome() {
        String today = java.time.LocalDate.now().toString();
        String sql = "SELECT SUM(total) FROM sellHistory WHERE date LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, today + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dashboard_todayIncome.setText(String.format("%.0f", rs.getDouble(1)));
            }
        } catch (SQLException e) {
            dashboard_todayIncome.setText("0");
            System.out.println("loadTodayIncome error: " + e.getMessage());
        }
    }

    // sum of all income
    private void loadTotalIncome() {
        String sql = "SELECT SUM(total) FROM sellHistory";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_totalIncome.setText(String.format("%.0f", rs.getDouble(1)));
            }
        } catch (SQLException e) {
            dashboard_totalIncome.setText("0");
            System.out.println("loadTotalIncome error: " + e.getMessage());
        }
    }

    // sum of all sold quantity
    private void loadSoldItems() {
        String sql = "SELECT SUM(quantity) FROM sellHistory";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_solditems.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            dashboard_solditems.setText("0");
            System.out.println("loadSoldItems error: " + e.getMessage());
        }
    }

    // total row count in sell history
    private void loadTotalOrders() {
        String sql = "SELECT COUNT(*) FROM sellHistory";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_totalOrder.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            dashboard_totalOrder.setText("0");
            System.out.println("loadTotalOrders error: " + e.getMessage());
        }
    }

    // count of products in menu
    private void loadMenuItems() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_menuItems.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            dashboard_menuItems.setText("0");
            System.out.println("loadMenuItems error: " + e.getMessage());
        }
    }

    // sum of all stock in inventory
    private void loadInventoryItems() {
        String sql = "SELECT SUM(stock) FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dashboard_inventoryItems.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            dashboard_inventoryItems.setText("0");
            System.out.println("loadInventoryItems error: " + e.getMessage());
        }
    }

    // monthly income data for line chart
    private void loadIncomeOverviewChart() {
        String sql = "SELECT SUBSTRING(date,6,2) AS month, SUM(total) AS income " +
                "FROM sellHistory " +
                "GROUP BY SUBSTRING(date,6,2) " +
                "ORDER BY month";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        getMonthName(rs.getString("month")),
                        rs.getDouble("income")
                ));
            }
        } catch (SQLException e) {
            System.out.println("loadIncomeOverviewChart error: " + e.getMessage());
        }

        dashboard_incomeOverview.getData().clear();
        dashboard_incomeOverview.getData().add(series);
    }

    // monthly order count for bar chart
    private void loadCustomerOverviewChart() {
        String sql = "SELECT SUBSTRING(date,6,2) AS month, COUNT(DISTINCT date) AS orders " +
                "FROM sellHistory " +
                "GROUP BY SUBSTRING(date,6,2) " +
                "ORDER BY month";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Customers");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        getMonthName(rs.getString("month")),
                        rs.getInt("orders")
                ));
            }
        } catch (SQLException e) {
            System.out.println("loadCustomerOverviewChart error: " + e.getMessage());
        }

        dashboard_customerOverview.getData().clear();
        dashboard_customerOverview.getData().add(series);
    }

    // top 3 most sold products with image
    private void loadTop3SellingItems() {
        String sql = "SELECT p.product_name, SUM(s.quantity) AS total_qty, p.image " +
                "FROM sellHistory s " +
                "JOIN products p ON s.product_name = p.product_name " +
                "GROUP BY p.product_name, p.image " +
                "ORDER BY total_qty DESC " +
                "LIMIT 3";

        Label[]     nameLabels = {topFoodNameOne, topFoodNameTwo, topFoodNameThree};
        ImageView[] imageViews = {pictopOne, pictopTwo, pictopThree};

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 0;
            while (rs.next() && i < 3) {
                nameLabels[i].setText(rs.getString("product_name"));
                byte[] imgBytes = rs.getBytes("image");
                if (imgBytes != null) {
                    imageViews[i].setImage(new Image(new ByteArrayInputStream(imgBytes)));
                }
                i++;
            }
        } catch (SQLException e) {
            System.out.println("loadTop3SellingItems error: " + e.getMessage());
        }
    }

    // converting month number to month name
    private String getMonthName(String monthNum) {
        switch (monthNum) {
            case "01": return "Jan";
            case "02": return "Feb";
            case "03": return "Mar";
            case "04": return "Apr";
            case "05": return "May";
            case "06": return "Jun";
            case "07": return "Jul";
            case "08": return "Aug";
            case "09": return "Sep";
            case "10": return "Oct";
            case "11": return "Nov";
            case "12": return "Dec";
            default:   return monthNum;
        }
    }
}