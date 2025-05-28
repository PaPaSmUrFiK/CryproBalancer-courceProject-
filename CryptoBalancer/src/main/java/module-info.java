module com.bsuir.cryptobalancer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires java.desktop;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;  // Исправлено имя модуля для ooxml
    requires org.jfree.jfreechart;  // Основной модуль JFreeChart


    opens cryptoBalancer to javafx.fxml;
    exports cryptoBalancer;
    exports cryptoBalancer.Enums;
    opens cryptoBalancer.Models.Entities to com.google.gson, java.base, javafx.fxml, javafx.base;
    opens cryptoBalancer.Models.TCP to com.google.gson;

}