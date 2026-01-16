package org.example.wettkampfverwaltung;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class ManageView {
    private BorderPane viewRoot;
    private Stage viewStage;

    private int fightsCount;
    public void setFightTime(int fightTime) {
        this.fightTime = fightTime;
    }

    private String altersKlasse;



    private Label timeLabel;
    private Label oaseiKomi01;
    private Label oaseiKomi02;
    private int fightTime;

    private ProgressBar progressbar01;
    private ProgressBar progressbar02;


    public ManageView(int fights){
        this.fightsCount = fights;

        viewRoot = new BorderPane();
        viewStage = new Stage();



        Scene viewScene = new Scene(viewRoot);
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
        Label count = new Label(index + "/" + fightsCount);
        HBox tmpTop = new HBox(count);
        tmpTop.setAlignment(Pos.CENTER);
        viewRoot.setTop(tmpTop);

        updateFight(f); // fügt im Center des BorderPanes den Kampf mit den Daten



        drawBottom();
    }
    /*


        Updatet den Kampf währende dem Kampf, nur den Kampf
     */
    public void updateFight(FighterPair f){
        VBox root = new VBox(); //Die VBox, die dann alles drinnen hat und der content der szene wird.



        // top fighter Box

        Label data01 = new Label(f.getName01() + "\n" + f.getVerein01());
        int points01 = 0;
        points01 = f.getWaza_ari01() * 10 + f.getYuko01();
        if(f.getIppon01() >= 1 || f.getWaza_ari01() == 2) points01 = 100;
        Label displayPoints01 = new Label(points01 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido01 = new Label(f.getShido01() > 0 ? "Shido: " + f.getShido01() : "");

        HBox topBox = new HBox(10, data01, displayPoints01, shido01);

        // lower fighter Box

        Label data02 = new Label(f.getName02() + "\n" + f.getVerein02());
        int points02 = 0;
        points02 = f.getWaza_ari02() * 10 + f.getYuko02();
        if(f.getIppon02() >= 1 || f.getWaza_ari02() == 2) points02 = 100;
        Label displayPoints02 = new Label(points02 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido02 = new Label(f.getShido02() > 0 ? "Shido: " + f.getShido02() : "");

        HBox lowerBox = new HBox(10, shido02, displayPoints02, data02);

        root.getChildren().addAll(topBox, lowerBox);
        viewRoot.setCenter(root);
    }


    /*
        Das wird zwischen den Kämpfen angezeigt
     */
    public void timeFiller(ArrayList<Verein> vereine, FighterPair next){
        VBox root = new VBox();
        for(Verein v : vereine){
            Label l = new Label(v.getName() + ":\t" + v.getPoints());
            root.getChildren().add(l);
        }

        if(next != null){
            Label nextV01 = new Label(next.getVerein01());
            Label nextN01 = new Label(next.getName01());
            Label nextV02 = new Label(next.getVerein02());
            Label nextN02 = new Label(next.getName02());
            Label l = new Label("Nächster Kampf:");

            VBox box01 = new VBox(nextN01, nextV01);
            VBox box02 = new VBox(nextN02, nextV02);
            HBox nextFight = new HBox(box01, box02);
            root.getChildren().addAll(l, nextFight);
        }
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
    }

    public void resetProgressbar02(){
        progressbar02 = null;
    }

    public void initProgressbar01(){
        progressbar01 = new ProgressBar(0);
    }

    public void initProgressbar02(){
        progressbar02 = new ProgressBar(0);
    }

    public void updateProgressbar01(double v){
        if(progressbar01 != null) progressbar01.setProgress(v);
    }

    public void updateProgressbar02(double v){
        if(progressbar02 != null) progressbar02.setProgress(v);
    }

    public void drawBottom(){
        if (timeLabel == null) timeLabel = new Label();
        if (oaseiKomi01 == null) oaseiKomi01 = new Label();
        if (oaseiKomi02 == null) oaseiKomi02 = new Label();
        VBox box01 = new VBox(10, oaseiKomi01);
        VBox box02 = new VBox(10, oaseiKomi02);
        if (progressbar01 != null) box01.getChildren().add(progressbar01);
        if (progressbar02 != null) box02.getChildren().add(progressbar02);

        HBox bottombox = new HBox(10, box01, timeLabel, box02);
            bottombox.setAlignment(Pos.CENTER);
            viewRoot.setBottom(bottombox);
        }

    }