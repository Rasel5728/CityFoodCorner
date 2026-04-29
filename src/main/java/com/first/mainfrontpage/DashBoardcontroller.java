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
    private Label customerCount;
    @FXML
    private LineChart<String,Number> incomeOverviewChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        customerCount.setText("100");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");

        series.getData().add(new XYChart.Data<>("Jan", 500));
        series.getData().add(new XYChart.Data<>("Feb", 900));
        series.getData().add(new XYChart.Data<>("Mar", 550));
        series.getData().add(new XYChart.Data<>("Apr", 700));
        series.getData().add(new XYChart.Data<>("May", 400));

        incomeOverviewChart.getData().add(series);
    }
}

/*Total Orders VS Sold Products

Total Orders” আর “Sold Products” এক জিনিস না — কাছাকাছি মনে হলেও আসলে আলাদা মেট্রিক।
Total Orders (মোট অর্ডার):
কতবার অর্ডার প্লেস হয়েছে
একেকটা অর্ডার = একেকটা ট্রানজ্যাকশন
উদাহরণ:
একজন কাস্টমার 1টা কফি + 1টা কেক নিল → এটা 1টা order

Sold Products (বিক্রি হওয়া পণ্য)
মোট কতগুলো আইটেম বিক্রি হয়েছে
এখানে প্রতিটা আইটেম আলাদা করে কাউন্ট হয়
একই উদাহরণ:
1টা কফি + 1টা কেক → 2টা product sold

সংক্ষেপে:
Total Orders = কয়বার অর্ডার হয়েছে
Sold Products = মোট কয়টা জিনিস বিক্রি হয়েছে
 */
