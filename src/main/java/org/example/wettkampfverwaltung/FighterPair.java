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
    private boolean done = false; // boolean, um dann styleklassen gut zuzuteilen. Wichtig immer index der liste der fighterPairs speichern und über den index drauf zugreifen

    private int ippon01 = 0;
    private int waza_ari01 = 0;
    private int yuko01 = 0;
    private int shido01 = 0;
    private boolean hansoku_make01 = false;

    public void incIppon01() {
        if(ippon01 + 1 == 1) ippon01++;
        else System.out.println("Ippon01 konnte nicht erhöht werden");
    }
    public void decIppon01() {
        if(ippon01 - 1 == 0) ippon01--;
    }
    public void incWaza_ari01(){
        if(waza_ari01 + 1 <= 2) waza_ari01++;
    }
    public void decWaza_ari01(){
        if(waza_ari01 - 1 >= 0) waza_ari01--;
    }
    public void incYuko01(){
        yuko01++;
    }
    public void decYuko01(){
        if(waza_ari01 - 1 >= 0){
            waza_ari01--;
        }
    }
    public void incShido01(){
        shido01++;
        if(shido01 >= 3){
            shido01 = 3;
            hansoku_make01 = true;
        }else{
            hansoku_make01 = false;
        }
    }
    public void decShido01(){
        if(shido01 - 1 >= 0){
            shido01--;
        }
    }


    private int ippon02 = 0;
    private int waza_ari02 = 0;
    private int yuko02 = 0;
    private int shido02 = 0;
    private boolean hansoku_make02 = false;

    public void incIppon02() {
        if(ippon02 + 1 == 1) ippon02++;
    }
    public void decIppon02() {
        if(ippon02 - 1 == 0) ippon02--;
    }
    public void incWaza_ari02(){
        if(waza_ari02 + 1 <= 2) waza_ari02++;
    }
    public void decWaza_ari02(){
        if(waza_ari02 - 1 >= 0) waza_ari02--;
    }
    public void incYuko02(){
        yuko02++;
    }
    public void decYuko02(){
        if(yuko02 - 1 >= 0){
            yuko02--;
        }
    }
    public void incShido02(){
        shido02++;
        if(shido02 >= 3){
            shido02 = 3;
            hansoku_make02 = true;
        }else{
            hansoku_make02 = false;
        }
    }
    public void decShido02(){
        if(shido02 - 1 >= 0){
            shido02--;
        }
    }

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


    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getWinnerVerein() {
        return winnerVerein;
    }

    public void setWinnerVerein(String winnerVerein) {
        this.winnerVerein = winnerVerein;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getPoints() {
        return points;
    }

    public void increasePoints(int amount) {
        this.points += amount;
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




    public int getIppon01() {
        return ippon01;
    }

    public int getWaza_ari01() {
        return waza_ari01;
    }

    public int getYuko01() {
        return yuko01;
    }

    public int getShido01() {
        return shido01;
    }

    public int getIppon02() {
        return ippon02;
    }

    public int getWaza_ari02() {
        return waza_ari02;
    }

    public int getYuko02() {
        return yuko02;
    }

    public int getShido02() {
        return shido02;
    }

    public boolean isHansoku_make01() {
        return hansoku_make01;
    }

    public boolean isHansoku_make02() {
        return hansoku_make02;
    }
}

