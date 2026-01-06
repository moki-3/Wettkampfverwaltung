package org.example.wettkampfverwaltung;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class StartClass extends Application {
    Wettkampf wf;
    Stage controlStage;
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

    private BorderPane controlRoot;


    private BorderPane fightPane;

    private static final int U10_TIME = 120;
    private static final int U12_TIME = 120;
    private Timeline fighTime;
    private Label timerLabel = new Label("no time yet");
    private int remainingtime = -1;







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

        controlRoot = new BorderPane();
        controlRoot.setCenter(vbox);

        Scene controlScene = new Scene(controlRoot);
        stage.setScene(controlScene);






        stage.setTitle("Neuer Wettkampf");
        controlStage = stage;
        controlStage.show();
    }


    private void buildLeftControlPane(){
        //Button für viewstage fullscreen
        Button viewStageFullscreen = new Button(mv.isViewStageFullscreen() ? "On" : "Off");
        //css klassen und name


        viewStageFullscreen.setOnAction(actionEvent -> {
            int fullScreenViewStage = mv.toggleViewStageFullscreen();
            if(fullScreenViewStage == 1){
                viewStageFullscreen.setText(mv.isViewStageFullscreen() ? "On" : "Off");
            }else{
                viewStageFullscreen.setText("ViewStage ist nicht sichtbar");
            }
        });
        viewStageFullscreen.setFocusTraversable(false);

        Button showViewStage = new Button("ViewStage öffnen");
        showViewStage.setFocusTraversable(false);
        showViewStage.setOnAction(actionEvent -> {
            openViewStage();
            viewStageFullscreen.setText(mv.isViewStageFullscreen() ? "On" : "Off");
        });

        HBox fullscreenbox = new HBox(20, new Label("viewStage Vollbildmodus"), viewStageFullscreen);
        VBox leftControls = new VBox(20, fullscreenbox, showViewStage, createVereinList(),wf.createList());
        controlRoot.setLeft(leftControls);

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
    public void openViewStage() {
       mv.openViewStage();

       updateViewStage();
    }


    /*
        Diese Methode updatet die ViewStage mit dem Aktuellen Kampf oder mit einem zwischenScreen, der den
        aktuellen Punktestand anzeigt. Hier muss noch angepasst werden, welches FighterPair angezeigt werden
        muss
     */
    public void updateViewStage(){
        if(isFight){
            //mv.updateFight(allFighterPairs.get(kampfIndex));
            if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10") || allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U20")) mv.updateTimeLabel(formatTime(U10_TIME));

            mv.newFight(allFighterPairs.get(kampfIndex), kampfIndex);
        }else {
            if(kampfIndex + 1 < allFighterPairs.size()){
                mv.timeFiller(vereine, allFighterPairs.get(kampfIndex+1));
            }else{
                mv.timeFiller(vereine, null);
            }
        }


    }


    /*
        Diese Methode wird verwendet, um den Nächsten kampf zu setzten und
        sie managed auch alles in der ViewStage
     */
    public void continueToNextFight(int index){
        //Kampfindex ändern und nexten kampf für diese Methode zwischenspeichern
        kampfIndex = index;


        //neuen Kampf in mv anzeigen
        mv.newFight(allFighterPairs.get(kampfIndex), kampfIndex);

        //RemainingTime in mv setzten
        if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10")) mv.setFightTime(U10_TIME);
        else if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")) mv.setFightTime(U12_TIME);

        updateFightControlView(allFighterPairs.get(kampfIndex));
    }


    private void updateControlStage(){
        buildLeftControlPane();
        updateFightControlView(allFighterPairs.get(kampfIndex));
        controlRoot.setCenter(fightPane);
        updateViewStage();

    }



    public void play(){
        isFight = true; // TEST
        kampfIndex = 0; // test

        controlStage.setOnCloseRequest(windowEvent -> {
                    mv.closeStage();
                });


        updateControlStage();

        resetTimer();
        fightPane.setBottom(timerLabel);
        controlStage.setMinWidth(800);
        controlStage.setMinHeight(450);


        updateControlStage();



    }



    private String formatTime(int totalTimeInSeconds){
        int minutes = totalTimeInSeconds / 60;
        int seconds = totalTimeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void startTimer(){
        if(fighTime == null){
            fighTime = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
                remainingtime--;
                timerLabel.setText(formatTime(remainingtime));
                mv.updateTimeLabel(formatTime(remainingtime));
                if(remainingtime <= 0){
                    fighTime.stop();
                }
            }));
            fighTime.setCycleCount(Timeline.INDEFINITE);
        }
        fighTime.play();

    }

    private void stopTimer(){
        if (fighTime != null){
            fighTime.stop();

        }
    }

    private void resetTimer(){
        if(fighTime != null){
            fighTime.stop();
        }

        if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10")){
            remainingtime = U10_TIME;
        }
        else if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")){
            remainingtime = U12_TIME;
        }

        timerLabel.setText(formatTime(remainingtime));

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


    public void updateFightControlView(FighterPair f){
        if(fightPane == null){
            fightPane = new BorderPane();
        }



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
            start_stop.setText(isFight ? "Matte" : "Hajime");
            updateControlStage();
        });


        HBox topBox = new HBox(endFight, start_stop);

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

        Button editIppon01 = new Button("Ippon [strg + 1] : " + f.getIppon01());
        editIppon01.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Ippon von " + f.getName01() + " ändern");
            Label info = new Label(f.getName01() + " hat "+f.getIppon01()+" Ippon. " + (f.getIppon01() > 1 ? f.getName01() + "Hat jetzt theoretisch gewonnen." : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incIppon01();
                info.setText(f.getName01() + " hat "+f.getIppon01()+" Ippon. " + (f.getIppon01() > 1 ? f.getName01() + "Hat jetzt theoretisch gewonnen." : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decIppon01();
                info.setText(f.getName01() + " hat "+f.getIppon01()+" Ippon. " + (f.getIppon01() > 1 ? f.getName01() + "Hat jetzt theoretisch gewonnen." : ""));
            });

            HBox buttons = new HBox(dec, inc);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();
            });

            VBox ippon01vBox = new VBox(buttons, info, close);


            Scene ippon01scene = new Scene(ippon01vBox);

            ippon01scene.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            warningStage.setScene(ippon01scene);

            warningStage.requestFocus();

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.show();

        });
        // strg + 1

        Button editWaza_ari01 = new Button("Waza-ari [strg + 2] : " + f.getWaza_ari01());
        editWaza_ari01.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Waza-ari von " + f.getName01() + " ändern");

            Label info = new Label(f.getName01() + " hat " + f.getWaza_ari01() + " Waza-ari. " + (f.getWaza_ari01() > 1 ? f.getName01() + " hätte gewonnen" : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incWaza_ari01();
                info.setText(f.getName01() + " hat " + f.getWaza_ari01() + " Waza-ari. " + (f.getWaza_ari01() > 1 ? f.getName01() + " hätte gewonnen" : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decWaza_ari01();
                info.setText(f.getName01() + " hat " + f.getWaza_ari01() + " Waza-ari. " + (f.getWaza_ari01() > 1 ? f.getName01() + " hätte gewonnen" : ""));
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();


        });
        // strg + 2

        Button editYuko01 = new Button("Yuko [strg + arrow uup + 3] : " + f.getYuko01());
        editYuko01.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Yuko von " + f.getName01() + " ändern");


            Label info = new Label(f.getName01() + " hat " + f.getYuko01() + " Yuko.");
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incYuko01();
                info.setText(f.getName01() + " hat " + f.getYuko01() + " Yuko.");
            });
            dec.setOnAction(actionEvent1 -> {
                f.decYuko01();
                info.setText(f.getName01() + " hat " + f.getYuko01() + " Yuko.");
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();

        });
        // strg + 3

        Button editShido01 = new Button("Shido [strg + 4] : " + f.getShido01());
        editShido01.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Shido von " + f.getName01() + " ändern");

            Label info = new Label(f.getName01() + " hat " + f.getShido01() + " Shido. " + (f.getShido01() >= 3 ? f.getName01() + " hätte ein Hansoku-make" : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incShido01();
                info.setText(f.getName01() + " hat " + f.getShido01() + " Shido. " + (f.getShido01() >= 3 ? f.getName01() + " hätte ein Hansoku-make" : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decShido01();
                info.setText(f.getName01() + " hat " + f.getShido01() + " Shido. " + (f.getShido01() >= 3 ? f.getName01() + " hätte ein Hansoku-make" : ""));
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();

        });
        // strg + 4

        Button hansoku_make01 = new Button("Hansoku-make");
        hansoku_make01.setDisable(!f.isHansoku_make01());
        hansoku_make01.setOnAction(actionEvent -> {
            //Hansoku-make
        });

        VBox controls01 = new VBox(10, editIppon01, editWaza_ari01, editYuko01, editShido01, hansoku_make01);

        Button osae_komi01 = new Button(isFesthalter01 ? "Toketa" : "Osae-komi" + "\n[strg + 5]");
        osae_komi01.setOnAction(actionEvent -> {
            // action und auch css klassen ändern mit if(osae_komi01.getName().equals...)
            System.out.println("osae_komi01 clicked");
        });



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

        Button editIppon02 = new Button("Ippon [alt + 1] : " + f.getIppon02());
        editIppon02.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Ippon von " + f.getName02() + " ändern");
            Label info = new Label(f.getName02() + " hat "+f.getIppon02()+" Ippon. " + (f.getIppon02() > 1 ? f.getName02() + "Hat jetzt theoretisch gewonnen." : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incIppon02();
                info.setText(f.getName02() + " hat "+f.getIppon02()+" Ippon. " + (f.getIppon02() > 1 ? f.getName02() + "Hat jetzt theoretisch gewonnen." : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decIppon02();
                info.setText(f.getName02() + " hat "+f.getIppon02()+" Ippon. " + (f.getIppon02() > 1 ? f.getName02() + "Hat jetzt theoretisch gewonnen." : ""));
            });

            HBox buttons = new HBox(dec, inc);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();
            });

            VBox ippon02vBox = new VBox(buttons, info, close);


            Scene ippon02scene = new Scene(ippon02vBox);

            ippon02scene.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            warningStage.setScene(ippon02scene);

            warningStage.requestFocus();

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.show();
        });


        Button editWaza_ari02 = new Button("Waza-ari [alt + 2] : " + f.getWaza_ari02());
        editWaza_ari02.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Waza-ari von " + f.getName02() + " ändern");

            Label info = new Label(f.getName02() + " hat " + f.getWaza_ari02() + " Waza-ari. " + (f.getWaza_ari02() > 1 ? f.getName02() + " hätte gewonnen" : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incWaza_ari02();
                info.setText(f.getName02() + " hat " + f.getWaza_ari02() + " Waza-ari. " + (f.getWaza_ari02() > 1 ? f.getName02() + " hätte gewonnen" : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decWaza_ari02();
                info.setText(f.getName02() + " hat " + f.getWaza_ari02() + " Waza-ari. " + (f.getWaza_ari02() > 1 ? f.getName02() + " hätte gewonnen" : ""));
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();
        });


        Button editYuko02 = new Button("Yuko [alt + 3] : " + f.getYuko02());
        editYuko02.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Yuko von " + f.getName02() + " ändern");


            Label info = new Label(f.getName02() + " hat " + f.getYuko02() + " Yuko.");
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incYuko02();
                info.setText(f.getName02() + " hat " + f.getYuko02() + " Yuko.");
            });
            dec.setOnAction(actionEvent1 -> {
                f.decYuko02();
                info.setText(f.getName02() + " hat " + f.getYuko02() + " Yuko.");
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();
        });

        Button editShido02 = new Button("Shido [alt + 4] : " + f.getShido02());
        editShido02.setOnAction(actionEvent -> {
            Stage warningStage = new Stage();
            warningStage.initOwner(controlStage);
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.setTitle("Shido von " + f.getName02() + " ändern");

            Label info = new Label(f.getName02() + " hat " + f.getShido02() + " Shido. " + (f.getShido02() >= 3 ? f.getName02() + " hätte ein Hansoku-make" : ""));
            Button inc = new Button("+");
            Button dec = new Button("-");
            inc.setOnAction(actionEvent1 -> {
                f.incShido02();
                info.setText(f.getName02() + " hat " + f.getShido02() + " Shido. " + (f.getShido02() >= 3 ? f.getName02() + " hätte ein Hansoku-make" : ""));
            });
            dec.setOnAction(actionEvent1 -> {
                f.decShido02();
                info.setText(f.getName02() + " hat " + f.getShido02() + " Shido. " + (f.getShido02() >= 3 ? f.getName02() + " hätte ein Hansoku-make" : ""));
            });

            HBox buttons = new HBox(inc, dec);

            Button close = new Button("exit");

            close.setOnAction(actionEvent1 -> {
                warningStage.close();
                updateControlStage();

            });

            warningStage.setOnCloseRequest(windowEvent -> {
                close.fire();
                fightPane.requestFocus();
            });

            VBox warningRoot = new VBox(buttons, info, close);

            Scene scn = new Scene(warningRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.PLUS)) inc.fire();
                if(keyEvent.getCode().equals(KeyCode.MINUS)) dec.fire();
                if(keyEvent.getCode().equals(KeyCode.ESCAPE) || keyEvent.getCode().equals(KeyCode.ENTER)) close.fire();
            });

            warningStage.setMinWidth(300);
            warningStage.setMinHeight(200);

            warningStage.setScene(scn);
            warningStage.requestFocus();
            warningStage.show();
        });


        Button hansoku_make02 = new Button("Hansoku-make");
        hansoku_make02.setDisable(!f.isHansoku_make02());
        hansoku_make02.setOnAction(actionEvent -> {
            //Hansoku-make
        });

        VBox controls02 = new VBox(10, editIppon02, editWaza_ari02, editYuko02, editShido02, hansoku_make02);

        Button osae_komi02 = new Button(isFesthalter02 ? "Toketa" : "Oase-komi" + "\n[alt + 5]");
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
        VBox contents = new VBox(topBox, upperFighter, lowerFighter, timers);
        fightPane.setCenter(contents);
        fightPane.setFocusTraversable(true);

        fightPane.setOnKeyPressed(keyEvent -> {
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT1) editIppon01.fire();
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT2) editWaza_ari01.fire();
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT3) editYuko01.fire();
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT4) editShido01.fire();
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT5) osae_komi01.fire();

            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT1) editIppon02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT2) editWaza_ari02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT3) editYuko02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT4) editShido02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT5) osae_komi02.fire();

            if(keyEvent.getCode() == KeyCode.SPACE){

                System.out.println("\nSpace Clicked");
                if(fighTime == null ){
                    startTimer();
                    System.out.println("\nTimer started and it is running\n");
                }
                else if(fighTime.getStatus() == Animation.Status.STOPPED){
                    startTimer();
                    System.out.println("\nTimer is running\n");
                }
                else {
                    stopTimer();
                    System.out.println("Timer Stopped");
                }
            }



        });

        fightPane.setOnMouseClicked(event -> {
            fightPane.requestFocus();
        });




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