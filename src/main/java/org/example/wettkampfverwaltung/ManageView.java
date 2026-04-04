package org.example.wettkampfverwaltung;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;

public class ManageView {
    private BorderPane viewRoot;
    private Stage viewStage;

    private int fightsCount;
    public void setFightTime(int fightTime) {
        this.fightTime = fightTime;
    }

    private Label timeLabel;
    private Label oaseiKomi01;
    private Label oaseiKomi02;
    private int fightTime;

    private ProgressBar progressbar01;
    private ProgressBar progressbar02;

    private Scene viewScene;

    GridPane bottomRoot = null;
    ColumnConstraints leftCol = null;
    ColumnConstraints rightCol = null;
    ColumnConstraints centerCol = null;


    public ManageView(int fights){
        this.fightsCount = fights;
        viewRoot = new BorderPane();
        viewStage = new Stage();
        viewScene = new Scene(viewRoot);
        String css = Objects.requireNonNull(getClass().getResource("/stylesheets/viewStage.css")).toExternalForm();
        viewScene.getStylesheets().add(css);
        viewStage.setFullScreenExitHint("");
        viewStage.setScene(viewScene);
    }

    public void openViewStage(){

        viewStage.setMinWidth(900);
        viewStage.setMinHeight(550);
        viewStage.show();
    }

    public void closeStage(){
        if(viewStage != null && viewStage.isShowing()){
            viewStage.close();
            System.exit(0);
        }
    }



