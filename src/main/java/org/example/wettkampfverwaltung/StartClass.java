package org.example.wettkampfverwaltung;

import com.fasterxml.jackson.databind.node.ValueNode;
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
    private boolean isFight = false;


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

    private HBox updateFightControlView(FighterPair f){

        if(!isFight){
            return new HBox(new Label("please select a fight"));
        }


        /*

         Ich mache jeden button einzeln, weil ich dann auf die mit Tastenkombination
         effiezient zugreifen kann
         Das ganze kann auch effiezienter gemacht werden, aber ich will jetzt einmal
         ein laufendes programm schaffen.

          */

        //################################################################################################

        // LEFT SIDE START

        name01= new Label(f.getName01());
         verein01= new Label(f.getVerein01());



        // BUTTONS LEFT START

         Button incIppon01 = new Button("+");
         incIppon01.setOnAction(actionEvent -> {
             // MEHTODE AUFRUFEN DIE WARNUNG MACHT
             /*
             if(ippon01+1 >= 2)
              */
             this.ippon01++;
             //methode aufrufen, die die szenen updated
         });
         Button decIppon01 = new Button("-");
         decIppon01.setOnAction(actionEvent -> {
             if(this.ippon01-- == 0) this.ippon01--; // verringert ippon01 nur wenn es dann gleich 0 ist, sonst würde da -1 stehen
             //methode aufrufen, die die szenen updated
         });

        //----------------------------------------------------------------

        Button incWazari01 = new Button("+");
         incWazari01.setOnAction(actionEvent -> {
             // MEHTODE AUFRUFEN DIE WARNUNG MACHT
             /*
             if(wazari01+1 > 1) // warnung weil es dann sonst einen Ippon geben würde
              */
             this.wazari01++;
             //methode aufrufen, die die szenen updated
         });
         Button decWazari01 = new Button("-");
         decWazari01.setOnAction(actionEvent -> {
             if(this.wazari01-- >= 0) this.wazari01--; // gleiches prinzip wie ippon01
         });

        //----------------------------------------------------------------

        Button incYuko01 = new Button("+");
        incYuko01.setOnAction(actionEvent -> {
            /*
                Fragen, wie viele Yukos es gibt
             */
            this.yuko01++;
        });
        Button decYuko01 = new Button("-");
        decYuko01.setOnAction(actionEvent -> {
            if(this.yuko01-- >= 0) this.yuko01--;
        });

        //----------------------------------------------------------------

        Button incShido01 = new Button("+");
        incShido01.setOnAction(actionEvent -> {
            if(this.shido01++ < 3){
                this.shido01++;
            }else{
                //mehtode aufrufen, die shido vergibt und davor fragt, ob das passt
            }
        });
        Button decShido01 = new Button("-");
        decShido01.setOnAction(actionEvent -> {
            if(this.shido01-- >= 0) this.shido01--;
        });

        // BUTTONS LEFT END

        //------------------------------------------------------------------------------------------------

        // HBOXEs LEFT START

        HBox leftIppon = new HBox(10, new Label("Ippon"), decIppon01, new Label(ippon01 + ""),
                incIppon01);
        HBox leftWazari = new HBox(10, new Label("Wazari"), decWazari01, new Label(wazari01 + ""),
                incWazari01);
        HBox leftYuko = new HBox(10, new Label("Yuko"), decYuko01, new Label(yuko01 + ""),
                incYuko01);
        HBox leftShido = new HBox(10, new Label("Shido"), decShido01, new Label(shido01 + ""),
                incShido01);

        // HBOXEs LEFT END

        //------------------------------------------------------------------------------------------------

        // LEFT HBOX START

         VBox leftFighter = new VBox(10, name01, verein01, leftIppon, leftWazari, leftYuko, leftShido);

        // LEFT HBOX END

        // LEFT SIDE END

        //################################################################################################

        // RIGHT SIDE START

         name02 = new Label(f.getName02());
         verein02 = new Label(f.getVerein02());

        // BUTTONS LEFT START

        Button incIppon02 = new Button("+");
        incIppon02.setOnAction(actionEvent -> {
            // MEHTODE AUFRUFEN DIE WARNUNG MACHT
             /*
             if(ippon02+1 >= 2)
              */
            this.ippon02++;
            //methode aufrufen, die die szenen updated
        });
        Button decIppon02 = new Button("-");
        decIppon02.setOnAction(actionEvent -> {
            if(this.ippon02-- == 0) this.ippon02--; // verringert ippon02 nur wenn es dann gleich 0 ist, sonst würde da -1 stehen
            //methode aufrufen, die die szenen updated
        });

        //----------------------------------------------------------------

        Button incWazari02 = new Button("+");
        incWazari02.setOnAction(actionEvent -> {
            // MEHTODE AUFRUFEN DIE WARNUNG MACHT
             /*
             if(wazari02+1 > 1) // warnung weil es dann sonst einen Ippon geben würde
              */
            this.wazari02++;
            //methode aufrufen, die die szenen updated
        });
        Button decWazari02 = new Button("-");
        decWazari02.setOnAction(actionEvent -> {
            if(this.wazari02-- >= 0) this.wazari02--; // gleiches prinzip wie ippon02
        });

        //----------------------------------------------------------------

        Button incYuko02 = new Button("+");
        incYuko02.setOnAction(actionEvent -> {
            /*
                Fragen, wie viele Yukos es gibt
             */
            this.yuko02++;
        });
        Button decYuko02 = new Button("-");
        decYuko02.setOnAction(actionEvent -> {
            if(this.yuko02-- >= 0) this.yuko02--;
        });

        //----------------------------------------------------------------

        Button incShido02 = new Button("+");
        incShido02.setOnAction(actionEvent -> {
            if(this.shido02++ < 3){
                this.shido02++;
            }else{
                //mehtode aufrufen, die shido vergibt und davor fragt, ob das passt
            }
        });
        Button decShido02 = new Button("-");
        decShido02.setOnAction(actionEvent -> {
            if(this.shido02-- >= 0) this.shido02--;
        });

        // BUTTONS LEFT END

        //------------------------------------------------------------------------------------------------

        // HBOXEs LEFT START

        HBox rightIppon = new HBox(10, new Label("Ippon"), decIppon02, new Label(ippon02 + ""),
                incIppon02);
        HBox rightWazari = new HBox(10, new Label("Wazari"), decWazari02, new Label(wazari02 + ""),
                incWazari02);
        HBox rightYuko = new HBox(10, new Label("Yuko"), decYuko02, new Label(yuko02 + ""),
                incYuko02);
        HBox rightShido = new HBox(10, new Label("Shido"), decShido02, new Label(shido02 + ""),
                incShido02);

        // HBOXEs LEFT END

        //------------------------------------------------------------------------------------------------

        // LEFT HBOX START

        /*
        Diese VBox muss ich auch rechtsbündig machen, vllt geht das mit css
         */

        VBox rightFighter = new VBox(10, name02, verein02, rightIppon, rightWazari, rightYuko, rightShido);

        // LEFT HBOX END

        // LEFT SIDE END


















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


    // Datentyp der Methode anpassen
    public void updateViewStage(){

        if(!isFight){
            VBox aktuellerStand = new VBox(new Label("Aktueller PunkteStand"));
            aktuellerStand.setSpacing(10);
            for(Verein v : vereine){
                Label tmp = new Label(v.getName() + ": " + v.getPoints());
                aktuellerStand.getChildren().add(tmp);
            }
            return; // hier muss ich die VBox zurückgeben oder es in was anderes umwandel, wenn ich was andere brauche
        }


        VBox leftData = new VBox( name01, verein01);
        if(ippon01 > 0) leftData.getChildren().add(new Label("Ippon: " + ippon01));
        if(wazari01 > 0) leftData.getChildren().add(new Label("Wazari: " + wazari01));
        if(yuko01 > 0) leftData.getChildren().add(new Label("Yuko: " + yuko01));
        if(shido01 > 0) leftData.getChildren().add(new Label("Shido: " + shido01));

        VBox rightData = new VBox(name02, verein02);
        if(ippon02 > 0) rightData.getChildren().add(new Label("Ippon: " + ippon02));
        if(wazari02 > 0) rightData.getChildren().add(new Label("Wazari: " + wazari02));
        if(yuko02 > 0) rightData.getChildren().add(new Label("Yuko: " + yuko02));
        if(shido02 > 0) rightData.getChildren().add(new Label("Shido: " + shido02));

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