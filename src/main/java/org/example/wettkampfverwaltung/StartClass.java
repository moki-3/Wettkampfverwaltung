package org.example.wettkampfverwaltung;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import kotlin.coroutines.CombinedContext;

import javax.swing.*;
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
    private int kampfIndex = 0; //akluteller kampf
    private boolean isFight = false; // wenn gerade hajime ist also wirklich jetzt gekämpft wird
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

    private GridPane bottomRoot;
    private ColumnConstraints leftCol;
    private ColumnConstraints centerCol;
    private ColumnConstraints rightCol;
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

    private boolean isCurrentlyAFight = false; // wenn gerade ein Kampf ist. Bleibt auch true, wenn Matte ist.

    private boolean hasCheckWinnerAlreadyBeenCalled = false; //diesen boolean brauche ich um einen bug zu beheben
    private boolean soundHasAlreadyBeenPlayed = false; //gleiches Konzept wie hasCheckWinnerAlreadyBeenCalled



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

        Label greeting = new Label("Wählen Sie eine csv-Datei aus");
        Button select = new Button("Datei auswählen");
        select.getStyleClass().add("select-file-button");
        Button continueButton = new Button("Select a file to continue");
        continueButton.getStyleClass().add("continue-file-button");
        Label fileName = new Label("Keine Datei Ausgewählt");

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
                fileName.setText("Datei: "+csv.getName());
                ReadFromCSV rfc = new ReadFromCSV();
                fighterPairs.set(rfc.read(csv)); // Hier wird die ArrayList gestzt
