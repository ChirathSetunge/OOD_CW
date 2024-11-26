module org.example.ood_cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;


    opens Frontend to javafx.fxml;
    exports Frontend;
}