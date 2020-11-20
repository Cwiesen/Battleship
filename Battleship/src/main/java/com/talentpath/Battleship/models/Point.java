package com.talentpath.Battleship.models;

public class Point {
    //Custom point class for Battleship
    public int x;
    public int y;

    public Point() {

    }

    public Point(Point toCopy) {
        this.x = toCopy.x;
        this.y = toCopy.y;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
