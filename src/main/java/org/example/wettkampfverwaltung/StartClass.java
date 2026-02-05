package org.example.wettkampfverwaltung;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.beans.Expression;
import java.io.File;
import java.sql.SQLOutput;
import java.sql.Time;
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
    private int goldenScoreAbsoluteTime = -1;

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

    private boolean r_flag;

    Stage chechWinnerStage;

    //wenn dieser boolean wahr ist, kann man den nächsten kampf auswählen
    boolean chooseFight = false;
    boolean isFesthalter01 = false;
    boolean isFesthalter02 = false;







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
        VBox leftControls = new VBox(20, fullscreenbox, showViewStage, createVereinList(), createList());
        controlRoot.setLeft(leftControls);

    }

    public ScrollPane createList() {
        VBox vbox = new VBox();
        for (FighterPair fp : allFighterPairs) {
//            Button button = new Button(fp.getName01() + " (" + fp.getVerein01() + ") " + " | "
//                    + fp.getName02() + " (" + fp.getVerein02() + ") " + " | " + fp.getAltersKlasse() + fp.getGewichtsKlasse());
            Label name01 = new Label(fp.getName01());
            Label name02 = new Label(fp.getName02());
            Label verein01 = new Label("(" + fp.getVerein01() + ")");
            Label verein02 = new Label("(" + fp.getVerein02() + ")");
            Label altersklasse = new Label(fp.getAltersKlasse());
            Label gewichtsklasse = new Label(fp.getGewichtsKlasse());
            if (!fp.getWinner().equals("nicht gesetzt")) {
                //wenn es einen gewinner gibt, css klassen setzten
                if (fp.getWinner().equals(name01.getText())) {
                    /*
                        CSS klassen: schrift bei name01 und verein01 grün, und bei name02 und verein02 rot machen
                     */
                } else {
                    /*
                        CSS klassen: schrift bei name02 und verein02 grün, und bei name01 und verein01 rot machen
                     */
                }

            }

            HBox row01 = new HBox();
            HBox row02 = new HBox();
            HBox row03 = new HBox();
            row01.getChildren().addAll(name01, verein01);
            row01.setSpacing(30);
            row02.getChildren().addAll(name02, verein02);
            row02.setSpacing(30);
            row03.getChildren().addAll(altersklasse, gewichtsklasse);
            row03.setSpacing(50);


            //css klassen für die hboxes


            VBox tmp = new VBox(10, row01, row02, row03);

            //css klassen

            tmp.setOnMouseClicked(mouseEvent -> {
                int index = allFighterPairs.indexOf(fp);
                System.out.println("\nvbox geklickt\t index = " + index);
                System.out.println("chooseFight: " + chooseFight);
                if(chooseFight) setNextFight(index);
            });

            vbox.getChildren().add(tmp);


        }

        ScrollPane sp = new ScrollPane(vbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // immer vertikal scrollen
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); //bei bedarf horizonal scrollen
        return sp;
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
        System.out.println("r_flag: " + r_flag);
        System.out.println("isFight: " + isFight);
        if(isFight){
            //mv.updateFight(allFighterPairs.get(kampfIndex));
            //if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10") || allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")) mv.updateTimeLabel(formatTime(U10_TIME));
            mv.updateTimeLabel(formatTime(remainingtime));
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
    public void continueToNextFight(){
        //Kampfindex ändern und nexten kampf für diese Methode zwischenspeichern

        System.out.println("in continueToNextFight");
        mv.timeFiller(vereine, null);

        Label text = new Label("Wähle den nächsten Kampf aus");
        chooseFight = true;
        controlRoot.setCenter(text);


        //neuen Kampf in mv anzeigen


        //RemainingTime in mv setzten
        //if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10")) mv.setFightTime(U10_TIME);
        //else if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")) mv.setFightTime(U12_TIME);

        //updateFightControlView(allFighterPairs.get(kampfIndex));
    }

    /*
    Diese Methode wird aufgerufen wenn man links einen Kampf klickt unc chooseFight = true ist.
    Dann wird die view in controlstage und in mv upgedated
     */
    public void setNextFight(int index){
        mv.timeFiller(vereine, allFighterPairs.get(index));
        Label frage = new Label("Nächster Kampf:");
        Label name01 = new Label(allFighterPairs.get(index).getName01());
        Label verein01 = new Label(allFighterPairs.get(index).getVerein01());

        Label name02 = new Label(allFighterPairs.get(index).getName02());
        Label verein02 = new Label(allFighterPairs.get(index).getVerein02());

        Label alter = new Label(allFighterPairs.get(index).getAltersKlasse());


        VBox box01 = new VBox(name01, verein01);
        VBox box02 = new VBox(name02, verein02);
        HBox nextFighters = new HBox(box01, box02);

        Button validate = new Button("Weiter");
        validate.setOnAction(actionEvent -> {
            //System.out.println("Weiter geklickt");
            chooseFight = false;
            kampfIndex = index;
            updateControlStage();

        });

        VBox box = new VBox(frage, nextFighters, alter, validate);
        controlRoot.setCenter(box);
    }


    private void updateControlStage(){
        buildLeftControlPane();
        updateFightControlView();
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
        if (fighTime == null) {
            fighTime = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
                //Wenn Golden Score, dann wird raufgezählt
                if(isGoldenScore) remainingtime ++;
                else remainingtime--;



                timerLabel.setText(formatTime(remainingtime));
                mv.updateTimeLabel(formatTime(remainingtime));
                if(isGoldenScore && remainingtime >= goldenScoreAbsoluteTime){
                    fighTime.stop();
                }
                if ((isGoldenScore && remainingtime >= goldenScoreAbsoluteTime) || (!isGoldenScore && remainingtime <= 0)) {
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

        if(isGoldenScore){
            remainingtime = 0; // weil es ja raufzählt
        }
        else if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10")){
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

    public void updateFightControlView(){
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
            isFight = !isFight;
            //start_stop.setText(isFight ? "Matte" : "Hajime");


            if(osae_komi02.getText().equals("Toketa [J]")) {
                stopOaseiKomi02();
                isFesthalter02 = false;
            }
            if(osae_komi01.getText().equals("Toketa [J]")) {
                stopOaseiKomi01();
                isFesthalter02 = false;
            }

            r_flag = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");

            resetOaseiKomi01();
            resetOaseiKomi02();



            updateControlStage();
        });


        HBox topBox = new HBox(endFight, start_stop, bStartGoldenScore);

        Label rMode = new Label(r_flag ? "R Modus ein" : "R Modus aus");

        Label goldenScore = new Label(isGoldenScore ? "Golden Score: " + formatTime(goldenScoreAbsoluteTime) : "");

        VBox top = new VBox(20, rMode, topBox, goldenScore);

        // TOP HBOX END
        //################################################################################################
        // UPPER FIGHTER START

        HBox upperFighter = new HBox();

        Label name01 = new Label(allFighterPairs.get(kampfIndex).getName01());
        Label verein01 = new Label(allFighterPairs.get(kampfIndex).getVerein01());

        VBox daten01 = new VBox(10, name01, verein01);

        String points01 = "0";
        if(allFighterPairs.get(kampfIndex).getIppon01() > 0 || allFighterPairs.get(kampfIndex).getWaza_ari01() >= 2) {
            points01 = "100";
            if(!r_flag) checkWinner();
        }
        else {
            int tmp = 10 * allFighterPairs.get(kampfIndex).getWaza_ari01() + allFighterPairs.get(kampfIndex).getYuko01();
            points01 = tmp + "";
        }


        Label points01l = new Label(points01);

        Button editIppon01 = new Button("Ippon [A] : " + allFighterPairs.get(kampfIndex).getIppon01());
        editIppon01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decIppon01();
            }else{
                allFighterPairs.get(kampfIndex).incIppon01();
                checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 1

        Button editWaza_ari01 = new Button("Waza-ari [S] : " + allFighterPairs.get(kampfIndex).getWaza_ari01());
        editWaza_ari01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decWaza_ari01();
            }else{
                allFighterPairs.get(kampfIndex).incWaza_ari01();
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 2) checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 2

        Button editYuko01 = new Button("Yuko [D] : " + allFighterPairs.get(kampfIndex).getYuko01());
        editYuko01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decYuko01();
            }else{
                allFighterPairs.get(kampfIndex).incYuko01();
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 3

        Button editShido01 = new Button("Shido [G] : " + allFighterPairs.get(kampfIndex).getShido01());
        editShido01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decShido01();
            }else{
                allFighterPairs.get(kampfIndex).incShido01();
            }
            r_flag = false;
            updateControlStage();
        });


        Button hansoku_make01 = new Button("Hansoku-make");
        hansoku_make01.setDisable(!allFighterPairs.get(kampfIndex).isHansoku_make01());
        hansoku_make01.setOnAction(actionEvent -> {
            //Hansoku-make
        });

        VBox controls01 = new VBox(10, editIppon01, editWaza_ari01, editYuko01, editShido01, hansoku_make01);

        osae_komi01 = new Button(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");

        osae_komi01.setOnAction(actionEvent -> {
            if(r_flag || osae_komi01.getText().equals("Toketa [F]")){
                stopOaseiKomi01();
                isFesthalter01 = false;
            }else{
                startOaseiKomi01();
                isFesthalter01 = true;
            }
            r_flag = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        });

        reset01 = new Button("Reset Oasei-Komi");
        reset01.setOnAction(actionEvent -> {
            resetOaseiKomi01();
            isFesthalter01 = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
        });



        upperFighter.getChildren().addAll(daten01, points01l, controls01, osae_komi01, reset01);

        // UPPER FIGHTER END
        //################################################################################################
        // LOWER FITHER START
        HBox lowerFighter = new HBox();

        Label name02 = new Label(allFighterPairs.get(kampfIndex).getName02());
        Label verein02 = new Label(allFighterPairs.get(kampfIndex).getVerein02());

        VBox daten02 = new VBox(10, name02, verein02);

        String points02 = "0";
        if(allFighterPairs.get(kampfIndex).getIppon02() > 0 || allFighterPairs.get(kampfIndex).getWaza_ari02() == 2){
            points02 = "100";
            if(!r_flag) checkWinner();
        }
        else {
            int tmp = 10 * allFighterPairs.get(kampfIndex).getWaza_ari02() + allFighterPairs.get(kampfIndex).getYuko02();
            points02 = tmp + "";
        }

        Label points02l = new Label(points02);

        Button editIppon02 = new Button("Ippon [Ö] : " + allFighterPairs.get(kampfIndex).getIppon02());
        editIppon02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decIppon02();
            }else{
                allFighterPairs.get(kampfIndex).incIppon02();
                checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });


        Button editWaza_ari02 = new Button("Waza-ari [L] : " + allFighterPairs.get(kampfIndex).getWaza_ari02());
        editWaza_ari02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decWaza_ari02();
            }else{
                allFighterPairs.get(kampfIndex).incWaza_ari02();
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 2) checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });


        Button editYuko02 = new Button("Yuko [K] : " + allFighterPairs.get(kampfIndex).getYuko02());
        editYuko02.setOnAction(actionEvent -> {
            if (r_flag) {
                allFighterPairs.get(kampfIndex).decYuko02();
            }else{
                allFighterPairs.get(kampfIndex).incYuko02();
            }
            r_flag = false;
            updateControlStage();
        });

        Button editShido02 = new Button("Shido [H] : " + allFighterPairs.get(kampfIndex).getShido02());
        editShido02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decShido02();
            }else{
                allFighterPairs.get(kampfIndex).incShido02();
            }
            r_flag = false;
            updateControlStage();
        });


        Button hansoku_make02 = new Button("Hansoku-make");
        hansoku_make02.setDisable(!allFighterPairs.get(kampfIndex).isHansoku_make02());
        hansoku_make02.setOnAction(actionEvent -> {
            //Hansoku-make
        });

        VBox controls02 = new VBox(10, editIppon02, editWaza_ari02, editYuko02, editShido02, hansoku_make02);

        osae_komi02 = new Button(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        osae_komi02.setOnAction(actionEvent -> {
            if(r_flag || osae_komi02.getText().equals("Toketa [J]")){
                stopOaseiKomi02();
                isFesthalter02 = false;
            }else{
                startOaseiKomi02();
                isFesthalter02 = true;
            }
            r_flag = false;
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
        });

        reset02 = new Button("Reset Oasei-Komi");
        reset02.setOnAction(actionEvent -> {
            resetOaseiKomi02();
            isFesthalter02 = false;
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        });

        lowerFighter.getChildren().addAll(daten02, points02l, controls02, osae_komi02, reset02);

        // LOWER FITHER END
        //################################################################################################
        // TIMERS START
        HBox timers = new HBox();

        // TIMERS END
        //################################################################################################
        VBox contents = new VBox(top, upperFighter, lowerFighter, timers);
        fightPane.setCenter(contents);
        fightPane.setFocusTraversable(true);

        fightPane.setOnKeyTyped(keyEvent -> {
            switch (keyEvent.getCharacter().toLowerCase()){
                case "a" -> editIppon01.fire();
                case "ö" -> editIppon02.fire();
                case "s" -> editWaza_ari01.fire();
                case "l" -> editWaza_ari02.fire();
                case "d" -> editYuko01.fire();
                case "k" -> editYuko02.fire();
                case "f" -> osae_komi01.fire();
                case "j" -> osae_komi02.fire();
                case "g" -> editShido01.fire();
                case "h" -> editShido02.fire();
                case " " -> start_stop.fire();
                case "r" -> {
                    r_flag = !r_flag;
                    updateControlStage();
                }

            }


            //updateControlStage();
            //schauen wie und wo ich mv updaten muss

        });

        fightPane.setOnMouseClicked(event -> {
            fightPane.requestFocus();
        });
    }

    private void resetOaseiKomi01(){
        //Punkte vergeben
        if(remainingOaseKomi >= OASEI_KOMI_SHORT && allFighterPairs.get(kampfIndex).getWaza_ari01() == 0){
            allFighterPairs.get(kampfIndex).incWaza_ari01();
        }
        if(controlRoot != null)updateFightControlView();
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
        if(remainingOaseKomi >= OASEI_KOMI_SHORT && allFighterPairs.get(kampfIndex).getWaza_ari02() == 0){
            allFighterPairs.get(kampfIndex).incWaza_ari02();
        }

        if(controlRoot != null)updateFightControlView();
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
        /*
        wenn die Timeline fighTime nicht exestiert oder gestoppt ist, wird der
        resetOaseiKomi01 aufgerufen.
        Das ganze wird aufgerufen, wenn der Kampf noch nicht gestartet hat
         */
        if(fighTime == null || fighTime.getStatus() == Animation.Status.STOPPED) {
            resetOaseiKomi01();
            return;
        }

        /*
        Wenn davor der andere Festhalter war, wird diese pausiert und
        die Zeit gespeichert. Mit dem boolean before wird abgespeichert,
        ob es eine Zeit davor gab. Das brauche ich später
         */
        boolean before = false;
        int seconds = 0;
        if(festhalterzeit02 != null && festhalterzeit01 == null){
            stopOaseiKomi02();

            String[] parts = festhalertLabel02.getText().split(":");
            if (parts.length <= 2) {
                seconds = Integer.parseInt(parts[1]);
                before = true;
                resetOaseiKomi02();
                System.out.println("seconds: " + seconds);
            }
        }





        if(festhalterzeit01 == null){
            if(progressBar01 == null){
                progressBar01 = new ProgressBar(0);
                mv.initProgressbar01();
            }

            festhalterzeit01 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
                remainingOaseKomi++;
                festhalertLabel01.setText(formatTime(remainingOaseKomi));
                //mv updaten
                mv.updateOaseiKomi01(formatTime(remainingOaseKomi));
                //progressbar
                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                //diese Progressbar
                progressBar01.setProgress(progress);
                //mv Progressbar
                mv.updateProgressbar01(progress);
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 1 && remainingOaseKomi >= OASEI_KOMI_SHORT){
                    stopOaseiKomi01();
                    progressBar01.setProgress(1.0);
                    mv.updateProgressbar01(1.0);
                    allFighterPairs.get(kampfIndex).incWaza_ari01();
                    checkWinner();
                }else if(remainingOaseKomi == OASEI_KOMI){
                    stopOaseiKomi01();
                    progressBar01.setProgress(1.0);
                    mv.updateProgressbar01(1.0);
                    allFighterPairs.get(kampfIndex).incIppon01();
                    checkWinner();
                }
            }));
            festhalterzeit01.setCycleCount(Timeline.INDEFINITE);
            if(before){
                remainingOaseKomi = seconds;
                festhalertLabel01.setText(formatTime(remainingOaseKomi));
                mv.updateOaseiKomi01(formatTime(remainingOaseKomi));
                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                progressBar01.setProgress(progress);
                mv.updateProgressbar01(progress);

            }
            drawBottom();
            mv.drawBottom();
        }
        festhalterzeit01.play();
    }

    private void startOaseiKomi02(){
        if(fighTime == null || fighTime.getStatus() == Animation.Status.STOPPED){
            resetOaseiKomi02();
            return;
        }

        boolean before = false;
        int seconds = 0;
        if(festhalterzeit01 != null && festhalterzeit02 == null){
            stopOaseiKomi01();
            String[] parts = festhalertLabel01.getText().split(":");
            if (parts.length <= 2) {
                seconds = Integer.parseInt(parts[1]);
                before = true;
                resetOaseiKomi01();
                System.out.println("Seconds: " + seconds);
            }
        }

        if(festhalterzeit02 == null){
            if(progressBar02 == null){
                progressBar02 = new ProgressBar(0);
                mv.initProgressbar02();
            }

            festhalterzeit02 = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
                remainingOaseKomi++;
                festhalertLabel02.setText(formatTime(remainingOaseKomi));
                mv.updateOaseiKomi02(formatTime(remainingOaseKomi));

                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                progressBar02.setProgress(progress);
                mv.updateProgressbar02(progress);
                //remainingOaseKomi >= OASEI_KOMI_SHORT weil wenn man die person wechselt während dem festhalter
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 1 && remainingOaseKomi >= OASEI_KOMI_SHORT){
                    stopOaseiKomi02();
                    progressBar02.setProgress(1.0);
                    mv.updateProgressbar02(1.0);
                    allFighterPairs.get(kampfIndex).incWaza_ari02();
                    checkWinner();
                }else if(remainingOaseKomi == OASEI_KOMI){
                    stopOaseiKomi02();
                    progressBar02.setProgress(1.0);
                    mv.updateProgressbar02(1.0);
                    allFighterPairs.get(kampfIndex).incIppon02();
                    checkWinner();
                }
            }));
            festhalterzeit02.setCycleCount(Timeline.INDEFINITE);
            if(before){
                remainingOaseKomi = seconds;
                festhalertLabel02.setText(formatTime(remainingOaseKomi));
                mv.updateOaseiKomi02(formatTime(remainingOaseKomi));
                double progress = (double) remainingOaseKomi / OASEI_KOMI;
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 1) progress = (double) remainingOaseKomi / OASEI_KOMI_SHORT;
                progressBar02.setProgress(progress);
                mv.updateProgressbar02(progress);
            }
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

        Label start = new Label("Golden Score starten");
        Label warnung = new Label("");
        TextField timeField = new TextField("02:00");
        AtomicInteger timeInSeconds = new AtomicInteger();


        Button submit = new Button("Fertig");
        submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
        Button exit = new Button("Cancel");
        exit.setOnAction(actionEvent -> goldenScoreStage.close());
        //submit.setDisable(true);

