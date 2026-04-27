module com.first.mainfrontpage {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.first.mainfrontpage to javafx.fxml;

    exports com.first.mainfrontpage;
}