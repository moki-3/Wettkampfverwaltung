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
    ArrayList<Verein> vereine = new ArrayList<>();
    ArrayList<FighterPair> allFighterPairs = new ArrayList<>();
    public ArrayList<Verein> getVereine() {
        return vereine;
    }
    public void setVereine(ArrayList<Verein> vereine) {
        this.vereine = vereine;
    }
    private int kampfCounter = 0;


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
            allFighterPairs = wf.getFighterPairs();
            addVereine();
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
        VBox leftControls = new VBox(20, fullscreenbox, showViewStage, createVereinList(),wf.createList());

        BorderPane bp = new BorderPane();


       bp.setLeft(leftControls);

        return new Scene(bp);

    }

    /*
    Macht ein VBox, besthend aus hboxen. In diesen sind der Name und die
    Punkte der vereine

    TODO: CSS KLASSEN!!!

     */
    private VBox createVereinList(){
        VBox vbox = new VBox();
        vbox.setSpacing(20);
        for(Verein v : vereine){
            Label l1 = new Label(v.getName()+ ":");
            Label l2 = new Label(v.getPoints() + "");
            HBox hbox = new HBox(10, l1, l2);
            vbox.getChildren().add(hbox);
        }
        return vbox;
    }

    /*
    öffnet die viewStage wenn sie geschlossen ist oder noch gar nicht exestiert
     */
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

    /*
        Erklärung
            Alle fighterPairs werden durchgelaufen, und jeder neue Name wird in
            die Vereine ArrayList gespeichert.
     */
    private void addVereine(){

        for(FighterPair f : allFighterPairs){
            //System.out.println("\n########################\n" + f.toString());
            boolean add01 = true;
            boolean add02 = true;
            for(Verein v : vereine){
                if(v.getName().equals(f.getVerein01())){
                    add01 = false;
                }
                if(v.getName().equals(f.getVerein02())){
                    add02 = false;
                }
                if(!add01 && !add02) break;
            }
            if (add01) vereine.add(new Verein(f.getVerein01()));
            if (add02) vereine.add(new Verein(f.getVerein02()));
        }


        //Testing purposes
//        System.out.println("\n\n##########################\n\nEs wurden " + vereine.size() +
//                " Vereine gefunden:");
//        for(Verein v : vereine){
//            System.out.println(v.getName());
//        }
    }


    /*

    Ich kann hier dann auch machen, das nur dann das sieht, wenn man es braucht.
    Deshalb muss alles außerhalb der Methode definiert werden. Ich brauch auch eine
    Aufräummehtode, die alles nach dem kampf wieder resetet

     */
    private Label name01;
    private Label verein01;
    private int ippon01 = 0;
    private int wazari01 = 0;
    private int yuko01 = 0;
    private int shido01 = 0;

    private Label name02;
    private Label verein02;
    private int ippon02 = 0;
    private int wazari02 = 0;
    private int yuko02 = 0;
    private int shido02 = 0;

    private HBox fight(FighterPair f){
         name01= new Label(f.getName01());
         verein01= new Label(f.getVerein01());

         name02 = new Label(f.getName02());
         verein02 = new Label(f.getVerein02());

         return new HBox(new Label("Nicht fertig"));

    };

    private void resetFight(){
        this.name01 = null;
        this.verein01 = null;
        this.ippon01 = 0;
        this.wazari01 = 0;
        this.yuko01 = 0;
        this.shido01 = 0;

        this.name02 = null;
        this.verein02 = null;
        this.ippon02 = 0;
        this.wazari02 = 0;
        this.yuko02 = 0;
        this.shido02 = 0;
    }

}





























































/*

    +----+         +----+     +--------+    +---+  +---+   +---+      +----------+  +---+  +---+  +---+  +---+
    |     \       /     |    /  +----+  \   |   | /   /    |   |      |         /   |    \ |   |  |   | /   /
    |   +  \     /  +   |   |  /      \  |  |   |/   /     |   |      +---+    /    |     \|   |  |   |/   /
    |   | \ \   / / |   |   | |        | |  |       |      |   |         /    /     |          |  |       |
    |   |  \ +-+ /  |   |   |  \      /  |  |   |\   \     |   |        /    +---+  |   |\     |  |   |\   \
    |   |   +---+   |   |    \  +----+  /   |   | \   \    |   |  /\   /         |  |   | \    |  |   | \   \
    +---+           +---+     +--------+    +---+  +---+   +---+  \/  +----------+  +---+  +---+  +---+  +---+

 */