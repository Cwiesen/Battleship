package com.talentpath.Battleship.models;

import java.util.List;
import java.util.stream.Collectors;

public class BattleshipBoard {
    private Integer boardId;
    private Integer playerId;
    private List<Ship> placedShips;
    private List<Point> boardHits;

    public BattleshipBoard() {

    }

    public BattleshipBoard(BattleshipBoard toCopy) {
        this.boardId = toCopy.boardId;
        this.playerId = toCopy.playerId;
        this.placedShips = toCopy.getPlacedShips().stream().collect(Collectors.toList());
        this.boardHits = toCopy.getBoardHits().stream().collect(Collectors.toList());
    }

    public Integer getBoardId() {
        return boardId;
    }

    public void setBoardId(Integer boardId) {
        this.boardId = boardId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public List<Ship> getPlacedShips() {
        return placedShips;
    }

    public void setPlacedShips(List<Ship> placedShips) {
        this.placedShips = placedShips;
    }

    public List<Point> getBoardHits() {
        return boardHits;
    }

    public void setBoardHits(List<Point> boardHits) {
        this.boardHits = boardHits;
    }
}
