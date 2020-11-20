package com.talentpath.Battleship.services;

import com.talentpath.Battleship.daos.BattleshipDao;
import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BattleshipService {

    BattleshipDao dao;

    @Autowired
    public BattleshipService(BattleshipDao dao) {
        this.dao = dao;
    }

    public List<BattleshipGame> getAllGames() throws NullGameException, InvalidIdException, InvalidPlayerException, InvalidBoardException {
        List<BattleshipGame> allGames = dao.getAllGames();
        if (allGames == null) {
            throw new NullGameException("Retrieved null on All Games call");
        }

        return allGames;
    }

    public BattleshipGame beginGame(Integer player1, Integer player2) throws InvalidPlayerException, InvalidIdException {
        //If the players are null, throw an error
        if (player1 == null || player2 == null) {
            throw new InvalidPlayerException("Tried to start a game with null players");
        }
        //If a player is zero, build a new player
        if (player1 == 0) {
            player1 = dao.createNewPlayer();
        }
        if (player2 == 0) {
            player2 = dao.createNewPlayer();
        }
        //Ask the dao to build a new game
        return dao.addGame(player1, player2);
    }

    public BattleshipGame getGameById(Integer gameId) throws InvalidIdException, NullGameException, InvalidPlayerException {
        if (gameId == null) {
            throw new InvalidIdException("Tried to find a null game.");
        }
        BattleshipGame retrievedGame = dao.getGameById(gameId);

        if (retrievedGame == null) {
            throw new NullGameException("Game does not exist.");
        }

        return retrievedGame;
    }

    public BattleshipBoard getPlayerBoard(Integer boardId) throws InvalidPlayerException, InvalidIdException, NullBoardException, InvalidBoardException {
        if (boardId == null) {
            throw new NullBoardException("Tried to get a board using null values.");
        }
            return dao.getPlayerBoard(boardId);
    }

    public BattleshipBoard placeShip(PlaceShip toPlace) throws InvalidIdException, InvalidPlayerException, InvalidShipException, NullBoardException, InvalidPlacementException, NullInputException, InvalidBoardException, NullGameException, InvalidPlayerTurnException {

        //Check for null values
        if (toPlace == null || toPlace.getBoardId() == null || toPlace.getGameId() == null || toPlace.getShipType() == null ||
        toPlace.getisHorizontal() == null || toPlace.getxPos() == null || toPlace.getyPos() == null) {
            throw new NullInputException("Tried to add a ship with null values.");
        }
        //Get the BattleshipGame
        BattleshipGame retrievedGame = dao.getGameById(toPlace.getGameId());

        //Check that the boardId is part of this game
        if (toPlace.getBoardId() != retrievedGame.getPlayer1().getBoardId() && toPlace.getBoardId() != retrievedGame.getPlayer2().getBoardId()) {
            throw new InvalidBoardException("Provided board is not part of current game.");
        }

        //Ships cannot be placed after a game has begun.
        if (retrievedGame.getPlayerTurn() != 0) {
            throw new InvalidPlacementException("Ships cannot be placed after a game has begun.");
        }

        //Get the player board
        BattleshipBoard playerBoard = dao.getPlayerBoard(toPlace.getBoardId());

        //Build the ship object
        Ship newShip = new Ship();
        newShip.setShipType(toPlace.getShipType());
        newShip.setHorizontal(toPlace.getisHorizontal());
        newShip.setStartingSquare(new Point(toPlace.getxPos(), toPlace.getyPos()));

        //Place the ship
        BattleshipBoard updatedBoard = dao.addShip(playerBoard.getBoardId(), newShip);

        //Check if the other player's board is finished
        BattleshipBoard otherPlayerBoard;
        if (playerBoard == retrievedGame.getPlayer1()) {
            otherPlayerBoard = retrievedGame.getPlayer2();
        } else {
            otherPlayerBoard = retrievedGame.getPlayer1();
        }
        //Update the game's player turn to 1 once all have been placed
        if (updatedBoard.getPlacedShips().size() == 5 && otherPlayerBoard.getPlacedShips().size() == 5) {
            dao.updatePlayerTurn(retrievedGame.getGameId());
        }
        //Return updated board
        return dao.getPlayerBoard(playerBoard.getBoardId());
    }

    public String placeHit(PlaceHit hitToPlace) throws InvalidIdException, InvalidPlayerException, InvalidShipException, NullBoardException, InvalidHitException, NullInputException, InvalidBoardException, InvalidPlayerTurnException, NullGameException {
        //Check for null values
        if(hitToPlace == null || hitToPlace.getBoardId() == null || hitToPlace.getGameId() == null || hitToPlace.getBoardId() == null ||
            hitToPlace.getxPos() == null || hitToPlace.getyPos() == null) {
            throw new NullInputException("Attempted to add a hit with null values.");
        }

        //Get the BattleshipGame
        BattleshipGame retrievedGame = dao.getGameById(hitToPlace.getGameId());

        if (hitToPlace.getBoardId() != retrievedGame.getPlayer1().getBoardId() && hitToPlace.getBoardId() != retrievedGame.getPlayer2().getBoardId()) {
            throw new InvalidBoardException("Attempted to add a hit to a board not part of this game.");
        }
        //Throw an error if it is not the correct players turn
        if (retrievedGame.getPlayerTurn() == 0) {
            throw new InvalidPlayerTurnException("A shot cannot be made until all ships are placed.");
        }

        if (retrievedGame.getPlayerTurn() == 1 && retrievedGame.getPlayer2().getBoardId() != hitToPlace.getBoardId() ) {
            throw new InvalidPlayerTurnException("It is currently Player 1's turn.");
        }
        if (retrievedGame.getPlayerTurn() == 2 && retrievedGame.getPlayer1().getBoardId() != hitToPlace.getBoardId()) {
            throw new InvalidPlayerTurnException("It is currently Player 2's turn.");
        }

        //Check that the hit is on the board
        if (hitToPlace.getxPos() < 0 || hitToPlace.getyPos() < 0 || hitToPlace.getxPos() > 9 || hitToPlace.getyPos() > 9) {
            throw new InvalidHitException("Hit coordinates must be between 0 and 9");
        }

        //Build the hit
        Point newHit = new Point(hitToPlace.getxPos(), hitToPlace.getyPos());

        //Add a hit to the player's board
        BattleshipBoard playerBoard = dao.addHit(hitToPlace.getBoardId(), newHit);

        //Get a list of occupied squares
        List<Point> occupiedSquares = dao.getOccupiedSquares(playerBoard);

        //Check if a ship was hit
        if (occupiedSquares.contains(newHit)) {
            //Check if all ships have been hit
            if (checkGameOver(playerBoard)) {
                return "Game Over!";
            }
            //Return 'Hit' if square is occupied and keep player turn the same.
            return "Hit!";
        }
        //Update the player turn and return 'Miss' if square is not occupied
        dao.updatePlayerTurn(retrievedGame.getGameId());
        return "Miss!";
    }

    private boolean checkGameOver(BattleshipBoard playerBoard) throws InvalidShipException, NullBoardException {
        //Check if all ship squares have been hit for a board
        List<Point> occupiedSquares = dao.getOccupiedSquares(playerBoard);
        List<Point> hitSquares = playerBoard.getBoardHits();

        for (Point square: occupiedSquares) {
            if (!hitSquares.contains(square)) {
                return false;
            }
        }
        return true;
    }
}
