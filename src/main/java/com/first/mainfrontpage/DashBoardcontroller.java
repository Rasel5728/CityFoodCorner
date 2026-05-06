package com.first.mainfrontpage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DashBoardcontroller implements Initializable {

    @FXML
    private Label dashboard_totalCustomer;
    @FXML
    private LineChart<String,Number> dashboard_incomeOverview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dashboard_totalCustomer.setText("100");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");

        series.getData().add(new XYChart.Data<>("Jan", 500));
        series.getData().add(new XYChart.Data<>("Feb", 900));
        series.getData().add(new XYChart.Data<>("Mar", 550));
        series.getData().add(new XYChart.Data<>("Apr", 700));
        series.getData().add(new XYChart.Data<>("May", 400));

        dashboard_incomeOverview.getData().add(series);
    }
}

/*Total Orders vs. Sold Products

Total Orders (number of orders placed):
Refers to how many times an order was placed
Each order = one transaction
Example: If a customer buys 1 coffee + 1 cake → that counts as 1 order

Sold Products (number of items sold):
Refers to the total number of individual items sold
Each item is counted separately
Example: 1 coffee + 1 cake → that counts as 2 products sold

In short:
Total Orders = how many times customers placed orders
Sold Products = how many items were sold in total
*/
