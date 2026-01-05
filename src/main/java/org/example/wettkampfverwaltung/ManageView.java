package org.example.wettkampfverwaltung;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Time;
import java.util.ArrayList;

public class ManageView {
    private int fightsCount; // die Anzahl aller Kämpfe
    private int actFight; // der Aktuelle Kampf

    private static final int U10_time = 120;
    private static final int U12_time = 120;
    private Timeline timeline;
    private Label timeLabel = new Label("");
    private int remainingTime = -1;
    private String currentAltersklasse; //U10 U12


    public void setCurrentAltersklasse(String currentAltersklasse) {
        this.currentAltersklasse = currentAltersklasse;
    }

    public int getFightsCount() {
        return fightsCount;
    }

    public void setFightsCount(int fightsCount) {
        this.fightsCount = fightsCount;
    }

    public int getActFight() {
        return actFight;
    }

    public void incActFight(){ this.actFight++; }

    private boolean isFight;

    public boolean isFight() {
        return isFight;
    }

    public void setFight(boolean fight) {
        isFight = fight;
    }

    private BorderPane viewRoot;
    private Scene viewScene;
    Stage viewStage;



    public ManageView(int fightsCount) {
        this.fightsCount = fightsCount;
        this.actFight = 0;
    }

    public VBox updateView(FighterPair f) {
        VBox root = new VBox(); //Die VBox, die dann alles drinnen hat und der content der szene wird.
        HBox fightCountBox = new HBox(new Label("Kampf " + actFight + "/" + fightsCount));
        fightCountBox.setAlignment(Pos.CENTER);

        // top fighter Box

        Label data01 = new Label(f.getName01() + "\n" + f.getVerein01());
        int points01 = 0;
        points01 = f.getWaza_ari01() * 50 + f.getYuko01();
        if(f.getIppon01() >= 1) points01 = 100;
        Label displayPoints01 = new Label(points01 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido01 = new Label(f.getShido01() > 0 ? "Shido: " + f.getShido01() : "");

        HBox topBox = new HBox(10, data01, displayPoints01, shido01);

        // lower fighter Box

        Label data02 = new Label(f.getName02() + "\n" + f.getVerein02());
        int points02 = 0;
        points02 = f.getWaza_ari02() * 50 + f.getYuko02();
        if(f.getIppon02() >= 1) points02 = 100;
        Label displayPoints02 = new Label(points02 + "");
        //im Shido Label nur etwas anzeigen, wenn es mindestens ein Shido gibt
        Label shido02 = new Label(f.getShido02() > 0 ? "Shido: " + f.getShido02() : "");

        HBox lowerBox = new HBox(10, shido02, displayPoints02, data02);

        //timer fehlt noch

        Label time = new Label("time geht noch nicht, nur zum test");

        root.getChildren().addAll(fightCountBox, topBox, lowerBox, time);

        return root;

    }

    public BorderPane timeFiller(ArrayList<Verein> v, FighterPair next){
        BorderPane bp = new BorderPane();
        VBox root = new VBox();
        for (Verein tmp : v){
            Label l = new Label(tmp.getName() + ":\t" + tmp.getPoints());
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

        bp.setCenter(root);

        return bp;
    }

    private String formatTime(int totalTimeInSeconds){
        int minutes = totalTimeInSeconds / 60;
        int seconds = totalTimeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void startTimer(){
        if(timeline == null){
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
                remainingTime--;
                timeLabel.setText(formatTime(remainingTime));
                if(remainingTime <= 0){
                    timeline.stop();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.play();
    }

    public void stopTimer(){
        if(timeline != null){
            timeline.stop();
        }
    }

    public void resetTimer(){
        if(timeline != null){
            timeline.stop();
        }
        if(currentAltersklasse.equals("U10")) remainingTime = U10_time;
        else if(currentAltersklasse.equals("U12")) remainingTime = U12_time;

        timeLabel.setText(formatTime(remainingTime));

    }

    public int toggleViewStageFullscreen(){
        if(viewStage != null && viewStage.isShowing()){
            viewStage.setFullScreen(!viewStage.isFullScreen());
            return 1;
        }
        return -1;
    }

    public void openViewStage(){
        if(viewStage == null){
            viewStage = new Stage();
            viewRoot = new BorderPane();
            viewRoot.setCenter(new Label("Noch nichts zu sehen"));
            viewScene = new Scene(viewRoot);
            viewStage.setScene(viewScene);
            viewStage.setFullScreenExitHint("");
        }
        viewStage.show();

    }

    public void updateViewStage(FighterPair f, int index, ArrayList<Verein> vereine, FighterPair next){
        if(viewStage != null && viewStage.isShowing()){
            if(isFight){
                viewRoot.setCenter(updateView(f));
            }else{
                if(next != null){
                    viewRoot.setCenter(timeFiller(vereine, next));
                }else{
                    viewRoot.setCenter(timeFiller(vereine, null));
                }
            }
        }
    }

    public void closeStage(){
        if(viewStage != null && viewStage.isShowing()){
            viewStage.close();
            System.exit(0);
        }
    }

    public boolean isViewStageFullscreen(){
        if(viewStage != null && viewStage.isShowing()) return viewStage.isFullScreen();
        return false;
    }

}