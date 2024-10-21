module org.example.ood_cw {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.ood_cw to javafx.fxml;
    exports org.example.ood_cw;
}