    /*
       Macht die View des Kampfes wenn es einen neuen Kampf gibt, macht
       die Anzeige oben, der wievielte Kampf das ist und fügt die Zeit
       zum BorderPane
     */
    public void newFight(FighterPair f, int index) {
        viewRoot.setCenter(null);
//        Label count = new Label(index + "/" + fightsCount);
//        count.getStyleClass().add("text-25");
//        HBox tmpTop = new HBox(count);
//        tmpTop.setAlignment(Pos.CENTER);
//        viewRoot.setTop(tmpTop);

        updateFight(f); // fügt im Center des BorderPanes den Kampf mit den Daten

        drawBottom();
    }
    /*


        Updatet den Kampf währende dem Kampf, nur den Kampf
     */
    public void updateFight(FighterPair f){
        VBox root = new VBox(); //Die VBox, die dann alles drinnen hat und der content der szene wird.
        VBox.setVgrow(root, Priority.ALWAYS);
        root.setFillWidth(true);


        // top fighter Box

        Label data01 = new Label(f.getName01() + "\n" + f.getVerein01());
        int points01 = 0;
        points01 = f.getWaza_ari01() * 10 + f.getYuko01();
        if(f.getIppon01() >= 1 || f.getWaza_ari01() == 2) points01 = 100;
        Label displayPoints01 = new Label(points01 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido01 = new Label(f.getShido01() > 0 ? "Shido: " + f.getShido01() : "");

        displayPoints01.styleProperty().bind(
                viewScene.heightProperty().divide(5)
                        .asString("-fx-font-size: %.0fpx; -fx-font-weight: bold;")
        );

        data01.styleProperty().bind(
                viewScene.heightProperty().divide(10)
                        .asString("-fx-font-size: %.0fpx;")
        );

        shido01.styleProperty().bind(
                viewScene.heightProperty().divide(20)
                        .asString("-fx-font-size: %.0fpx;")
        );


        HBox topBox = new HBox(100, data01, displayPoints01, shido01);
        topBox.getStyleClass().add("white-box");
        topBox.setAlignment(Pos.CENTER);
        topBox.setMaxWidth(Double.MAX_VALUE);
        topBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(topBox, Priority.ALWAYS);

        // lower fighter Box

        Label data02 = new Label(f.getName02() + "\n" + f.getVerein02());
        int points02 = 0;
        points02 = f.getWaza_ari02() * 10 + f.getYuko02();
        if(f.getIppon02() >= 1 || f.getWaza_ari02() == 2) points02 = 100;
        Label displayPoints02 = new Label(points02 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido02 = new Label(f.getShido02() > 0 ? "Shido: " + f.getShido02() : "");

        displayPoints02.styleProperty().bind(
                viewScene.heightProperty().divide(5)
                        .asString("-fx-font-size: %.0fpx; -fx-font-weight: bold;")
        );

        data02.styleProperty().bind(
                viewScene.heightProperty().divide(10)
                        .asString("-fx-font-size: %.0fpx;")
        );

        shido02.styleProperty().bind(
                viewScene.heightProperty().divide(20)
                        .asString("-fx-font-size: %.0fpx;")
        );

        HBox lowerBox = new HBox(100, data02, displayPoints02, shido02);
        lowerBox.getStyleClass().add("blue-box");
        lowerBox.setAlignment(Pos.CENTER);
        lowerBox.setMaxWidth(Double.MAX_VALUE);
        lowerBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(lowerBox, Priority.ALWAYS);

        root.getChildren().addAll(topBox, lowerBox);
        viewRoot.setCenter(root);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setMaxWidth(Double.MAX_VALUE);
    }


    /*
        Das wird zwischen den Kämpfen angezeigt
     */
    public void timeFiller(ArrayList<Verein> vereine, FighterPair next){
        viewRoot.setBottom(null);
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        for(Verein v : vereine){
            Label l = new Label(v.getName() + ":\t" + v.getPoints());
            l.getStyleClass().add("text-100");
            root.getChildren().add(l);
        }

        if(next != null){
            Label nextV01 = new Label(next.getVerein01());
            nextV01.getStyleClass().add("text-75");
            Label nextN01 = new Label(next.getName01());
            nextN01.getStyleClass().add("text-75");
            Label nextV02 = new Label(next.getVerein02());
            nextV02.getStyleClass().add("text-75");
            Label nextN02 = new Label(next.getName02());
            nextN02.getStyleClass().add("text-75");
            Label l = new Label("Nächster Kampf:");
            l.getStyleClass().add("text-75");

            Label alter = new Label(next.getAltersKlasse());
            alter.getStyleClass().add("text-75");

            VBox box01 = new VBox(20, nextN01, nextV01);
            box01.setAlignment(Pos.CENTER);
            box01.getStyleClass().add("hightlight-White-Box");
            VBox box02 = new VBox(20, nextN02, nextV02);
            box02.setAlignment(Pos.CENTER);
            box02.getStyleClass().add("hightlight-Blue-Box");
            HBox nextFight = new HBox(50, box01, box02);
            nextFight.setAlignment(Pos.CENTER);
            VBox box = new VBox(40, nextFight, alter);
            box.setAlignment(Pos.CENTER);



            //root.getChildren().addAll(l, box);

            VBox all = new VBox(50, l, box);
            all.setAlignment(Pos.CENTER);

            viewRoot.setBottom(all);
        }
        viewRoot.setCenter(null);

        viewRoot.setCenter(root);
    }

    public boolean isViewStageShowing(){
        return viewStage.isShowing();
    }

    public boolean isViewStageFullscreen(){
        if(viewStage != null && viewStage.isShowing()) return viewStage.isFullScreen();
        return false;
    }

    public int toggleViewStageFullscreen(){
        if(viewStage != null && viewStage.isShowing()){
            viewStage.setFullScreen(!viewStage.isFullScreen());
            return 1;
        }
        return -1;
    }

    public void updateTimeLabel(String time){
        if(timeLabel == null) timeLabel = new Label(time);
        else timeLabel.setText(time);
    }

    public void updateOaseiKomi01(String time){
        if(oaseiKomi01 == null) oaseiKomi01 = new Label(time);
        else oaseiKomi01.setText(time);
    }

    public void updateOaseiKomi02(String time){
        if(oaseiKomi02 == null) oaseiKomi02 = new Label(time);
        else oaseiKomi02.setText(time);
    }

    public void resetProgresbar01(){
        progressbar01 = null;
        drawBottom();
    }

    public void resetProgressbar02(){
        progressbar02 = null;
        drawBottom();
    }

    public void initProgressbar01(){
        progressbar01 = new ProgressBar(0);
        progressbar01.getStyleClass().add("progress-bar");
    }

    public void initProgressbar02(){
        progressbar02 = new ProgressBar(0);
        progressbar02.getStyleClass().add("progress-bar");
    }

    public void updateProgressbar01(double v){
        if(progressbar01 != null) progressbar01.setProgress(v);
    }

    public void updateProgressbar02(double v){
        if(progressbar02 != null) progressbar02.setProgress(v);
    }

    public void drawBottom(){

        if(bottomRoot == null){
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
            row.setPercentHeight(100); // oder aufteilen z.B. 50 für zwei Zeilen
            row.setVgrow(Priority.ALWAYS);
            bottomRoot.getRowConstraints().add(row);
        }

        bottomRoot.getChildren().clear();


        if (timeLabel == null) timeLabel = new Label();
        if (oaseiKomi01 == null) oaseiKomi01 = new Label();
        if (oaseiKomi02 == null) oaseiKomi02 = new Label();

        oaseiKomi01.styleProperty().bind(
                bottomRoot.heightProperty().divide(10).asString("-fx-font-size: %.0fpx")
        );
        VBox box01 = new VBox(10, oaseiKomi01);
        box01.getStyleClass().add("background-white");
        box01.setAlignment(Pos.CENTER);
        box01.setMaxHeight(Double.MAX_VALUE);

        if (progressbar01 != null) {
            progressbar01.setMaxWidth(Double.MAX_VALUE);
            box01.getChildren().add(progressbar01);

            GridPane.setFillWidth(box01, true);
            GridPane.setFillHeight(box01, true);

            VBox.setVgrow(oaseiKomi01, Priority.ALWAYS);
            oaseiKomi01.setMaxHeight(Double.MAX_VALUE);

            VBox.setVgrow(progressbar01, Priority.ALWAYS);
            progressbar01.setMaxHeight(Double.MAX_VALUE);

            bottomRoot.add(box01, 0, 0);
        }


        oaseiKomi02.styleProperty().bind(
                bottomRoot.heightProperty().divide(10).asString("-fx-font-size: %.0fpx")
        );
        VBox box02 = new VBox(10, oaseiKomi02);
        box02.getStyleClass().add("background-blue");
        box02.setAlignment(Pos.CENTER);
        box02.setMaxHeight(Double.MAX_VALUE);

        if (progressbar02 != null) {
            progressbar02.setMaxWidth(Double.MAX_VALUE);
            progressbar02.setScaleX(-1);

            box02.getChildren().add(progressbar02);

            GridPane.setFillWidth(box02, true);
            GridPane.setFillHeight(box02, true);

            VBox.setVgrow(oaseiKomi02, Priority.ALWAYS);
            oaseiKomi02.setMaxHeight(Double.MAX_VALUE);

            VBox.setVgrow(progressbar02, Priority.ALWAYS);
            progressbar02.setMaxHeight(Double.MAX_VALUE);

            bottomRoot.add(box02, 2, 0);
        }

        timeLabel.styleProperty().bind(
                bottomRoot.heightProperty().divide(3).asString("-fx-font-size: %.0fpx")
        );

        timeLabel.getStyleClass().add("timerlabel");

        VBox vTimer = new VBox(timeLabel);
        vTimer.getStyleClass().add("timerlabel-vbox");
        vTimer.setAlignment(Pos.CENTER);

        HBox hTimer = new HBox(vTimer);
        hTimer.getStyleClass().add("timerlabel-hBox");
        hTimer.setAlignment(Pos.CENTER);

        GridPane.setFillWidth(hTimer, true);
        bottomRoot.add(hTimer, 1, 0);


        bottomRoot.prefHeightProperty().bind(viewRoot.heightProperty().divide(3));
        bottomRoot.setMaxHeight(Double.MAX_VALUE);
        viewRoot.setBottom(bottomRoot);


    }

    /*
    Diese Methode zeigt an, wer gewonnen hat.
    TODO
    CSS Klassen im if, damit es verschieden Designs gibt, wenn unentschieden ist.
     */
    public void hightlightWinner(String winner, String winnerPoints, String winnerVerein, String name02, String points02, String verein02, boolean isUnentschieden, boolean firstIsBlue){
        if(isUnentschieden){
            Label info = new Label("Unentschieden!");
            info.getStyleClass().add("text-150");
            Label lwinner = new Label(winner);
            lwinner.getStyleClass().add("text-100");
            Label lwinnerPoints = new Label(winnerPoints);
            lwinnerPoints.getStyleClass().add("text-100");
            Label lwinnerVerein = new Label(winnerVerein);
            lwinnerVerein.getStyleClass().add("text-100");
            Label lname02 = new Label(name02);
            lname02.getStyleClass().add("text-100");
            Label lpoints02 = new Label(points02);
            lpoints02.getStyleClass().add("text-100");
            Label lverein02 = new Label(verein02);
            lverein02.getStyleClass().add("text-100");

            VBox box01 = new VBox(10, lwinner,lwinnerPoints, lwinnerVerein);
            box01.setAlignment(Pos.CENTER);
            VBox box02 = new VBox(10, lname02, lpoints02, lverein02);
            box02.setAlignment(Pos.CENTER);

            if(firstIsBlue){
                box01.getStyleClass().add("hightlight-Blue-Box");
                box02.getStyleClass().add("hightlight-White-Box");
            }else{
                box02.getStyleClass().add("hightlight-Blue-Box");
                box01.getStyleClass().add("hightlight-White-Box");
            }

            HBox boxes = new HBox(50, box01, box02);
            boxes.setAlignment(Pos.CENTER);
            VBox contents = new VBox(50, info, boxes);
            contents.setAlignment(Pos.CENTER);
            viewRoot.setCenter(contents);
            viewRoot.setBottom(null);

        }else{
            Label lwinner = new Label(winner);
            lwinner.getStyleClass().add("text-100");
            Label lwinnerPoints = new Label(winnerPoints);
            lwinnerPoints.getStyleClass().add("text-100");
            Label lwinnerVerein = new Label(winnerVerein);
            lwinnerVerein.getStyleClass().add("text-100");
            Label lname02 = new Label(name02);
            lname02.getStyleClass().add("text-100");
            Label lpoints02 = new Label(points02);
            lpoints02.getStyleClass().add("text-100");
            Label lverein02 = new Label(verein02);
            lverein02.getStyleClass().add("text-100");

            VBox box01 = new VBox(10, lwinner, lwinnerPoints, lwinnerVerein);
            box01.setAlignment(Pos.CENTER);
            VBox box02 = new VBox(10, lname02, lpoints02, lverein02);
            box02.setAlignment(Pos.CENTER);
            if(firstIsBlue){
                box01.getStyleClass().add("hightlight-Blue-Box");
                box02.getStyleClass().add("hightlight-White-Box");
            }else{
                box02.getStyleClass().add("hightlight-Blue-Box");
                box01.getStyleClass().add("hightlight-White-Box");
            }
            VBox contents = new VBox(50, box01, box02);
            contents.setAlignment(Pos.CENTER);
            viewRoot.setCenter(contents);
            viewRoot.setBottom(null);
        }
    }
}