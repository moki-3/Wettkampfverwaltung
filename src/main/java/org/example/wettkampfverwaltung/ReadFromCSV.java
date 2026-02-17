package org.example.wettkampfverwaltung;

/*

Hier werden die einzlenen Zeilen der CSV Datei in FighterPairs umgewandelt und
in ein Array gespeichert

 */

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReadFromCSV {

    public ArrayList<FighterPair> read(File csvFile){
        ArrayList<FighterPair> fighterPairs = new ArrayList<>();
        try{
            String gesamtInhalt = java.nio.file.Files.readString(csvFile.toPath());
            String[] zeilen = gesamtInhalt.split("\n");
            for(int i = 0; i < zeilen.length; i++){
                String[] row = zeilen[i].split(",");
                if(row.length == 6){
                    for(int j = 0; j < row.length; j++){
                        row[j] = row[j].replace("\uFEFF", "").trim();
                    }
                    FighterPair fp = new FighterPair(row[0], row[1], row[2], row[3],row[4],row[5]);
                    fighterPairs.add(fp);
                }else{
                    throw new Exception("Zu viele Spalten");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fighterPairs;
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