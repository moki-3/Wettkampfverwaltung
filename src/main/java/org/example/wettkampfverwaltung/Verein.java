package org.example.wettkampfverwaltung;

/*

Hier werden Vereine Verwaltet, sie haben einen Namen und Punkte, die bei 0 beginnen

 */


public class Verein {
    private String name;
    private int points = 0;
    public boolean showPoints = true;
    public boolean currentlyEdited = false;

    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public boolean getShowPoints() {
        return showPoints;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isCurrentlyEdited() {
        return currentlyEdited;
    }

    public void setCurrentlyEdited(boolean currentlyEdited) {
        this.currentlyEdited = currentlyEdited;
    }

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





























































/*

    +----+         +----+     +--------+    +---+  +---+   +---+      +----------+  +---+  +---+  +---+  +---+
    |     \       /     |    /  +----+  \   |   | /   /    |   |      |         /   |    \ |   |  |   | /   /
    |   +  \     /  +   |   |  /      \  |  |   |/   /     |   |      +---+    /    |     \|   |  |   |/   /
    |   | \ \   / / |   |   | |        | |  |       |      |   |         /    /     |          |  |       |
    |   |  \ +-+ /  |   |   |  \      /  |  |   |\   \     |   |        /    +---+  |   |\     |  |   |\   \
    |   |   +---+   |   |    \  +----+  /   |   | \   \    |   |  /\   /         |  |   | \    |  |   | \   \
    +---+           +---+     +--------+    +---+  +---+   +---+  \/  +----------+  +---+  +---+  +---+  +---+

 */