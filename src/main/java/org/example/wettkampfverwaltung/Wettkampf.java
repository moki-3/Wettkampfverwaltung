package org.example.wettkampfverwaltung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import java.util.ArrayList;

public class Wettkampf {

    private ArrayList<FighterPair> fighterPairs = new ArrayList<>();

    public ArrayList<FighterPair> getFighterPairs() {
        return fighterPairs;
    }

    public Wettkampf(ArrayList<FighterPair> list){
        this.fighterPairs = list;
    }

    /*
        TO DO
        * CSS Klassen hinzufügen
     */
    public ScrollPane createList() {
        VBox vbox = new VBox();
        for (FighterPair fp : fighterPairs) {
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
                int index = fighterPairs.indexOf(fp);
                System.out.println("\nvbox geklickt\t index = " + index);
            });

            vbox.getChildren().add(tmp);


        }

        ScrollPane sp = new ScrollPane(vbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // immer vertikal scrollen
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); //bei bedarf horizonal scrollen
        return sp;
    }



}
