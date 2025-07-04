module org.touzin8.jchip8 {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;


    opens org.touzin8.jchip8 to javafx.fxml;
    exports org.touzin8.jchip8;
}