package org.example.wettkampfverwaltung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

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
    private int kampfIndex = 0; //akluteller kampf
    private boolean isFight = false;
    ManageView mv;

    Preferences prefs = Preferences.userNodeForPackage(getClass());






    /*
        Diese Methode macht das erste Fenster, bei dem man die .csv File auswählt. Dann wird die
        List aller Fighters erstellt und danach wird die play() methode aufgerufen.
        Dazu wird Wettkampf.java benutzt.
        Hier wird auch mv gesetzt, mit der Länge der allFighterPairs.size() methode.
     */
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
             mv = new ManageView(allFighterPairs.size());
            //System.out.println("allFighters.size() = " + mv.getFightsCount()); passt, es wird richtig übergeben
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

       //test
       bp.setCenter(updateFightControlView(allFighterPairs.get(0)));

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
            viewStage.setFullScreenExitHint("");
            viewStage.show();
        }
        viewStage.show();
        updateViewStage();
    }


    /*
        Diese Methode updatet die ViewStage mit dem Aktuellen Kampf oder mit einem zwischenScreen, der den
        aktuellen Punktestand anzeigt. Hier muss noch angepasst werden, welches FighterPair angezeigt werden
        muss
     */
    public void updateViewStage(){
        if (viewStage != null && viewStage.isShowing()) {
            if(isFight){
                viewStage.setScene(mv.updateView(allFighterPairs.get(0))); // ANPASSEN!!!!!
            }else{
                kampfIndex = 28;
                if(kampfIndex + 1 < allFighterPairs.size()){
                    viewStage.setScene(mv.timeFiller(vereine, allFighterPairs.get(kampfIndex+1)));
                }else{
                    viewStage.setScene(mv.timeFiller(vereine, null));
                }


            }
        }
    }

    private void updateControlStage(){
        controlStage.setScene(updateControlView());

    }

    private void updatePreferences(){
        prefs.putDouble("window.width", controlStage.getWidth());
        prefs.putDouble("window.height", controlStage.getHeight());
        prefs.putDouble("window.x", controlStage.getX());
        prefs.putDouble("window.y", controlStage.getY());
    }

    public void play(){
        controlStage.setOnCloseRequest(windowEvent -> {
                    if (viewStage != null && viewStage.isShowing()) {
                        viewStage.close();
                        System.exit(0);
                    }
                });

        updateControlStage();
        controlStage.setWidth(800);
        controlStage.setHeight(450);
        controlStage.setX(0);
        controlStage.setY(0);
        updatePreferences();
        updateControlStage();



    }



    /*
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

    boolean isFesthalter01 = false;
    boolean isFesthalter02 = false;


    public VBox updateFightControlView(FighterPair f){
        VBox root = new VBox();

        System.out.println("Ippon01: " + f.getIppon01());

        //################################################################################################
        // TOP HBOX START

        Button endFight = new Button("Kampf beenden");
        endFight.setOnAction(actionEvent -> {
            //Kampf beenden
        });


        Button start_stop = new Button(isFight ? "Matte" : "Hajime");
        start_stop.setOnAction(actionEvent -> {
            if(isFight){
                isFight = false;
            }else{
                isFight = true;
            }
            System.out.println("isFight changed to " + isFight);
        });


        HBox topBox = new HBox(endFight);

        // TOP HBOX END
        //################################################################################################
        // UPPER FIGHTER START

        HBox upperFighter = new HBox();

        Label name01 = new Label(f.getName01());
        Label verein01 = new Label(f.getVerein01());

        VBox daten01 = new VBox(10, name01, verein01);

        String points01 = "0";
        if(f.getIppon01() > 0) points01 = "100";
        else {
            int tmp = 50 * f.getWaza_ari01() + f.getYuko01();
            points01 = tmp + "";
        }

        Label points01l = new Label(points01);

        Button editIppon01 = new Button("Ippon [strg + arrow Up + 1] : " + f.getIppon01());
        editIppon01.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Ippon von " + f.getName01() + " ändern");
            Label info = new Label("");
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incIppon01();
                info.setText(f.getName01() + " hat einen Ippon. " + f.getName01() + "Hat jetzt theoretisch gewonnen.");
            });
            dec.setOnAction(actionEvent1 -> {
                f.decIppon01();
                info.setText(f.getName01() + " hat jetzt einen Ippon weniger.");
            });

            HBox buttons = new HBox(dec, inc);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                //updateFightControlView(f);
                updatePreferences();
                System.out.println(controlStage.getWidth() + "\n"+ controlStage.getHeight() + "\n"+ controlStage.getX() + "\n"+ controlStage.getY());
                updateControlStage();
                System.out.println(controlStage.getWidth() + "\n"+ controlStage.getHeight() + "\n"+ controlStage.getX() + "\n"+ controlStage.getY());
            });

            VBox ippon01vBox = new VBox(buttons, info, close);


            Scene ippon01scene = new Scene(ippon01vBox);

            warningStage.setOnCloseRequest(windowEvent -> {
                //updateFightControlView(f);
                updatePreferences();
                System.out.println(controlStage.getWidth() + "\n"+ controlStage.getHeight() + "\n"+ controlStage.getX() + "\n"+ controlStage.getY());
                updateControlStage();
                System.out.println(controlStage.getWidth() + "\n"+ controlStage.getHeight() + "\n"+ controlStage.getX() + "\n"+ controlStage.getY());
            });

            warningStage.setScene(ippon01scene);

            warningStage.requestFocus();

            warningStage.show();


            //update Ippon 01 mit smallStage deren owner controlStage ist
            System.out.println("editIppon01 clicked");
        });
        // strg + arrow up + 1

        Button editWaza_ari01 = new Button("Waza-ari [strg + arrow Up + 2] : " + f.getWaza_ari01());
        editWaza_ari01.setOnAction(actionEvent -> {
            //update Waza_ari01 like ippon
            System.out.println("editWaza_ari01 clicked");
        });
        // strg + arrow up + 2

        Button editYuko01 = new Button("Yuko [strg + arrow uup + 3] : " + f.getYuko01());
        editYuko01.setOnAction(actionEvent -> {
            //update yuko01 like Waza_ari
            System.out.println("editYuko01 clicked");
        });
        // strg + arrow up + 3

        Button editShido01 = new Button("Shido [strg + arrow up + 4] : " + f.getShido01());
        editShido01.setOnAction(actionEvent -> {
            //update shido like Waza_ari and maybe make the screen red to show that somebody is disqualified
            System.out.println("editShido01 clicked");
        });
        // strg + arrow up + 4

        VBox controls01 = new VBox(10, editIppon01, editWaza_ari01, editYuko01, editShido01);

        Button osae_komi01 = new Button(isFesthalter01 ? "Toketa" : "Osae-komi" + "\n[strg + arrow up + 5]");
        osae_komi01.setOnAction(actionEvent -> {
            // action und auch css klassen ändern mit if(osae_komi01.getName().equals...)
            System.out.println("osae_komi01 clicked");
        });
        //

        upperFighter.getChildren().addAll(daten01, points01l, controls01, osae_komi01);

        // UPPER FIGHTER END
        //################################################################################################
        // LOWER FITHER START
        HBox lowerFighter = new HBox();

        Label name02 = new Label(f.getName02());
        Label verein02 = new Label(f.getVerein02());

        VBox daten02 = new VBox(10, name02, verein02);

        String points02 = "0";
        if(f.getIppon02() > 0) points02 = "100";
        else {
            int tmp = 50 * f.getWaza_ari02() + f.getYuko02();
            points02 = tmp + "";
        }

        Label points02l = new Label(points02);

        Button editIppon02 = new Button("Ippon [strg + arrow Up + 1] : " + f.getIppon02());
        editIppon02.setOnAction(actionEvent -> {
            //update Ippon 02 mit smallStage deren owner controlStage ist
            System.out.println("editIppon02 clicked");
        });
        // strg + arrow up + 1

        Button editWaza_ari02 = new Button("Waza-ari [strg + arrow Up + 2] : " + f.getWaza_ari02());
        editWaza_ari02.setOnAction(actionEvent -> {
            //update Waza_ari02 like ippon
            System.out.println("editWaza-ari02 clicked");
        });
        // strg + arrow up + 2

        Button editYuko02 = new Button("Yuko [strg + arrow uup + 3] : " + f.getYuko02());
        editYuko02.setOnAction(actionEvent -> {
            //update yuko02 like Waza_ari
            System.out.println("editYuko02 clicked");
        });
        // strg + arrow up + 3

        Button editShido02 = new Button("Shido [strg + arrow up + 4] : " + f.getShido02());
        editShido02.setOnAction(actionEvent -> {
            //update shido like Waza_ari and maybe make the screen red to show that somebody is disqualified
            System.out.println("editShido02 clicked");
        });
        // strg + arrow up + 4

        VBox controls02 = new VBox(10, editIppon02, editWaza_ari02, editYuko02, editShido02);

        Button osae_komi02 = new Button(isFesthalter02 ? "Toketa" : "Oase-komi" + "\n[strg + arrow up + 5]");
        osae_komi02.setOnAction(actionEvent -> {
            // action und auch css klassen ändern mit if(osae_komi02.getName().equals...)
            System.out.println("osae_komi02 clicked");
        });
        //

        lowerFighter.getChildren().addAll(daten02, points02l, controls02, osae_komi02);

        // LOWER FITHER END
        //################################################################################################
        // TIMERS START
        HBox timers = new HBox();

        // TIMERS END
        //################################################################################################
        root.getChildren().addAll(topBox, upperFighter, lowerFighter, timers);

        return root;
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