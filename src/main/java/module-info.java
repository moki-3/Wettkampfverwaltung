module org.example.wettkampfverwaltung {
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
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires java.prefs;
    requires java.sql;

    opens org.example.wettkampfverwaltung to javafx.fxml;
    exports org.example.wettkampfverwaltung;
}