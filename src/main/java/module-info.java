module org.example.ood_cw {
    requires javafx.controls;
    requires javafx.fxml;


    opens Frontend to javafx.fxml;
    exports Frontend;
}