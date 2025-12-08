package org.example.wettkampfverwaltung;

/*

Hier werden Vereine Verwaltet, sie haben einen Namen und Punkte, die bei 0 beginnen

 */


public class Verein {
    private String name;
    private int points = 0;

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void increasePoints(int amount){
        points += amount;
    }

    public Verein(String name) {
        this.name = name;
    }
}
