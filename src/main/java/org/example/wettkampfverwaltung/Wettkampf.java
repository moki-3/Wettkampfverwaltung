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
}