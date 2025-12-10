package org.example.wettkampfverwaltung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class StartClass extends Application {
    Wettkampf wf;
    Stage controlStage;
    Stage viewStage;

    @Override
    public void start(Stage stage) throws Exception {
        Label greeting = new Label("Hallo! Bitte wählen sie eine Datei aus");
        Button select = new Button("Datei auswählen");
        Button continueButton = new Button("Select a file to continue");
        continueButton.setDisable(true);

            AtomicReference<ArrayList<FighterPair>> fighterPairs = new AtomicReference<>(new ArrayList<>());

        select.setOnAction(actionEvent -> {
            continueButton.setText("Überprüfen...");
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv")
            );
            fc.setTitle("CSV-Datei auswählen");

            File csv = fc.showOpenDialog(stage);

            if (csv != null) {
                System.out.println("File selected: " + csv.getAbsolutePath());
                ReadFromCSV rfc = new ReadFromCSV();
                fighterPairs.set(rfc.read(csv)); // Hier wird die ArrayList gestzt
                for (FighterPair pair : fighterPairs.get()) {
                    //hier gebe ich die inhalte der fighterpairs arraylist aus
                    System.out.println("\n-------------------------------\n");
                    System.out.println(pair);
                }

                continueButton.setText("Weiter");
                continueButton.setDisable(false);
            } else {
                System.err.println("No File selected");
            }
        });


        continueButton.setOnAction(actionEvent -> {
            wf = new Wettkampf(fighterPairs.get());
            play();
        });

        VBox vbox = new VBox(10, greeting, select, continueButton);

        BorderPane bp = new BorderPane();
        bp.setCenter(vbox);

        Scene scne = new Scene(bp);
        stage.setScene(scne);

        stage.setTitle("Neuer Wettkampf");
        controlStage = stage;
        controlStage.show();
    }





    private void listFights(){


        VBox vbox = new VBox(10, wf.createList());
        Scene scn = new Scene(vbox);
        controlStage.setTitle("Liste aller Kämpfe");

        controlStage.setScene(scn);
    }

    private Scene updateControlView(){

        //Button, mit dem man die viewStage auf fullscreen machen kann
        Button viewStageFullscreen = new Button("On");


        viewStageFullscreen.setOnAction(actionEvent -> {
            if(viewStage.isFullScreen()){
                viewStage.setFullScreen(false);
            }else{
                viewStage.setFullScreen(true);
            }
            viewStageFullscreen.setText(viewStage.isFullScreen() ? "Off" : "On");
        });

        //Button, mit der man die zweite Stage anzeigen kann
        Button showViewStage = new Button("viewStage öffnen");


        showViewStage.setOnAction(actionEvent -> {
            openViewStage();

        });




        //HBox, in der der button zum Fullscreen und ein text dafür ist
        HBox fullscreenbox = new HBox(20, new Label("viewStage Vollbildmodus"), viewStageFullscreen);


        //VBox, die die fullscreenbox, den button zum öffnen der viewStage und die liste der kämpfe anzeigt
        VBox leftControls = new VBox(20, fullscreenbox, showViewStage, wf.createList());

        BorderPane bp = new BorderPane();

        bp.setLeft(leftControls);

        return new Scene(bp);

    }

    public void openViewStage(){
        if(viewStage == null){
            //wenn die stage noch nicht exestiert
            viewStage = new Stage();
            viewStage.setScene(new Scene(new Label("nichts zum anzeien")));
            viewStage.setFullScreenExitHint("");
            viewStage.show();
        }else if(!viewStage.isShowing()){
            viewStage.setScene(new Scene(new Label("nichts zum anzeien")));
            viewStage.show();
        }
    }

    public void play(){
        controlStage.setOnCloseRequest(windowEvent -> {
                    if (viewStage != null && viewStage.isShowing()) {
                        viewStage.close();
                        System.exit(0);
                    }
                });

        controlStage.setScene(updateControlView());



    }

}
