package com.talentpath.Battleship.models;

public class Ship {
    private String shipType;
    private Boolean isHorizontal;
    private Point startingSquare;

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public Boolean getHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(Boolean horizontal) {
        isHorizontal = horizontal;
    }

    public Point getStartingSquare() {
        return startingSquare;
    }

    public void setStartingSquare(Point startingSquare) {
        this.startingSquare = startingSquare;
    }
}
