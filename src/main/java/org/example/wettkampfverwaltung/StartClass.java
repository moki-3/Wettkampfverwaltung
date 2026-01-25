package org.example.wettkampfverwaltung;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
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

    private boolean isGoldenScore = false;

    private BorderPane fightPane;

    private static final int U10_TIME = 120;
    private static final int U12_TIME = 120;
    private Timeline fighTime;
    private Label timerLabel = new Label("no time yet");
    private int remainingtime = -1;

    private HBox bottomRoot;
    private Label festhalertLabel01 = new Label("");
    private Timeline festhalterzeit01;
    private Label festhalertLabel02 = new Label("");
    private Timeline festhalterzeit02;
    private static final int OASEI_KOMI = 20;
    private static final int OASEI_KOMI_SHORT = 15;
    private int remainingOaseKomi = 0;

    private ProgressBar progressBar01;
    private ProgressBar progressBar02;

    private Button reset01, reset02, osae_komi01, osae_komi02, bStartGoldenScore;








    /*
        Diese Methode macht das erste Fenster, bei dem man die .csv File auswählt. Dann wird die
        List aller Fighters erstellt und danach wird die play() methode aufgerufen.
        Dazu wird Wettkampf.java benutzt.
        Hier wird auch mv gesetzt, mit der Länge der allFighterPairs.size() methode.
     */
    @Override
    public void start(Stage stage) throws Exception {
        resetOaseiKomi01();
        resetOaseiKomi01();

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
            if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10") || allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")) mv.updateTimeLabel(formatTime(U10_TIME));

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
        es steht dann "Bitte nächsten kampf auswählen" und man kann links den nächsten kampf auswählen
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
        //Kommentare, weil wenn ich die ProgressBars von anfang an habe, sehe ich sie auch immer, und das will ich nicth
        //progressBar01 = new ProgressBar(0);
        //progressBar02 = new ProgressBar(0);

        bStartGoldenScore = new Button("Golden Score starten");
        bStartGoldenScore.setOnAction(actionEvent -> startGoldeScore());
        bStartGoldenScore.setDisable(true);

        controlStage.setOnCloseRequest(windowEvent -> {
                    mv.closeStage();
                });
        if(this.fightPane == null) this.fightPane = new BorderPane();

        updateControlStage();

        resetTimer();
        //fightPane.setBottom(timerLabel);
        drawBottom();
        controlStage.setMinWidth(900);
        controlStage.setMinHeight(550);


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
                    checkWinner();
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
        mv.updateTimeLabel(formatTime(remainingtime)); // nochmal checken ob das stimmt

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


        Button endFight = new Button("Kampf Beenden");
        endFight.setOnAction(actionEvent -> {
            //Kampf beenden
            if(fighTime != null && fighTime.getStatus() != Animation.Status.STOPPED) stopTimer();
            checkWinner();
        });



        String text = "Kampf Starten";
        if(fighTime != null) text = fighTime.getStatus() == Animation.Status.STOPPED ? "Hajime" : "Matte";
        Button start_stop = new Button(text);
        start_stop.setOnAction(actionEvent -> {


            //System.out.println("\nSpace Clicked");
            if(fighTime == null ){
                startTimer();
                start_stop.setText("Matte");
                //System.out.println("\nTimer started and it is running\n");
            }
            else if(fighTime.getStatus() == Animation.Status.STOPPED){
                startTimer();
                start_stop.setText("Matte");
                //System.out.println("\nTimer is running\n");
            }
            else {
                stopTimer();
                start_stop.setText("Hajime");
                //System.out.println("Timer Stopped");
            }
            if(isFight){
                isFight = false;
            }else{
                isFight = true;
            }
            //start_stop.setText(isFight ? "Matte" : "Hajime");

            stopOaseiKomi01();
            stopOaseiKomi02();
            resetOaseiKomi01();
            resetOaseiKomi02();



            updateControlStage();
        });


        HBox topBox = new HBox(endFight, start_stop, bStartGoldenScore);

        // TOP HBOX END
        //################################################################################################
        // UPPER FIGHTER START

        HBox upperFighter = new HBox();

        Label name01 = new Label(f.getName01());
        Label verein01 = new Label(f.getVerein01());

        VBox daten01 = new VBox(10, name01, verein01);

        String points01 = "0";
        if(f.getIppon01() > 0 || f.getWaza_ari01() >= 2) {
            points01 = "100";
            checkWinner();
        }
        else {
            int tmp = 10 * f.getWaza_ari01() + f.getYuko01();
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
                checkWinner();
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

        osae_komi01 = new Button(isFesthalter01 ? "Toketa\n[strg + 5]" : "Osae-komi" + "\n[strg + 5]");

        osae_komi01.setOnAction(actionEvent -> {
            // action und auch css klassen ändern mit if(osae_komi01.getName().equals...
            //System.out.println("osae_komi01 clicked");
            if(festhalterzeit02 != null) reset02.fire();
            if(isFesthalter01){
                //wenn festhaler gerade ist, dann wird gestopt
                stopOaseiKomi01();
            }else{
                if(festhalertLabel01.getText().isEmpty()) {
                    festhalertLabel01.setText(formatTime(0));
                    mv.updateOaseiKomi01(festhalertLabel01.getText());
                }

                startOaseiKomi01();
            }
            isFesthalter01 = !isFesthalter01;
            osae_komi01.setText(isFesthalter01 ? "Toketa\n[strg + 5]" : "Osae-komi" + "\n[strg + 5]");
        });

        reset01 = new Button("Reset Oasei-Komi [strg + 6]");
        reset01.setOnAction(actionEvent -> {
            resetOaseiKomi01();
            isFesthalter01 = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa\n[strg + 5]" : "Osae-komi" + "\n[strg + 5]");
        });



        upperFighter.getChildren().addAll(daten01, points01l, controls01, osae_komi01, reset01);

        // UPPER FIGHTER END
        //################################################################################################
        // LOWER FITHER START
        HBox lowerFighter = new HBox();

        Label name02 = new Label(f.getName02());
        Label verein02 = new Label(f.getVerein02());

        VBox daten02 = new VBox(10, name02, verein02);

        String points02 = "0";
        if(f.getIppon02() > 0 || f.getWaza_ari02() == 2){
            points02 = "100";
            checkWinner();
        }
        else {
            int tmp = 10 * f.getWaza_ari02() + f.getYuko02();
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
                checkWinner();
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

        osae_komi02 = new Button(isFesthalter02 ? "Toketa\n[alt + 5]" : "Osae-komi" + "\n[alt + 5]");
        osae_komi02.setOnAction(actionEvent -> {
            // action und auch css klassen ändern mit if(osae_komi01.getName().equals...
            //System.out.println("osae_komi02 clicked");
            if(festhalterzeit01 != null) reset01.fire();
            if(isFesthalter02){
                //wenn festhaler gerade ist, dann wird gestopt
                stopOaseiKomi02();
            }else{
                if(festhalertLabel02.getText().isEmpty()) {
                    festhalertLabel02.setText(formatTime(0));
                    mv.updateOaseiKomi02(festhalertLabel02.getText());
                }
                startOaseiKomi02();
            }
            isFesthalter02 = !isFesthalter02;
            osae_komi02.setText(isFesthalter02 ? "Toketa\n[alt + 5]" : "Osae-komi" + "\n[alt + 5]");
        });

        reset02 = new Button("Reset Oasei-Komi [alt + 6]");
        reset02.setOnAction(actionEvent -> {
            resetOaseiKomi02();
            isFesthalter02 = false;
            osae_komi02.setText(isFesthalter02 ? "Toketa\n[alt + 5]" : "Osae-komi" + "\n[alt + 5]");
        });

        lowerFighter.getChildren().addAll(daten02, points02l, controls02, osae_komi02, reset02);

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
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.DIGIT6) reset01.fire();

            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT1) editIppon02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT2) editWaza_ari02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT3) editYuko02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT4) editShido02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT5) osae_komi02.fire();
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.DIGIT6) reset02.fire();

            if(keyEvent.getCode() == KeyCode.SPACE){

                start_stop.fire();
            }



        });

        fightPane.setOnMouseClicked(event -> {
            fightPane.requestFocus();
        });




    }

    private void resetOaseiKomi01(){
        //Punkte vergeben
        if(remainingOaseKomi == OASEI_KOMI){
            allFighterPairs.get(kampfIndex).incIppon01();

            //warnWinning01();
            checkWinner();
        }else if(remainingOaseKomi >= OASEI_KOMI_SHORT){
            allFighterPairs.get(kampfIndex).incWaza_ari01();
            if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 2) {
                //warnWinning01();
                checkWinner();
            }
        }
        if(controlRoot != null)updateFightControlView(allFighterPairs.get(kampfIndex));
        if(mv != null) mv.updateFight(allFighterPairs.get(kampfIndex));

        //alles reseten
        if(festhalterzeit01 != null) festhalterzeit01.stop();
        festhalterzeit01 = null;
        festhalertLabel01.setText("");
        remainingOaseKomi = 0;
        isFesthalter01 = false;
        if(this.mv != null) mv.updateOaseiKomi01("");
        //if(this.progressBar01 != null) progressBar01.setProgress(0);
        progressBar01 = null;
        if(mv != null){
            mv.resetProgresbar01();
            mv.drawBottom();
        }
        drawBottom();
    }

    private void resetOaseiKomi02(){
        //Punkte vergeben
        if(remainingOaseKomi == OASEI_KOMI){
            allFighterPairs.get(kampfIndex).incIppon02();
            //warnWinning02();
            checkWinner();
        }else if(remainingOaseKomi >= OASEI_KOMI_SHORT){
            allFighterPairs.get(kampfIndex).incWaza_ari02();
            if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 2) {
                //warnWinning01();
                checkWinner();
            }
        }
        if(controlRoot != null)updateFightControlView(allFighterPairs.get(kampfIndex));
        if(mv != null) mv.updateFight(allFighterPairs.get(kampfIndex));
        if(festhalterzeit02 != null) festhalterzeit02.stop();
        festhalterzeit02 = null;
        festhalertLabel02.setText("");
        remainingOaseKomi = 0;
        isFesthalter02 = false;
        if(this.mv != null) mv.updateOaseiKomi02("");
        //if(this.progressBar02 != null) progressBar02.setProgress(0);
        progressBar02 = null;
        if(mv != null){
            mv.resetProgressbar02();
            mv.drawBottom();
        }
        drawBottom();
    }


    private void startOaseiKomi01(){
        if(fighTime == null || fighTime.getStatus() == Animation.Status.STOPPED) {
            resetOaseiKomi01();
            return;
        }
        if(festhalterzeit01 == null){
            if(progressBar01 == null){
                progressBar01 = new ProgressBar(0);
                mv.initProgressbar01();
            }
            festhalterzeit01 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) ->{
                remainingOaseKomi++;
                festhalertLabel01.setText(formatTime(remainingOaseKomi));
                //mv updaten
                mv.updateOaseiKomi01(formatTime(remainingOaseKomi));
                //progressbar
                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                progressBar01.setProgress(progress);
                mv.updateProgressbar01(progress);
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 1 && remainingOaseKomi == OASEI_KOMI_SHORT){
                    //System.out.println("im if");
                    festhalterzeit01.stop();
                    progressBar01.setProgress(1.0);
                    mv.updateProgressbar01(1.0);
                    allFighterPairs.get(kampfIndex).incWaza_ari01();
                    checkWinner();

                }
                else if(remainingOaseKomi == OASEI_KOMI){
                    festhalterzeit01.stop();
                    progressBar01.setProgress(1.0);
                    mv.updateProgressbar01(1.0);

                    allFighterPairs.get(kampfIndex).incIppon01();

                    //warnWinning01();
                    checkWinner();
                }
            }));
            festhalterzeit01.setCycleCount(Timeline.INDEFINITE);
            drawBottom();
            mv.drawBottom();
        }

        festhalterzeit01.play();
    }

    private void startOaseiKomi02(){
        if(fighTime == null || fighTime.getStatus() == Animation.Status.STOPPED) {
            resetOaseiKomi02();
            return;
        }
        if(festhalterzeit02 == null){
            if(progressBar02 == null) {
                progressBar02 = new ProgressBar(0);
                mv.initProgressbar02();
            }
            festhalterzeit02 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) ->{
                remainingOaseKomi++;
                festhalertLabel02.setText(formatTime(remainingOaseKomi));
                //mv updaten
                mv.updateOaseiKomi02(formatTime(remainingOaseKomi));
                //progressbar
                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                progressBar02.setProgress(progress);
                mv.updateProgressbar02(progress);
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 1 && remainingOaseKomi == OASEI_KOMI_SHORT){
                    festhalterzeit02.stop();
                    progressBar02.setProgress(1.0);
                    mv.updateProgressbar02(1.0);

                    allFighterPairs.get(kampfIndex).incWaza_ari02();
                    checkWinner();

                }else if(remainingOaseKomi == OASEI_KOMI){
                    festhalterzeit02.stop();
                    progressBar02.setProgress(1.0);
                    mv.updateProgressbar02(1.0);
                    allFighterPairs.get(kampfIndex).incIppon02();

                    //warnWinning02();
                    checkWinner();
                }
            }));
            festhalterzeit02.setCycleCount(Timeline.INDEFINITE);
            drawBottom();
            mv.drawBottom();
        }
        festhalterzeit02.play();
    }

    private void stopOaseiKomi01(){
        if(festhalterzeit01 != null){
            festhalterzeit01.stop();

        }
    }

    private void stopOaseiKomi02(){
        if(festhalterzeit02 != null){
            festhalterzeit02.stop();

        }
    }


    public void drawBottom(){

        if(bottomRoot == null){
            bottomRoot = new HBox();
            bottomRoot.setSpacing(10);
        }


        //fightPane.setBottom(timerLabel);




        VBox box01 = new VBox(10, festhalertLabel01);
        VBox box02 = new VBox(10, festhalertLabel02);
        if(progressBar02 != null) box02.getChildren().add(progressBar02);
        if(progressBar01 != null) box01.getChildren().add(progressBar01);

        bottomRoot.getChildren().clear();
        bottomRoot.getChildren().addAll(box01, timerLabel, box02);
        if(fightPane != null)fightPane.setBottom(bottomRoot);
    }










    public void startGoldeScore(){
        Stage goldenScoreStage = new Stage();
        goldenScoreStage.initOwner(controlStage);
        goldenScoreStage.initModality(Modality.WINDOW_MODAL);
        final boolean[] valid = {false};
        Label start = new Label("Golden Score starten");
        Label warnung = new Label("");
        TextField timeField = new TextField("02:00");
        AtomicInteger timeInSeconds = new AtomicInteger();
        AtomicInteger minutes = new AtomicInteger();
        AtomicInteger seconds = new AtomicInteger();

        Button submit = new Button("Fertig");
        Button exit = new Button("Cancel");
        exit.setOnAction(actionEvent -> goldenScoreStage.close());
        submit.setDisable(true);

        timeField.setOnAction(actionEvent -> {

            if(timeField.getText().length() != 5) warnung.setText("Ungültige Eingabe: die Eingabe muss 5 Zeichen lang sein. Aktuelle länge: " + timeField.getText().length());
            else if(timeField.getText().toCharArray()[2] != ':') warnung.setText("Ungültige Eingabe: das 3. Zeichen muss ein ':' sein");
            else {
                String[] zeichen = timeField.getText().split(":");
                try{
                    minutes.set(Integer.parseInt(zeichen[0]));
                    seconds.set(Integer.parseInt(zeichen[1]));
                    valid[0] = true;
                    submit.setDisable(false);
                }catch (Exception e){
                    submit.setDisable(true);
                    System.out.println(e);
                    warnung.setText("Ungültige Eingabe: Es sind keine Zahlen. Schreibe z.B.: '02:00' für 2 Minuten");
                }
            }
        });

        submit.setOnAction(actionEvent -> {
            if(valid[0]){
                timeInSeconds.set(minutes.get() * 60 + seconds.get());
                goldenScoreStage.close();
                setGoldenScoreTime(timeInSeconds.get());
            }else{
                warnung.setText("Ungültige Eingabe!");
                submit.setDisable(true);
            }
        });

        VBox buttons = new VBox(30, exit, submit);
        BorderPane goldenRoot = new BorderPane();
        HBox center = new HBox(20, start, warnung, timeField);
        goldenRoot.setBottom(buttons);
        goldenRoot.setCenter(center);
        Scene scn = new Scene(goldenRoot);

        scn.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) exit.fire();
        });

        goldenScoreStage.setScene(scn);
        goldenScoreStage.show();


    }


    public void setGoldenScoreTime(int time){
        isGoldenScore = true;
        System.out.println(formatTime(time));
        System.out.println("isGoldenScore = " + true);
    }



    // checkt wer gewonnen hat, wenn gleichstand ist dann wird golden score button disabled(false)
    private void checkWinner(){
        int points01 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
        int points02 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
        if(points01 >= 100) points01 = 100;
        if(points02 >= 100) points02 = 100;

        String winner = "-1";

        if(points01 == points02) {
            bStartGoldenScore.setDisable(false); // wenn gleichstand kann goldenScore gemacht werden
        }
        else if(points01 > points02){
            winner = allFighterPairs.get(kampfIndex).getName01();
        }else{
            winner = allFighterPairs.get(kampfIndex).getName02();
        }

        Stage smallStage = new Stage();
        smallStage.initOwner(controlStage);
        smallStage.initModality(Modality.WINDOW_MODAL);

        BorderPane smallRoot = new BorderPane();

        Label who01 = new Label(allFighterPairs.get(kampfIndex).getName01());
        Label ippons01 = new Label("Ippons: " + allFighterPairs.get(kampfIndex).getIppon01());
        Label waza_aris01 = new Label("Waza-aris: " + allFighterPairs.get(kampfIndex).getWaza_ari01());
        Label yukos01 = new Label("Yukos: " + allFighterPairs.get(kampfIndex).getYuko01());
        Label shidos01 = new Label("shidos: " + allFighterPairs.get(kampfIndex).getShido01());

        VBox labels01 = new VBox(10, who01, ippons01, waza_aris01, yukos01, shidos01);

        Label who02 = new Label(allFighterPairs.get(kampfIndex).getName02());
        Label ippons02 = new Label("Ippons: " + allFighterPairs.get(kampfIndex).getIppon02());
        Label waza_aris02 = new Label("Waza-aris: " + allFighterPairs.get(kampfIndex).getWaza_ari02());
        Label yukos02 = new Label("Yukos: " + allFighterPairs.get(kampfIndex).getYuko02());
        Label shidos02 = new Label("shidos: " + allFighterPairs.get(kampfIndex).getShido02());

        VBox labels02 = new VBox(10, who02, ippons02, waza_aris02, yukos02, shidos02);

        HBox allInfo = new HBox(20, labels01, labels02);



        if(winner == "-1"){
            //wenn untentschieden

            Label info = new Label("Untenschieden!\nGoldenScore kann jetzt gestartet werden!");

            VBox content = new VBox(50, info, allInfo);
            smallRoot.setCenter(content);

            bStartGoldenScore.setDisable(false);

            Button ok = new Button("ok");

            ok.setOnAction(actionEvent -> smallStage.close());

            Button disable = new Button("Ich brauche noch kein Golden Score");
            disable.setOnAction(actionEvent -> {
                bStartGoldenScore.setDisable(true);
                ok.fire();
            });

            HBox buttons = new HBox(20, ok, disable);

            smallRoot.setBottom(buttons);

            Scene scn = new Scene(smallRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.ESCAPE) ok.fire();
            });

            smallStage.setScene(scn);
        }else{

            Button b01 = new Button(allFighterPairs.get(kampfIndex).getName01());
            Button b02 = new Button(allFighterPairs.get(kampfIndex).getName02());

            //hier if mit css machen, dass button, wo name gleich winner ist, primary button wird


            Button cancel = new Button("Cancel");
            cancel.setOnAction(actionEvent -> smallStage.close());


            HBox buttons = new HBox(30, b01, b02, cancel);

            Label info = new Label(Objects.equals(winner, allFighterPairs.get(kampfIndex).getName01()) ? allFighterPairs.get(kampfIndex).getName01() + " hat gewonnen!" : allFighterPairs.get(kampfIndex).getName02() + " hat gewonnen!");



            smallRoot.setTop(info);


            VBox content = new VBox(40, allInfo, buttons);

            smallRoot.setCenter(content);

            Scene scn = new Scene(smallRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) cancel.fire();
            });


            smallStage.setScene(scn);

        }

        smallStage.show();

    }

    /*

    17.01.25 23:21
    Ich bin jetzt zu müde um weiter zu machen. Was ich mache: checkWinner checkt wer gewonnen hat und hier kann ich auch dann
    den / die Gewiner*in setzten. Somit kann ich das bei den Festhalterfunktionen wegmachen und einfach wenn die Zeit aufgebraucht
    ist checkWinner() aufrufen. checkWinner() rufe ich auch auf, wenn die 2 Minuten Kampfzeit zuende sind. Auch wenn ein ich einen
    Ippon mache. In der Methode, wo ich die Controls update, berechne ich ja auch die Punkte. Da kann ich auch machen, wenn die
    Punkte 100 oder mehr sind, wird checkWinner aufgerufen. Und beim Button endfight.
    In checkWinner wird dann vorgeschlagen wer gewonnen hat, aber es kann auch die andere Person ausgewählt werden. Und hier
    werden dann die Points für den Verein vergeben und dann wird auch dieser TimeFiller in MV angezeigt
    Ich brauche in der Methode auch diese kleinen Stages da.

    GoldenScore muss ich auch noch machen. Ich benutze da einfach das normale timelabel aber mache eine andere Timeline, da ich
    ja raufzählen muss. Und einen Boolean oder getStatus damit ich weiß ob GoldenScore ist, weil bei einem Golden Score ja alles
    entscheidend ist.

     */





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