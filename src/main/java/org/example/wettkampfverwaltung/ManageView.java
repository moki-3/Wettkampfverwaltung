package org.example.wettkampfverwaltung;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ManageView {
    private int fightsCount;
    private int actFight;

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


    public void pauseTimer() {
        ;
    }

    public void contTimer(){
        ;
    }

    public void resetTimer(){
        ;
    }
}