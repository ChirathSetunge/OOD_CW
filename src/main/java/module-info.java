module org.example.ood_cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires commons.math3;
    requires org.xerial.sqlitejdbc;


    opens Frontend to javafx.fxml;
    exports Frontend;
}