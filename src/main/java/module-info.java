module com.first.mainfrontpage {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires mysql.connector.j;


    opens com.first.mainfrontpage to javafx.fxml;

    exports com.first.mainfrontpage;
}