//                for (FighterPair pair : fighterPairs.get()) {
//                    //hier gebe ich die inhalte der fighterpairs arraylist aus
//                    System.out.println("\n-------------------------------\n");
//                    System.out.println(pair);
//                }

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

        VBox vbox = new VBox(10, greeting, select, fileName,continueButton);
        vbox.setAlignment(Pos.CENTER);

        controlRoot = new BorderPane();
        controlRoot.setCenter(vbox);
        controlRoot.getStyleClass().add("background-ef");

        Scene controlScene = new Scene(controlRoot);

        String css = Objects.requireNonNull(getClass().getResource("/stylesheets/controlStage.css")).toExternalForm();
        controlScene.getStylesheets().add(css);
        stage.setScene(controlScene);






        stage.setTitle("Neuer Wettkampf");
        controlStage = stage;
        controlStage.setMinWidth(300);
        controlStage.setMinHeight(200);
        controlStage.show();
    }


    private void buildLeftControlPane(){
        //Button für viewstage fullscreen
        Button viewStageFullscreen = new Button(mv.isViewStageFullscreen() ? "On" : "Off");
        //css klassen und name

        viewStageFullscreen.getStyleClass().add("mv-fullscreen");

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

        showViewStage.getStyleClass().add("open-mv");

        Label lviewStageFullScreen = new Label("viewStage Vollbildmodus");
        lviewStageFullScreen.getStyleClass().add("mv-fullscreen-label");

        VBox vereineBox = createVereinList();


        Button insert = new Button("Kampf einfügen");
        insert.setOnAction(event -> {
            if(chooseFight){
                controlRoot.setCenter(insertFight());
            }
        });

        insert.getStyleClass().add("open-mv");

        HBox fullscreenbox = new HBox(20, lviewStageFullScreen, viewStageFullscreen);
        fullscreenbox.setAlignment(Pos.CENTER);
        VBox leftControls = new VBox(20, fullscreenbox, showViewStage, insert, vereineBox, createList());
        VBox.setMargin(fullscreenbox, new Insets(10, 10, 0, 20));
        VBox.setMargin(showViewStage, new Insets(0, 10, 0, 20));
        VBox.setMargin(insert, new Insets(0, 10, 0, 20));
        VBox.setMargin(vereineBox, new Insets(10, 10, 10, 20));

        leftControls.getStyleClass().add("background-ddd");

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

            if(fp.getWinner().equals("untentschieden")){
                //Unentschieden nach golden score

                name01.getStyleClass().add("font-red");
                verein01.getStyleClass().add("font-red");
                name02.getStyleClass().add("font-red");
                verein02.getStyleClass().add("font-red");

            }else if (!fp.getWinner().equals("nicht gesetzt")) {
                //wenn es einen gewinner gibt, css klassen setzten
                if (fp.getWinner().equals(name01.getText())) {
                    /*
                        CSS klassen: schrift bei name01 und verein01 grün, und bei name02 und verein02 rot machen
                     */
                    name01.getStyleClass().add("font-green");
                    verein01.getStyleClass().add("font-green");
                    name02.getStyleClass().add("font-red");
                    verein02.getStyleClass().add("font-red");
                } else {
                    /*
                        CSS klassen: schrift bei name02 und verein02 grün, und bei name01 und verein01 rot machen
                     */
                    name01.getStyleClass().add("font-red");
                    verein01.getStyleClass().add("font-red");
                    name02.getStyleClass().add("font-green");
                    verein02.getStyleClass().add("font-green");
                }

            }

            HBox row01 = new HBox(); // 01
            HBox row02 = new HBox(); // 02
            HBox row03 = new HBox(); // altersklasse, gewichtsklasse
            row01.getChildren().addAll(name01, verein01);
            row01.setSpacing(30);
            row02.getChildren().addAll(name02, verein02);
            row02.setSpacing(30);
            row03.getChildren().addAll(altersklasse, gewichtsklasse);
            row03.setSpacing(50);



            //css klassen für die hboxes


            VBox tmp = new VBox(10, row01, row02, row03);
            VBox.setMargin(row01, new Insets(5, 5, 2.5, 15));
            VBox.setMargin(row02, new Insets(2.5, 5, 2.5, 15));
            VBox.setMargin(row03, new Insets(2.5, 5, 5, 15));

            tmp.setPadding(new Insets(2.5, 5, 2.5, 5));

            //css klassen

            tmp.getStyleClass().add("fighter-box");

            tmp.setAlignment(Pos.CENTER);

            if (!fp.getWinner().equals("nicht gesetzt") || fp.isHansoku_make01() || fp.isHansoku_make02()){
                tmp.getStyleClass().add("fighter-box-border-red");
            }else if(kampfIndex == allFighterPairs.indexOf(fp)) {
                tmp.getStyleClass().add("fighter-box-border-orange");
            }else{
                tmp.getStyleClass().add("fighter-box-border-green");
            }




            tmp.setOnMouseClicked(mouseEvent -> {
                int index = allFighterPairs.indexOf(fp);
                System.out.println("\nvbox geklickt\t index = " + index);
                System.out.println("chooseFight: " + chooseFight);
                System.out.println("isDone = " + fp.isDone());
                System.out.println("allFighterPairs.get(index).isDone() = " + allFighterPairs.get(index).isDone());
                if(chooseFight && !fp.isDone()) setNextFight(index);
            });

            vbox.getChildren().add(tmp);

            VBox.setMargin(tmp, new Insets(10));


        }

        ScrollPane sp = new ScrollPane(vbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // immer vertikal scrollen
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); //bei bedarf horizonal scrollen
        sp.getStyleClass().add("background-ddd");
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
        //System.out.println("In updateViewStage");
        //System.out.println("r_flag: " + r_flag);
        //System.out.println("isFight: " + isFight);
        if(isFight || isCurrentlyAFight){
            //mv.updateFight(allFighterPairs.get(kampfIndex));
            //if(allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U10") || allFighterPairs.get(kampfIndex).getAltersKlasse().equals("U12")) mv.updateTimeLabel(formatTime(U10_TIME));
            mv.updateTimeLabel(formatTime(remainingtime));
            mv.newFight(allFighterPairs.get(kampfIndex), kampfIndex);
        }else{
//            if(kampfIndex + 1 < allFighterPairs.size()){
//                mv.timeFiller(vereine, allFighterPairs.get(kampfIndex+1));
//            }else{
//                mv.timeFiller(vereine, null);
//            }
            if(!isCurrentlyAFight) mv.timeFiller(vereine, null);
        }
    }


    /*
        Diese Methode wird verwendet, um den Nächsten kampf zu setzten und
        sie managed auch alles in der ViewStage
        es steht dann "Bitte nächsten kampf auswählen" und man kann links den nächsten kampf auswählen
     */
    public void continueToNextFight(){
        //Kampfindex ändern und nexten kampf für diese Methode zwischenspeichern
        isGoldenScore = false;
        bStartGoldenScore.setDisable(true);
        resetTimer();
        resetOaseiKomi02();
        resetOaseiKomi01();
        buildLeftControlPane();
        System.out.println("in continueToNextFight");
        if(!isCurrentlyAFight) mv.timeFiller(vereine, null);
        hasCheckWinnerAlreadyBeenCalled = false;
        soundHasAlreadyBeenPlayed = false;
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
        frage.getStyleClass().add("text-size-15");

        Label name01 = new Label(allFighterPairs.get(index).getName01());
        Label verein01 = new Label(allFighterPairs.get(index).getVerein01());
        name01.getStyleClass().add("text-size-15");
        verein01.getStyleClass().add("text-size-15");

        Label name02 = new Label(allFighterPairs.get(index).getName02());
        Label verein02 = new Label(allFighterPairs.get(index).getVerein02());
        name02.getStyleClass().add("text-size-15");
        verein02.getStyleClass().add("text-size-15");


        Label alter = new Label(allFighterPairs.get(index).getAltersKlasse());
        alter.getStyleClass().add("next-altersklasse");

        VBox box01 = new VBox(10, name01, verein01);
        VBox box02 = new VBox(10, name02, verein02);
        box01.setAlignment(Pos.CENTER);
        box02.setAlignment(Pos.CENTER);

        box01.getStyleClass().add("nextfighers-white");
        box02.getStyleClass().add("nextfighers-blue");

        HBox nextFighters = new HBox(30, box01, box02);
        nextFighters.setAlignment(Pos.CENTER);


        Button validate = new Button("Weiter");
        validate.setOnAction(actionEvent -> {
            //System.out.println("Weiter geklickt");
            chooseFight = false;
            kampfIndex = index;
            isCurrentlyAFight = true;
            updateControlStage();
        });
        validate.getStyleClass().add("weiter-nextfight");

        validate.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(220), validate);
            scaleUp.setToX(1.3);
            scaleUp.setToY(1.3);
            scaleUp.play();
            validate.getStyleClass().add("background-saturated-orange");
        });

        validate.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), validate);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            validate.getStyleClass().remove("background-saturated-orange");
        });

        Button delete = new Button("Löschen");
        delete.getStyleClass().add("kampf-beenden");
        delete.setOnAction(event -> {
            allFighterPairs.remove(index);
            Label text = new Label("Wähle den nächsten Kampf aus");
            chooseFight = true;
            controlRoot.setCenter(text);
            buildLeftControlPane();
        });

        delete.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(220), delete);
            scaleUp.setToX(0.7);
            scaleUp.setToY(0.7);
            scaleUp.play();
        });

        delete.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), delete);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });



        VBox box = new VBox(20, frage, nextFighters, alter, validate, delete);
        box.setAlignment(Pos.CENTER);
        controlRoot.setCenter(box);
    }


    private void updateControlStage(){
        buildLeftControlPane();
        updateFightControlView();
        ScrollPane sp = new ScrollPane(fightPane);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        controlRoot.setCenter(sp);
        updateViewStage();
    }

    public void play(){
        //Kommentare, weil wenn ich die ProgressBars von anfang an habe, sehe ich sie auch immer, und das will ich nicth
        //progressBar01 = new ProgressBar(0);
        //progressBar02 = new ProgressBar(0);

        bStartGoldenScore = new Button("Golden Score starten");
        bStartGoldenScore.getStyleClass().add("goldenScore-button");
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
        controlStage.setTitle("Wettkampfverwaltung");

        //updateControlStage();
        continueToNextFight();
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
                    playSound();
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
            else if (add02) vereine.add(new Verein(f.getVerein02()));
        }
    }

    private void pauseOaseiKomi01() {
        if (festhalterzeit01 != null) festhalterzeit01.stop();
    }
    private void pauseOaseiKomi02() {
        if (festhalterzeit02 != null) festhalterzeit02.stop();
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
            playSound();
            checkWinner();
        });

        endFight.getStyleClass().add("kampf-beenden");

        String text = "Kampf Starten";
        if(fighTime != null) text = fighTime.getStatus() == Animation.Status.STOPPED ? "Hajime" : "Matte";
        Button start_stop = new Button(text);
        start_stop.getStyleClass().add("kampf-start-stop");
        start_stop.setOnAction(actionEvent -> {
            //System.out.println("\nSpace Clicked");
            if (fighTime == null || fighTime.getStatus() == Animation.Status.STOPPED) {
                startTimer();
                start_stop.setText("Matte");
            } else {
                stopTimer();
                start_stop.setText("Hajime");
            }
            isFight = !isFight;
            System.out.println("\nneues isFight: " + isFight + "\n");
            //start_stop.setText(isFight ? "Matte" : "Hajime");


            if(osae_komi01.getText().equals("Toketa [F]")) pauseOaseiKomi01();
            if(osae_komi02.getText().equals("Toketa [J]")) pauseOaseiKomi02();

            r_flag = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");

            resetOaseiKomi01();
            resetOaseiKomi02();



            updateControlStage();
        });


        HBox topBox = new HBox(30, endFight, start_stop, bStartGoldenScore);

        Label rMode = new Label(r_flag ? "R Modus ein" : "R Modus aus");
        rMode.getStyleClass().add("font-15px");
        HBox rbox = new HBox(10, rMode);
        rbox.setAlignment(Pos.CENTER);

        Label goldenScore = new Label(isGoldenScore ? "Golden Score: " + formatTime(goldenScoreAbsoluteTime) : "");
        goldenScore.getStyleClass().add("font-15px");
        HBox gBox = new HBox(10, goldenScore);
        gBox.setAlignment(Pos.CENTER);

        HBox topButtons = new HBox(20, topBox);
        topButtons.setAlignment(Pos.CENTER);

        VBox top = new VBox(10, rbox, topButtons, gBox);
        VBox.setMargin(rbox, new Insets(10, 0, 0, 0));
        VBox.setMargin(gBox, new Insets(0, 0, 10, 0));


        fightPane.setTop(top);

        // TOP HBOX END
        //################################################################################################
        // UPPER FIGHTER START

        HBox upperFighter = new HBox(20);
        upperFighter.getStyleClass().addAll("font-15px", "background-white", "fighter-boxes");

        Label name01 = new Label(allFighterPairs.get(kampfIndex).getName01());
        Label verein01 = new Label(allFighterPairs.get(kampfIndex).getVerein01());
        name01.getStyleClass().add("font-15px");
        verein01.getStyleClass().add("font-15px");

        VBox daten01 = new VBox(10, name01, verein01);

        String points01 = "0";
        if(allFighterPairs.get(kampfIndex).getIppon01() > 0 || allFighterPairs.get(kampfIndex).getWaza_ari01() >= 2) {
            points01 = "100";
            if(!r_flag) {
                playSound();
                checkWinner();
            }
        }
        else {
            int tmp = 10 * allFighterPairs.get(kampfIndex).getWaza_ari01() + allFighterPairs.get(kampfIndex).getYuko01();
            points01 = tmp + "";
        }


        Label points01l = new Label(points01);
        points01l.getStyleClass().add("font-15px");
        
        Button editIppon01 = new Button("Ippon [A] : " + allFighterPairs.get(kampfIndex).getIppon01());
        editIppon01.getStyleClass().addAll("font-15px", "fight-controls");
        editIppon01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decIppon01();
            }else{
                playSound();
                allFighterPairs.get(kampfIndex).incIppon01();
                checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 1

        Button editWaza_ari01 = new Button("Waza-ari [S] : " + allFighterPairs.get(kampfIndex).getWaza_ari01());
        editWaza_ari01.getStyleClass().addAll("font-15px", "fight-controls");
        editWaza_ari01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decWaza_ari01();
            }else{
                allFighterPairs.get(kampfIndex).incWaza_ari01();
                if(allFighterPairs.get(kampfIndex).getWaza_ari01() >= 2 || isGoldenScore) {
                    playSound();
                    checkWinner();
                }
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 2

        Button editYuko01 = new Button("Yuko [D] : " + allFighterPairs.get(kampfIndex).getYuko01());
        editYuko01.getStyleClass().addAll("font-15px", "fight-controls");
        editYuko01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decYuko01();
            }else{
                allFighterPairs.get(kampfIndex).incYuko01();
                if (isGoldenScore) {
                    playSound();
                    checkWinner();
                }
            }
            r_flag = false;
            updateControlStage();
        });
        // strg + 3

        Button editShido01 = new Button("Shido [G] : " + allFighterPairs.get(kampfIndex).getShido01());
        editShido01.getStyleClass().addAll("font-15px", "fight-controls");
        editShido01.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decShido01();
                allFighterPairs.get(kampfIndex).setHansoku_make01(false);
            }else{
                allFighterPairs.get(kampfIndex).incShido01();
            }
            r_flag = false;
            updateControlStage();
        });


        Button hansoku_make01 = new Button("Hansoku-make");
        hansoku_make01.getStyleClass().addAll("font-15px", "fight-controls");
        //hansoku_make01.setDisable(!allFighterPairs.get(kampfIndex).isHansoku_make01());
        hansoku_make01.setDisable(false);
        hansoku_make01.setOnAction(actionEvent -> {
            playSound();
            allFighterPairs.get(kampfIndex).setHansoku_make01(true);
            checkWinner();
        });

        VBox controls01 = new VBox(10, editIppon01, editWaza_ari01, editYuko01, editShido01, hansoku_make01);

        osae_komi01 = new Button(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
        osae_komi01.getStyleClass().addAll("font-15px", "fight-controls");
        osae_komi01.setOnAction(actionEvent -> {
            if(r_flag) reset01.fire();
            if(osae_komi01.getText().equals("Toketa [F]")){
                stopOaseiKomi01();
                isFesthalter01 = false;
            }else{
                startOaseiKomi01();
                isFesthalter01 = true;
                System.out.println("startOaseiKomi01(); ausgelöst");
            }
            r_flag = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        });

        reset01 = new Button("Reset Oasei-Komi");
        reset01.getStyleClass().addAll("font-15px", "fight-controls-reset-oasei-komi");
        reset01.setOnAction(actionEvent -> {
            resetOaseiKomi01();
            isFesthalter01 = false;
            osae_komi01.setText(isFesthalter01 ? "Toketa [F]" : "Osae-komi [F]");
        });



        upperFighter.getChildren().addAll(daten01, points01l, controls01, osae_komi01, reset01);

        // UPPER FIGHTER END
        //################################################################################################
        // LOWER FITHER START
        HBox lowerFighter = new HBox(20);
        lowerFighter.getStyleClass().addAll("font-15px", "background-blue", "fighter-boxes");

        Label name02 = new Label(allFighterPairs.get(kampfIndex).getName02());
        Label verein02 = new Label(allFighterPairs.get(kampfIndex).getVerein02());
        name02.getStyleClass().add("font-15px");
        verein02.getStyleClass().add("font-15px");


        VBox daten02 = new VBox(10, name02, verein02);

        String points02 = "0";
        if(allFighterPairs.get(kampfIndex).getIppon02() > 0 || allFighterPairs.get(kampfIndex).getWaza_ari02() == 2){
            points02 = "100";
            if(!r_flag) {
                playSound();
                checkWinner();
            }
        }
        else {
            int tmp = 10 * allFighterPairs.get(kampfIndex).getWaza_ari02() + allFighterPairs.get(kampfIndex).getYuko02();
            points02 = tmp + "";
        }

        Label points02l = new Label(points02);
        points02l.getStyleClass().add("font-15px");

        Button editIppon02 = new Button("Ippon [Ö] : " + allFighterPairs.get(kampfIndex).getIppon02());
        editIppon02.getStyleClass().addAll("font-15px", "fight-controls");
        editIppon02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decIppon02();
            }else{
                playSound();
                allFighterPairs.get(kampfIndex).incIppon02();
                checkWinner();
            }
            r_flag = false;
            updateControlStage();
        });


        Button editWaza_ari02 = new Button("Waza-ari [L] : " + allFighterPairs.get(kampfIndex).getWaza_ari02());
        editWaza_ari02.getStyleClass().addAll("font-15px", "fight-controls");
        editWaza_ari02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decWaza_ari02();
            }else{
                allFighterPairs.get(kampfIndex).incWaza_ari02();
                if(allFighterPairs.get(kampfIndex).getWaza_ari02() >= 2 || isGoldenScore) {
                    playSound();
                    checkWinner();
                }
            }
            r_flag = false;
            updateControlStage();
        });


        Button editYuko02 = new Button("Yuko [K] : " + allFighterPairs.get(kampfIndex).getYuko02());
        editYuko02.getStyleClass().addAll("font-15px", "fight-controls");
        editYuko02.setOnAction(actionEvent -> {
            if (r_flag) {
                allFighterPairs.get(kampfIndex).decYuko02();
            }else{
                allFighterPairs.get(kampfIndex).incYuko02();
                if (isGoldenScore) {
                    playSound();
                    checkWinner();
                }
            }
            r_flag = false;
            updateControlStage();
        });

        Button editShido02 = new Button("Shido [H] : " + allFighterPairs.get(kampfIndex).getShido02());
        editShido02.getStyleClass().addAll("font-15px", "fight-controls");
        editShido02.setOnAction(actionEvent -> {
            if(r_flag){
                allFighterPairs.get(kampfIndex).decShido02();
                allFighterPairs.get(kampfIndex).setHansoku_make02(false);
            }else{
                allFighterPairs.get(kampfIndex).incShido02();
            }
            r_flag = false;
            updateControlStage();
        });


        Button hansoku_make02 = new Button("Hansoku-make");
        //hansoku_make02.setDisable(!allFighterPairs.get(kampfIndex).isHansoku_make02());
        hansoku_make02.getStyleClass().addAll("font-15px", "fight-controls");
        hansoku_make02.setDisable(false);
        hansoku_make02.setOnAction(actionEvent -> {
            playSound();
            allFighterPairs.get(kampfIndex).setHansoku_make02(true);
            checkWinner();
        });

        VBox controls02 = new VBox(10, editIppon02, editWaza_ari02, editYuko02, editShido02, hansoku_make02);

        osae_komi02 = new Button(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        osae_komi02.getStyleClass().addAll("font-15px", "fight-controls");
        osae_komi02.setOnAction(actionEvent -> {
            if(r_flag) reset02.fire();
            if(osae_komi02.getText().equals("Toketa [J]")){
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
        reset02.getStyleClass().addAll("font-15px", "fight-controls-reset-oasei-komi");
        reset02.setOnAction(actionEvent -> {
            resetOaseiKomi02();
            isFesthalter02 = false;
            osae_komi02.setText(isFesthalter02 ? "Toketa [J]" : "Osae-komi [J]");
        });

        lowerFighter.getChildren().addAll(daten02, points02l, controls02, osae_komi02, reset02);

        // LOWER FITHER END
        //################################################################################################


        VBox contents = new VBox(upperFighter, lowerFighter);
        VBox.setMargin(lowerFighter, new Insets(20, 0, 0, 0));
        contents.setAlignment(Pos.CENTER);
        HBox container = new HBox(contents); //Nur damit ich alles zentrieren kann
        container.setAlignment(Pos.CENTER);
        fightPane.setCenter(container);
        fightPane.setFocusTraversable(true);

        controlStage.getScene().setOnKeyTyped(keyEvent -> {
            switch (keyEvent.getCharacter().toLowerCase()) {
                case "a" -> { editIppon01.fire(); }
                case "ö" -> { editIppon02.fire(); }
                case "s" -> { editWaza_ari01.fire(); }
                case "l" -> { editWaza_ari02.fire(); }
                case "d" -> { editYuko01.fire(); }
                case "k" -> { editYuko02.fire(); }
                case "f" -> { osae_komi01.fire(); }
                case "j" -> { osae_komi02.fire(); }
                case "g" -> { editShido01.fire(); }
                case "h" -> { editShido02.fire(); }
                case " " -> { start_stop.fire(); }
                case "r" -> { r_flag = !r_flag; updateControlStage(); }
            }

            editIppon01.setFocusTraversable(false);
            editIppon02.setFocusTraversable(false);
            editWaza_ari01.setFocusTraversable(false);
            editYuko01.setFocusTraversable(false);
            editShido01.setFocusTraversable(false);
            editShido02.setFocusTraversable(false);
            editYuko02.setFocusTraversable(false);
            editWaza_ari02.setFocusTraversable(false);
            start_stop.setFocusTraversable(false);


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
                progressBar01.getStyleClass().add("progress-bar");
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
                    playSound();
                    progressBar01.setProgress(1.0);
                    mv.updateProgressbar01(1.0);
                    allFighterPairs.get(kampfIndex).incWaza_ari01();
                    checkWinner();
                }else if(remainingOaseKomi == OASEI_KOMI){
                    stopOaseiKomi01();
                    playSound();
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
                progressBar02.getStyleClass().add("progress-bar");
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
                    playSound();
                    progressBar02.setProgress(1.0);
                    mv.updateProgressbar02(1.0);
                    allFighterPairs.get(kampfIndex).incWaza_ari02();
                    checkWinner();
                }else if(remainingOaseKomi == OASEI_KOMI){
                    stopOaseiKomi02();
                    playSound();
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

        if(bottomRoot == null) {
            bottomRoot = new GridPane();
            bottomRoot.getStyleClass().add("grid-pane");
            leftCol = new ColumnConstraints();
            leftCol.setPercentWidth(40);
            leftCol.setHgrow(Priority.ALWAYS);

            rightCol = new ColumnConstraints();
            rightCol.setPercentWidth(40);
            rightCol.setHgrow(Priority.ALWAYS);

            centerCol = new ColumnConstraints();
            centerCol.setPercentWidth(20);
            centerCol.setHgrow(Priority.ALWAYS);

            bottomRoot.getColumnConstraints().addAll(leftCol, centerCol, rightCol);

            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setFillHeight(true);
            bottomRoot.getRowConstraints().add(row);
        }

        //fightPane.setBottom(timerLabel);
        bottomRoot.getChildren().clear();


        festhalertLabel02.getStyleClass().add("festhalter-zeit");
        VBox box02 = new VBox(10, festhalertLabel02);
        box02.setAlignment(Pos.CENTER);
        box02.setMaxHeight(Double.MAX_VALUE);
        box02.getStyleClass().add("background-blue");
        if(progressBar02 != null) {
            progressBar02.setMaxWidth(Double.MAX_VALUE);
            progressBar02.setScaleX(-1);
            box02.getChildren().add(progressBar02);

            GridPane.setFillWidth(box02, true);
            GridPane.setFillHeight(box02, true);

            VBox.setVgrow(festhalertLabel02, Priority.ALWAYS);
            festhalertLabel02.setMaxHeight(Double.MAX_VALUE);

            VBox.setVgrow(progressBar02, Priority.ALWAYS);
            progressBar02.setMaxHeight(Double.MAX_VALUE);

            bottomRoot.add(box02, 2, 0);
        }


        festhalertLabel01.getStyleClass().add("festhalter-zeit");
        VBox box01 = new VBox(10, festhalertLabel01);
        box01.setAlignment(Pos.CENTER);
        box01.setMaxHeight(Double.MAX_VALUE);
        box01.getStyleClass().add("background-white");
        if(progressBar01 != null) {
            progressBar01.setMaxWidth(Double.MAX_VALUE);
            box01.getChildren().add(progressBar01);

            GridPane.setFillWidth(box01, true);
            GridPane.setFillHeight(box01, true);

            VBox.setVgrow(festhalertLabel01, Priority.ALWAYS);
            festhalertLabel01.setMaxHeight(Double.MAX_VALUE);

            VBox.setVgrow(progressBar01, Priority.ALWAYS);
            progressBar01.setMaxHeight(Double.MAX_VALUE);

            bottomRoot.add(box01, 0, 0);
        }


        timerLabel.getStyleClass().add("timerlabel");

        VBox vTimer = new VBox(timerLabel);
        vTimer.getStyleClass().add("timerlabel-vbox");
        vTimer.setAlignment(Pos.CENTER);

        HBox hTimer = new HBox(vTimer);
        hTimer.getStyleClass().add("timerlabel-hBox");
        hTimer.setAlignment(Pos.CENTER);

        GridPane.setFillWidth(hTimer, true);
        bottomRoot.add(hTimer, 1, 0);

        //bottomRoot.getChildren().addAll(box01, timerLabel, box02); als es noch eine HBox war
        //bottomRoot.setAlignment(Pos.CENTER); als es noch eine HBox war
        if(fightPane != null)fightPane.setBottom(bottomRoot);
    }


    public void startGoldeScore(){
        Stage goldenScoreStage = new Stage();
        goldenScoreStage.initOwner(controlStage);
        goldenScoreStage.initModality(Modality.WINDOW_MODAL);

        Label start = new Label("Golden Score starten");
        start.getStyleClass().add("text-20");
        Label warnung = new Label("");
        TextField timeField = new TextField("02:00");
        timeField.getStyleClass().add("eingabe");
        timeField.setMaxWidth(150);
        AtomicInteger timeInSeconds = new AtomicInteger();


        Button submit = new Button("Fertig");
        submit.getStyleClass().add("submit");
        submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));

        //submit.setDisable(true);

//        timeField.setOnAction(actionEvent -> {
//            submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
//        });
        timeField.setOnKeyTyped(keyEvent -> {
            submit.setDisable(!isInputValidAsTimeformat_mm_ss(timeField.getText()));
        });
        timeField.setOnAction(actionEvent -> submit.fire());

        Button resetTimeField = new Button("Reset Input");
        resetTimeField.getStyleClass().add("reset");
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
                hasCheckWinnerAlreadyBeenCalled = false;
                soundHasAlreadyBeenPlayed = false;
                setGoldenScoreTime(timeInSeconds.get());

            }else{
                warnung.setText("Ungültige Eingabe!");
                resetTimeField.fire();
                submit.setDisable(true);
            }
        });




        VBox all = new VBox(10, start, warnung, timeField, resetTimeField, submit);
        all.setAlignment(Pos.CENTER);


        BorderPane goldenRoot = new BorderPane();


        goldenRoot.setCenter(all);
        Scene scn = new Scene(goldenRoot);

        String css = Objects.requireNonNull(getClass().getResource("/stylesheets/goldenScoreStage.css")).toExternalForm();
        scn.getStylesheets().add(css);

        scn.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goldenScoreStage.close();
        });

        goldenScoreStage.setScene(scn);
        goldenScoreStage.setMinHeight(250);
        goldenScoreStage.setMinWidth(250);
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
        if(hasCheckWinnerAlreadyBeenCalled) return;
        hasCheckWinnerAlreadyBeenCalled = true;
        stopTimer();

        //playSound();

        int points01 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
        int points02 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
        if(points01 >= 100) points01 = 100;
        if(points02 >= 100) points02 = 100;

        String winner = "-1";

        boolean unentschiedenNachGoldenScore = false;

        if(points01 == points02 && !isGoldenScore) {
            bStartGoldenScore.setDisable(false); // wenn gleichstand kann goldenScore gemacht werden
        }else if(points01 == points02 && isGoldenScore){
            unentschiedenNachGoldenScore = true;
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
        chechWinnerStage.setTitle("Kampf zuende");
        chechWinnerStage.initOwner(controlStage);
        chechWinnerStage.initModality(Modality.WINDOW_MODAL);

        BorderPane smallRoot = new BorderPane();
        smallRoot.getStyleClass().add("background-df");

        Label who01 = new Label(allFighterPairs.get(kampfIndex).getName01());
        Label ippons01 = new Label("Ippons: " + allFighterPairs.get(kampfIndex).getIppon01());
        Label waza_aris01 = new Label("Waza-aris: " + allFighterPairs.get(kampfIndex).getWaza_ari01());
        Label yukos01 = new Label("Yukos: " + allFighterPairs.get(kampfIndex).getYuko01());
        Label shidos01 = new Label("shidos: " + allFighterPairs.get(kampfIndex).getShido01());

        who01.getStyleClass().add("text-20");
        ippons01.getStyleClass().add("text-15");
        waza_aris01.getStyleClass().add("text-15");
        yukos01.getStyleClass().add("text-15");
        shidos01.getStyleClass().add("text-15");

        VBox labels01 = new VBox(10, who01, ippons01, waza_aris01, yukos01, shidos01);


        Label who02 = new Label(allFighterPairs.get(kampfIndex).getName02());
        Label ippons02 = new Label("Ippons: " + allFighterPairs.get(kampfIndex).getIppon02());
        Label waza_aris02 = new Label("Waza-aris: " + allFighterPairs.get(kampfIndex).getWaza_ari02());
        Label yukos02 = new Label("Yukos: " + allFighterPairs.get(kampfIndex).getYuko02());
        Label shidos02 = new Label("shidos: " + allFighterPairs.get(kampfIndex).getShido02());

        who02.getStyleClass().add("text-20");
        ippons02.getStyleClass().add("text-15");
        waza_aris02.getStyleClass().add("text-15");
        yukos02.getStyleClass().add("text-15");
        shidos02.getStyleClass().add("text-15");

        VBox labels02 = new VBox(10, who02, ippons02, waza_aris02, yukos02, shidos02);

        HBox allInfo = new HBox(20, labels01, labels02);
        allInfo.setAlignment(Pos.CENTER);


        chechWinnerStage.setMinWidth(300);
        chechWinnerStage.setMinHeight(400);


        if(winner == "-1" && !unentschiedenNachGoldenScore && !allFighterPairs.get(kampfIndex).isHansoku_make01() && !allFighterPairs.get(kampfIndex).isHansoku_make02()){
            //wenn untentschieden vor Golden Score

            Label info = new Label("Unentschieden!");
            info.getStyleClass().add("text-30");
            HBox infoContainer = new HBox(info);
            infoContainer.setAlignment(Pos.CENTER);

            VBox content = new VBox(50, infoContainer, allInfo);
            smallRoot.setCenter(content);

            bStartGoldenScore.setDisable(false);

            Button ok = new Button("ok");
            ok.getStyleClass().add("primaryButton");

            ok.setOnAction(actionEvent -> {
                chechWinnerStage.close();
            });

            Button disable = new Button("Ich brauche noch kein Golden Score");
            disable.getStyleClass().add("secondaryButton");
            disable.setOnAction(actionEvent -> {
                bStartGoldenScore.setDisable(true);
                hasCheckWinnerAlreadyBeenCalled = false;
                ok.fire();
            });

            VBox buttons = new VBox(20, ok, disable);
            buttons.setAlignment(Pos.CENTER);

            smallRoot.setBottom(buttons);

            Scene scn = new Scene(smallRoot);
            String css = Objects.requireNonNull(getClass().getResource("/stylesheets/checkWinnerStage.css")).toExternalForm();
            scn.getStylesheets().add(css);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.ESCAPE) ok.fire();
            });

            chechWinnerStage.setOnCloseRequest(event -> ok.fire());

            chechWinnerStage.setScene(scn);
        }else if(unentschiedenNachGoldenScore && !allFighterPairs.get(kampfIndex).isHansoku_make01() && !allFighterPairs.get(kampfIndex).isHansoku_make02()){
            //System.out.println("im unentschiedenNachGoldenScore if");
            /*
            unentschieden nach golden score

            Wenn ich hier bin, wird in der Stage, die dem Controler angezeigt wird,
            schon angezeigt, das unentschieden ist.
            Ich muss dann noch hier in Controlstage anzeigen, dass untschieden ist und
            in der viewstage
             */
            Label info = new Label("Unentschieden");
            info.getStyleClass().add("text-30");

            HBox infoContainer = new HBox(info);
            infoContainer.setAlignment(Pos.CENTER);



            Button ok = new Button("ok");
            ok.getStyleClass().add("primaryButton");

            ok.setOnAction(actionEvent -> {
                /*
                Alles setzten damit ein Unentschieden richtig in der viewstage und in der
                Controlstage angezeigt wird
                hightlightWinner mit unentschieden = true
                 */
                allFighterPairs.get(kampfIndex).setWinner("untentschieden");
                for (Verein v : vereine){
                    if(v.getName().equals(allFighterPairs.get(kampfIndex).getVerein01()) || v.getName().equals(allFighterPairs.get(kampfIndex).getVerein02())){
                        v.increasePoints(3);
                    }
                }
                chechWinnerStage.close();
                allFighterPairs.get(kampfIndex).setDone(true);
                highlightWinner();
            });

            VBox content = new VBox(50, infoContainer, allInfo, ok);
            content.setAlignment(Pos.CENTER);
            smallRoot.setCenter(content);

            Scene scn = new Scene(smallRoot);
            String css = Objects.requireNonNull(getClass().getResource("/stylesheets/checkWinnerStage.css")).toExternalForm();
            scn.getStylesheets().add(css);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.ESCAPE) ok.fire();
            });

            chechWinnerStage.setOnCloseRequest(event -> ok.fire());

            chechWinnerStage.setScene(scn);

        }else{

            chechWinnerStage.setMinHeight(300);

            if(allFighterPairs.get(kampfIndex).isHansoku_make01()){
                System.out.println("01 hat ein hansoku make");
                chechWinnerStage.setTitle(allFighterPairs.get(kampfIndex).getName01() + " hat ein Hansoku make");
                setAllFightsDoneWithName(allFighterPairs.get(kampfIndex).getName01());

            }
            if(allFighterPairs.get(kampfIndex).isHansoku_make02()){
                System.out.println("02 hat ein hansoku make");
                chechWinnerStage.setTitle(allFighterPairs.get(kampfIndex).getName02() + " hat ein Hansoku make");
                setAllFightsDoneWithName(allFighterPairs.get(kampfIndex).getName02());
            }

            Button b01 = new Button(allFighterPairs.get(kampfIndex).getName01());
            b01.getStyleClass().add("white-button");
            Button b02 = new Button(allFighterPairs.get(kampfIndex).getName02());
            b02.getStyleClass().add("blue-button");

            //hier if mit css machen, dass button, wo name gleich winner ist, primary button wird
            if(winner.equals(b01.getText())){
                b01.getStyleClass().add("winner-marker");
            }else{
                b02.getStyleClass().add("winner-marker");
            }


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
            cancel.setOnAction(actionEvent -> {
                chechWinnerStage.close();
                hasCheckWinnerAlreadyBeenCalled = false;
            });

            cancel.getStyleClass().add("cancel");



            HBox fighterButtons = new HBox(20, b01, b02);
            fighterButtons.setAlignment(Pos.CENTER);

            VBox buttons = new VBox(20, fighterButtons, cancel);
            buttons.setAlignment(Pos.CENTER);

            Label info = new Label(Objects.equals(winner, allFighterPairs.get(kampfIndex).getName01()) ? allFighterPairs.get(kampfIndex).getName01() + " hat gewonnen!" : allFighterPairs.get(kampfIndex).getName02() + " hat gewonnen!");
            info.getStyleClass().add("text-30");
            HBox infoContainer = new HBox(info);
            infoContainer.setAlignment(Pos.CENTER);


            smallRoot.setTop(infoContainer);


            VBox content = new VBox(40, allInfo, buttons);

            smallRoot.setCenter(content);

            Scene scn = new Scene(smallRoot);
            String css = Objects.requireNonNull(getClass().getResource("/stylesheets/checkWinnerStage.css")).toExternalForm();
            scn.getStylesheets().add(css);

            scn.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) cancel.fire();
            });


            chechWinnerStage.setScene(scn);

        }



        chechWinnerStage.show();

    }


    /*
    Diese Methode zeigt nach checkWinner, wenn es einen Winner gibt und man auf diesen Namen bei checkWinner
    geklickt hat, in Controlstage und Viewstagen den Winner an.
    TODO
    CSS Klassen in den ifs, damit es verschieden Designs gibt, wenn es Unentschieden ist und wenn nicht
     */
    private void highlightWinner(){
        //System.out.println("In highlightWinner");
        //wenn ein Kampf fertig ist, kommt man hier her
        isCurrentlyAFight = false;
        //eigentlich sollte done schon auf true gesetzt sein aber sicher ist sicher
        allFighterPairs.get(kampfIndex).setDone(true);

        Label winner = new Label(allFighterPairs.get(kampfIndex).getWinner() + " hat gewonnen!");


        String fighter01 = allFighterPairs.get(kampfIndex).getName01();
        String fighter02 = allFighterPairs.get(kampfIndex).getName02();

        Label winnerPoints = new Label();
        Label winnerVerein = new Label();
        Label name02 = new Label();
        Label points02 = new Label();
        Label verein02 = new Label();

        Label lunentschieden = new Label("");

        boolean unentschieden = false;

        if(allFighterPairs.get(kampfIndex).getWinner().toLowerCase().equals("untentschieden")){
            //System.out.println("\nIm unentschieden if in highlightWinner!\n");
            //wenn unentschieden nach Golden Score
            winner.setText(allFighterPairs.get(kampfIndex).getName01());
            //einfach 01 auf gewinner machen, weil weiß ja eh niemand und ich habe die Labels schon
            int tmp01 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
            if(tmp01 >= 100) tmp01 = 100;
            winnerPoints.setText(""+tmp01);

            winnerVerein.setText(allFighterPairs.get(kampfIndex).getVerein01());

            name02.setText(fighter02);

            int tmp02 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
            if(tmp02 >= 100) tmp02 = 100;
            points02.setText(""+tmp02);
            verein02.setText(allFighterPairs.get(kampfIndex).getVerein02());


            unentschieden = true;
            lunentschieden.setText("Unentschieden");
            lunentschieden.getStyleClass().add("text-20");


        }else if(allFighterPairs.get(kampfIndex).getWinner().equals(fighter01)){
//            System.out.println("winner: " + winner.getText());
//            System.out.println("Name01: " + allFighterPairs.get(kampfIndex).getName01());
//            System.out.println("Name02: " + fighter02);

            //wenn 01 Gewinenr*in ist
            int tmp01 = allFighterPairs.get(kampfIndex).getIppon01() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari01() * 10 + allFighterPairs.get(kampfIndex).getYuko01();
            if(tmp01 >= 100) tmp01 = 100;
            winnerPoints.setText(""+tmp01);

            winnerVerein.setText(allFighterPairs.get(kampfIndex).getVerein01());

            name02.setText(fighter02);

            int tmp02 = allFighterPairs.get(kampfIndex).getIppon02() * 100 + allFighterPairs.get(kampfIndex).getWaza_ari02() * 10 + allFighterPairs.get(kampfIndex).getYuko02();
            if(tmp02 >= 100) tmp02 = 100;
            points02.setText(""+tmp02);
            verein02.setText(allFighterPairs.get(kampfIndex).getVerein02());
            lunentschieden.setText("");

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
            lunentschieden.setText("");
        }


//        System.out.println("winner: " + winner.getText());
//        System.out.println("winnerPoints: " + winnerPoints.getText());
//        System.out.println("winnerVerein: " + winnerVerein.getText());
//        System.out.println("name02: " + name02.getText());
//        System.out.println("points02: " + points02.getText());
//        System.out.println("verein02: " + verein02.getText());


        VBox box01 = new VBox(10, winner, winnerPoints, winnerVerein);
        box01.getStyleClass().add("highlightWinner");

        VBox box02 = new VBox(10, name02, points02, verein02);
        box02.getStyleClass().add("highlightWinner");

        HBox contents = new HBox(30, box01, box02);

        boolean firstIsBlue = false;

        if(!unentschieden && winner.getText().contains(allFighterPairs.get(kampfIndex).getName01())){
            box01.getStyleClass().add("background-white");
            box02.getStyleClass().add("background-blue");
        } else if (!unentschieden && winner.getText().contains(allFighterPairs.get(kampfIndex).getName02())) {
            box02.getStyleClass().add("background-white");
            box01.getStyleClass().add("background-blue");
            firstIsBlue = true;
        }else{
            box02.getStyleClass().add("background-gray");
            box01.getStyleClass().add("background-gray");
        }


        Button submit = new Button("Weiter");
        submit.getStyleClass().add("submit");
        submit.setOnAction(actionEvent -> continueToNextFight());



        VBox allContents = new VBox(20, lunentschieden,contents, submit);
        allContents.setAlignment(Pos.CENTER);        HBox allContentsHorizontl = new HBox(allContents);
        allContentsHorizontl.setAlignment(Pos.CENTER);

        mv.hightlightWinner(winner.getText(), winnerPoints.getText(), winnerVerein.getText(), name02.getText(), points02.getText(), verein02.getText(), unentschieden, firstIsBlue);
        System.out.println("winner.getText = " + winner.getText());

        //controlRoot.setBottom(null);

        controlRoot.setCenter(allContentsHorizontl);
    }

    private void setAllFightsDoneWithName(String name){
        for (FighterPair fp : allFighterPairs){
            if(fp.getName01().equals(name) || fp.getName02().equals(name)) {
                fp.setDone(true);
                if(fp.getName01().equals(name)) fp.setHansoku_make01(true);
                else fp.setHansoku_make02(true);
            }
        }
        buildLeftControlPane();
    }

    private void playSound(){
        if(!soundHasAlreadyBeenPlayed) {
            SoundPlayer sp = new SoundPlayer();
            sp.playEndFight();
        }
        soundHasAlreadyBeenPlayed = true;
    }


    private ArrayList<String> getFullList(){
        ArrayList<String> list = new ArrayList<>();
        for(FighterPair fp : allFighterPairs){
            String combo01 = fp.getName01() + "|" + fp.getVerein01() + "|" + fp.getAltersKlasse() + "|" + fp.getGewichtsKlasse();
            String combo02 = fp.getName02() + "|" + fp.getVerein02() + "|" + fp.getAltersKlasse() + "|" + fp.getGewichtsKlasse();

            if(!list.contains(combo01)) list.add(combo01);
            if(!list.contains(combo02)) list.add(combo02);
        }
        return list;
    }

    private VBox insertFight(){
        Label verein01 = new Label("Kein name01 ausgewählt!");
        Label verein02 = new Label("Kein name02 ausgewählt!");
        verein02.getStyleClass().add("text-size-15");
        verein01.getStyleClass().add("text-size-15");
        ComboBox<String> alter = new ComboBox<>();
        ComboBox<String> gewicht = new ComboBox<>();




        ComboBox<String> name01 = new ComboBox<>();
        ComboBox<String> name02 = new ComboBox<>();


        ArrayList<String> all = getFullList();
        for(String s : all){
            String[] tmp = s.split("\\|");
            name01.getItems().add(tmp[0]);
        }

        for(String s : all){
            String[] tmp = s.split("\\|");
            name02.getItems().add(tmp[0]);
        }

        name01.setOnAction(event -> {
            String choosen = name01.getValue();
            String match = "";
            for(String s : all){
                if(s.contains(choosen)){
                    match = s;
                    break;
                }
            }

            String[] matchArray = match.split("\\|");

            verein01.setText(matchArray[1]);
            alter.getItems().add(matchArray[2]);
            gewicht.getItems().add(matchArray[3]);
        });


        name02.setOnAction(event -> {
            String choosen = name02.getValue();
            String match = "";
            for(String s : all){
                if(s.contains(choosen)){
                    match = s;
                    break;
                }
            }

            String[] matchArray = match.split("\\|");

            verein02.setText(matchArray[1]);
            alter.getItems().add(matchArray[2]);
            gewicht.getItems().add(matchArray[3]);
        });


        HBox box01 = new HBox(20, name01, verein01);
        HBox box02 = new HBox(20, name02, verein02);
        HBox box03 = new HBox(20, gewicht, alter);
        box01.setAlignment(Pos.CENTER);
        box02.setAlignment(Pos.CENTER);
        box03.setAlignment(Pos.CENTER);

        Label info = new Label("");
        info.getStyleClass().add("text-size-15");



        Button submit = new Button("Übernehmen");
        submit.getStyleClass().add("submit");
        submit.setOnAction(event -> {
            if(name01.getValue() != null && !verein01.getText().equals("Kein name01 ausgewählt!") && name02.getValue() != null && !verein02.getText().equals("Kein name02 ausgewählt!") && alter.getValue() != null && gewicht.getValue() != null) {
                FighterPair tmp = new FighterPair(name01.getValue(), verein01.getText(), name02.getValue(), verein02.getText(), alter.getValue(), gewicht.getValue());
                info.setText("");
                allFighterPairs.add(tmp);
                buildLeftControlPane();
                info.setText("Erfolgreich übernommen!");
                info.getStyleClass().remove("font-red");
            }else{
                info.setText("Error: bitte alle Werte setzen!");
                info.getStyleClass().add("font-red");
            }
        });






        VBox root = new VBox(20, box01, box02, box03, submit, info);


        root.setAlignment(Pos.CENTER);


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