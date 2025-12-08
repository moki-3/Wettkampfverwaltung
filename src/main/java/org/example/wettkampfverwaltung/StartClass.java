package org.example.wettkampfverwaltung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class StartClass extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Label greeting = new Label("Hallo! Bitte wählen sie eine Datei aus");
        Button select = new Button("Datei auswählen");

        select.setOnAction(actionEvent -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv")
            );
            fc.setTitle("CSV-Datei auswählen");

            File csv = fc.showOpenDialog(stage);

            if (csv != null) {
                System.out.println("File selected: " + csv.getAbsolutePath());
                // csv datei hier verarbeite
            } else {
                System.err.println("No File selected");
            }
        });

        VBox vbox = new VBox(10, greeting, select);

        BorderPane bp = new BorderPane();
        bp.setCenter(vbox);

        Scene scne = new Scene(bp);
        stage.setScene(scne);

        stage.setTitle("Neuer Wettkampf");
        stage.show();



    }
}
