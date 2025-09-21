module com.shwandashop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.apache.pdfbox;

    opens com.shwandashop to javafx.fxml;
    opens com.shwandashop.productHandling to javafx.base;
    exports com.shwandashop;
}
