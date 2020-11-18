package com.talentpath.Battleship.models;

import com.talentpath.Battleship.daos.BattleshipDao;

public class BattleshipGame {
    private Integer gameId;
    private BattleshipBoard player1;
    private BattleshipBoard player2;
    //playerTurn valid values:
    // 0: Game Set up, 1: Player 1 turn, 2: Player 2 turn
    private Integer playerTurn;

    public BattleshipGame() {
        gameId = 0;
        player1 = new BattleshipBoard();
        player2 = new BattleshipBoard();
        playerTurn = 0;
    }

    public BattleshipGame(BattleshipGame toCopy) {
        this.gameId = toCopy.getGameId();
        this.player1 = new BattleshipBoard(toCopy.getPlayer1());
        this.player2 = new BattleshipBoard(toCopy.getPlayer2());
        this.playerTurn = toCopy.getPlayerTurn();
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public BattleshipBoard getPlayer1() {
        return player1;
    }

    public void setPlayer1(BattleshipBoard player1) {
        this.player1 = player1;
    }

    public BattleshipBoard getPlayer2() {
        return player2;
    }

    public void setPlayer2(BattleshipBoard player2) {
        this.player2 = player2;
    }

    public Integer getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(Integer playerTurn) {
        this.playerTurn = playerTurn;
    }
}
