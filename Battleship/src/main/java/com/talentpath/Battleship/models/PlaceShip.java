package com.talentpath.Battleship.models;

public class PlaceShip {

    Integer gameId;
    Integer boardId;
    String shipType;
    Boolean isHorizontal;
    Integer xPos;
    Integer yPos;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getBoardId() {
        return boardId;
    }

    public void setBoardId(Integer boardId) {
        this.boardId = boardId;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public Boolean getisHorizontal() {
        return isHorizontal;
    }

    public void setisHorizontal(Boolean horizontal) {
        isHorizontal = horizontal;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }
}
