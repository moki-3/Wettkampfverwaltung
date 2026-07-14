package org.example.wettkampfverwaltung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Button select = new Button("Select file");
        select.setOnAction(actionEvent -> {
            WettkampfManager sc = new WettkampfManager();
            try {
                sc.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Scene scene = new Scene(select);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
