package org.example.wettkampfverwaltung;

/*

Hier werden Paare von Kämpfern verarbeitet, also jeder einzelne Kampf

 */

public class FighterPair {
    private String name01;
    private String verein01;
    private String name02;
    private String verein02;
    private String altersKlasse;
    private String gewichtsKlasse;

    public String getName01() {
        return name01;
    }

    public String getVerein01() {
        return verein01;
    }

    public String getName02() {
        return name02;
    }

    public String getVerein02() {
        return verein02;
    }

    public String getAltersKlasse() {
        return altersKlasse;
    }

    public String getGewichtsKlasse() {
        return gewichtsKlasse;
    }

    public FighterPair(String name01, String verein01, String name02, String verein02, String altersKlasse, String gewichtsKlasse) {
        this.name01 = name01;
        this.verein01 = verein01;
        this.name02 = name02;
        this.verein02 = verein02;
        this.altersKlasse = altersKlasse;
        this.gewichtsKlasse = gewichtsKlasse;
    }
}