//        timeField.setOnAction(actionEvent -> {
//            submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
//        });
        timeField.setOnKeyTyped(keyEvent -> {
            submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
        });
        timeField.setOnAction(actionEvent -> submit.fire());

        Button resetTimeField = new Button("Reset Input");
        resetTimeField.setOnAction(actionEvent -> {
            timeField.setText("02:00");
            submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
        });

        submit.setOnAction(actionEvent -> {
            if(isInputValidAsTimeformat_mm_ss(timeField.getText())){
                String[] zeichen = timeField.getText().split(":");
                int minutes = Integer.parseInt(zeichen[0]);
                int seconds = Integer.parseInt(zeichen[1]);
                timeInSeconds.set(minutes * 60 + seconds);
                goldenScoreStage.close();
                setGoldenScoreTime(timeInSeconds.get());

            }else{
                warnung.setText("Ungültige Eingabe!");
                resetTimeField.fire();
                submit.setDisable(true);
            }
        });



        VBox buttons = new VBox(30, exit, resetTimeField, submit);
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

    private boolean isInputValidAsTimeformat_mm_ss(String exp){
        //Wenn die Expression nicht 5 Zeichen (z.B.: 02:00) oder kein ':' an der richtigen Stelle hat
        if(exp.length() != 5 || exp.toCharArray()[2] != ':') return false;
        //Testen, ob man die Zeichen in int umwandeln kann
        String[] zeichen = exp.split(":");
        try{
            int a = Integer.parseInt(zeichen[0]);
            int b = Integer.parseInt(zeichen[1]);
            //System.out.println("a: " + a + "\nb: " + b);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public void setGoldenScoreTime(int time){
        //time ist in Sekunden
        isGoldenScore = true;
        goldenScoreAbsoluteTime = time;
        resetTimer();
        System.out.println(formatTime(time));
        System.out.println("isGoldenScore = " + isGoldenScore);

        // updateFightControlView();
        updateControlStage();
    }

    // checkt wer gewonnen hat, wenn gleichstand ist dann wird golden score button disabled(false)
    private void checkWinner(){

        stopTimer();

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

        if(chechWinnerStage != null && chechWinnerStage.isShowing()){
            chechWinnerStage.close();
            chechWinnerStage = null;
        }

        chechWinnerStage = new Stage();
        chechWinnerStage.initOwner(controlStage);
        chechWinnerStage.initModality(Modality.WINDOW_MODAL);

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

            ok.setOnAction(actionEvent -> {
                chechWinnerStage.close();

            });

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

            chechWinnerStage.setScene(scn);
        }else{

            Button b01 = new Button(allFighterPairs.get(kampfIndex).getName01());
            Button b02 = new Button(allFighterPairs.get(kampfIndex).getName02());

            //hier if mit css machen, dass button, wo name gleich winner ist, primary button wird

            b01.setOnAction(actionEvent -> {
                allFighterPairs.get(kampfIndex).setWinner(allFighterPairs.get(kampfIndex).getName01());
                allFighterPairs.get(kampfIndex).setWinnerVerein(allFighterPairs.get(kampfIndex).getVerein01());
                for (Verein v : vereine){
                    if(v.getName().equals(allFighterPairs.get(kampfIndex).getVerein01()) ){
                        v.increasePoints(10);
                        break;
                    }
                }
                chechWinnerStage.close();

                allFighterPairs.get(kampfIndex).setDone(true);
                highlightWinner();
            });

            b02.setOnAction(actionEvent -> {
                allFighterPairs.get(kampfIndex).setWinner(allFighterPairs.get(kampfIndex).getName02());
                allFighterPairs.get(kampfIndex).setWinnerVerein(allFighterPairs.get(kampfIndex).getVerein02());
                for (Verein v : vereine){
                    if(v.getName().equals(allFighterPairs.get(kampfIndex).getVerein02()) ){
                        v.increasePoints(10);
                        break;
                    }
                }
                chechWinnerStage.close();
                allFighterPairs.get(kampfIndex).setDone(true);
                highlightWinner();

            });


            Button cancel = new Button("Cancel");
            cancel.setOnAction(actionEvent -> chechWinnerStage.close());


            HBox buttons = new HBox(30, b01, b02, cancel);

            Label info = new Label(Objects.equals(winner, allFighterPairs.get(kampfIndex).getName01()) ? allFighterPairs.get(kampfIndex).getName01() + " hat gewonnen!" : allFighterPairs.get(kampfIndex).getName02() + " hat gewonnen!");



            smallRoot.setTop(info);


            VBox content = new VBox(40, allInfo, buttons);

            smallRoot.setCenter(content);

            Scene scn = new Scene(smallRoot);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) cancel.fire();
            });


            chechWinnerStage.setScene(scn);

        }

        chechWinnerStage.show();

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

    private void highlightWinner(){
        Label winner = new Label(allFighterPairs.get(kampfIndex).getWinner() + " hat gewonnen!");

        String fighter01 = allFighterPairs.get(kampfIndex).getName01();
        String fighter02 = allFighterPairs.get(kampfIndex).getName02();

        Label winnerPoints = new Label();
        Label winnerVerein = new Label();
        Label name02 = new Label();
        Label points02 = new Label();
        Label verein02 = new Label();

        if(allFighterPairs.get(kampfIndex).getWinner().equals(fighter01)){
            System.out.println("winner: " + winner.getText());
            System.out.println("Name01: " + allFighterPairs.get(kampfIndex).getName01());
            System.out.println("Name02: " + allFighterPairs.get(kampfIndex).getName02());

            //wenn 01 Gewinenr*in ist
            int tmp01 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
            if(tmp01 >= 100) tmp01 = 100;
            winnerPoints.setText(""+tmp01);

            winnerVerein.setText(allFighterPairs.get(kampfIndex).getVerein01());

            name02.setText(allFighterPairs.get(kampfIndex).getName02());

            int tmp02 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
            if(tmp02 >= 100) tmp02 = 100;
            points02.setText(""+tmp02);
            verein02.setText(allFighterPairs.get(kampfIndex).getVerein02());

        }else{
            int tmp01 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
            if(tmp01 >= 100) tmp01 = 100;
            winnerPoints.setText(""+tmp01);

            winnerVerein.setText(allFighterPairs.get(kampfIndex).getVerein02());

            name02.setText(allFighterPairs.get(kampfIndex).getName01());

            int tmp02 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
            if(tmp02 >= 100) tmp02 = 100;
            points02.setText(""+tmp02);
            verein02.setText(allFighterPairs.get(kampfIndex).getVerein01());
        }


        System.out.println("winner: " + winner.getText());
        System.out.println("winnerPoints: " + winnerPoints.getText());
        System.out.println("winnerVerein: " + winnerVerein.getText());
        System.out.println("name02: " + name02.getText());
        System.out.println("points02: " + points02.getText());
        System.out.println("verein02: " + verein02.getText());


        VBox box01 = new VBox(10, winner, winnerPoints, winnerVerein);
        VBox box02 = new VBox(10, name02, points02, verein02);
        VBox contents = new VBox(30, box01, box02);


        Button select = new Button("Weiter");
        select.setOnAction(actionEvent -> continueToNextFight());



        VBox allContents = new VBox(20, contents, select);
        mv.hightlightWinner(winner.getText(), winnerPoints.getText(), winnerVerein.getText(), name02.getText(), points02.getText(), verein02.getText());


        //controlRoot.setBottom(null);

        controlRoot.setCenter(allContents);
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