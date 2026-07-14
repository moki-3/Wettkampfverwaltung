package org.example.wettkampfverwaltung;

import java.util.ArrayList;

public class HelpMethods {
    public ArrayList<FighterPair> getEmptyList(){
        ArrayList<FighterPair> list = new ArrayList<>();
        FighterPair tmp1 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "U10", "Keine Altersklasse");
        FighterPair tmp2 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "U12", "Keine Altersklasse");
        FighterPair tmp3 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "U14", "Keine Altersklasse");
        FighterPair tmp4 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "U16", "Keine Altersklasse");
        FighterPair tmp5 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "U18", "Keine Altersklasse");
        FighterPair tmp6 = new FighterPair("Weiß", "Kein Verein", "Blau", "Kein Verein", "Allgemeine Klasse", "Keine Altersklasse");

        list.add(tmp1);
        list.add(tmp2);
        list.add(tmp3);
        list.add(tmp4);
        list.add(tmp5);
        list.add(tmp6);

        return list;
    }

    public ArrayList<String> helpGetFullList(ArrayList<FighterPair> allList){
        ArrayList<String> list = new ArrayList<>();
        for(FighterPair fp : allList){
            String combo01 = fp.getName01() + "|" + fp.getVerein01() + "|" + fp.getAltersKlasse() + "|" + fp.getGewichtsKlasse();
            String combo02 = fp.getName02() + "|" + fp.getVerein02() + "|" + fp.getAltersKlasse() + "|" + fp.getGewichtsKlasse();

            if(!list.contains(combo01)) list.add(combo01);
            if(!list.contains(combo02)) list.add(combo02);
        }
        return list;
    }
}
