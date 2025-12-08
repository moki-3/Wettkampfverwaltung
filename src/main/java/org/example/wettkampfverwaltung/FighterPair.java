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

    private String winner = "nicht gesetzt";
    private String winnerVerein = "nicht gesetzt";
    private int points = 0; // punkte die das gewinnerverein bekommt, zum nachrechnen. Ist ja redundant weil ic das ganze schon in der verein klasse berechne

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

    @Override
    public String toString(){
        return "\nFighter01 = " + getName01() + "\nVerein01 = " + getVerein01() +
                "\nFighter02 = " + getName02() + "\nVerein02 = " + getVerein02() +
                "\nAltersklasse = " + getAltersKlasse() +
                "\nGewichtsklasse = " + getGewichtsKlasse();
    }
}
