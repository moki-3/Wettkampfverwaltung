package org.example.wettkampfverwaltung;

public class Kampf {
    private FighterPair fighters;

    private static final int ZEIT = 2;
    private static final int GOLDENSCORE = 1; // fragen
    private boolean isGoldenScore = false;
    //fragen wie die punkte sind

    private int ipponFirst = 0;
    private int wazariFirst = 0;
    private int yukoFirst = 0;
    private int shidosLeft = 0;

    private int ipponRight = 0;
    private int wazariRight = 0;
    private int yukoRight = 0;
    private int shidosRight = 0;

    public Kampf(FighterPair fighters) {
        this.fighters = fighters;
    }

    public void setWinner(String name){
        fighters.setDone(true);
        if(name.equals(fighters.getName01())){
            fighters.setWinner(fighters.getName01());
            fighters.setWinnerVerein(fighters.getVerein01());

        }
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