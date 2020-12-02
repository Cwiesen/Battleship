package com.talentpath.Battleship.models;

import java.util.Objects;

public class Point{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public String toString() {
        return "x: " + this.x + ", y: " + this.y;
    }
}
