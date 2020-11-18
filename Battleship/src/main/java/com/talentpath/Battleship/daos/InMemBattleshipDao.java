package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile( "servicetesting" )
public class InMemBattleshipDao implements BattleshipDao {

    List<BattleshipGame> allGames = new ArrayList<>();
    List<Integer> allPlayers = new ArrayList<>();
    List<String> validShipTypes = new ArrayList<>();

    public InMemBattleshipDao() {
        //Reset allGames on initialization.
        reset();
    }

    @Override
    public List<BattleshipGame> getAllGames() {
        //Return a copy of all games
        return allGames.stream().map( toCopy -> new BattleshipGame(toCopy) ).collect(Collectors.toList());
    }

    @Override
    public BattleshipGame addGame(Integer player1, Integer player2) throws InvalidPlayerException {
        //Check for null values in game to add
        if (player1 == null || player2 == null) {
            throw new InvalidPlayerException("Tried to add a game using null players.");
        }

        //Check that the players are not equal
        if (player1 == player2) {
            throw new InvalidPlayerException("Player 1 and Player 2 cannot use the same board.");
        }

        //If the player doesn't already exist, throw an error.
        if (!allPlayers.contains(player1)){
            throw new InvalidPlayerException("A player with id " + player1 + " does not exist.");
        }
        if (!allPlayers.contains(player2)){
            throw new InvalidPlayerException("A player with id " + player2 + " does not exist.");
        }

        BattleshipGame newGame = new BattleshipGame();
        //Get the next valid game index
        newGame.setGameId(allGames.stream().mapToInt( g -> g.getGameId()).max().orElse(0) + 1);

        //Create player1's board
        BattleshipBoard player1Board = new BattleshipBoard();
        //Get the next valid boardId for the player
        player1Board.setBoardId(allGames.stream().mapToInt( g -> Integer.max(g.getPlayer1().getBoardId(), g.getPlayer2().getBoardId())).max().orElse(0) + 1);
        player1Board.setPlayerId(player1);
        player1Board.setPlacedShips(new ArrayList<>());
        player1Board.setBoardHits(new ArrayList<>());
        newGame.setPlayer1(player1Board);

        //Create player2's board
        BattleshipBoard player2Board = new BattleshipBoard();
        //Get the next valid boardId for the player
        player2Board.setBoardId(allGames.stream().mapToInt( g -> Integer.max(g.getPlayer1().getBoardId(), g.getPlayer2().getBoardId())).max().orElse(0) + 1);
        player2Board.setPlayerId(player2);
        player2Board.setPlacedShips(new ArrayList<>());
        player2Board.setBoardHits(new ArrayList<>());
        newGame.setPlayer2(player2Board);

        allGames.add(newGame);

        //Return a copy of the game
        return new BattleshipGame(newGame);
    }

    @Override
    public Integer createNewPlayer() {
        Integer newPlayerId = allPlayers.stream().mapToInt( g -> g).max().orElse(0) + 1;
        allPlayers.add(newPlayerId);
        return newPlayerId;
    }

    @Override
    public BattleshipGame getGameById(Integer gameId) throws InvalidIdException {
        if (gameId == null) {
            throw new InvalidIdException("Tried to retrieve a game using an invalid id.");
        }
        //Search for a game with the passed Id
        BattleshipGame toCopy = allGames.stream().filter( g -> g.getGameId().equals(gameId)).findAny().orElse(null);
        //Return a copy of the game
        if (toCopy == null) {
            throw new InvalidIdException("A game with id " + gameId + " does not exist.");
        }
        return new BattleshipGame(toCopy);
    }

    @Override
    public BattleshipBoard getPlayerBoard(Integer boardId) throws InvalidPlayerException, InvalidIdException, InvalidBoardException {
        //boardId cannot be null
        if (boardId == null) {
            throw new InvalidBoardException("Attempted to get a board using a null boardId.");
        }
        //Search for the player board
        for (BattleshipGame currentGame : allGames) {
            //Check player1
            if (currentGame.getPlayer1().getBoardId() == boardId) {
                return new BattleshipBoard(currentGame.getPlayer1());
            }
            //Check player2
            if (currentGame.getPlayer2().getBoardId() == boardId) {
                return new BattleshipBoard(currentGame.getPlayer2());
            }
        }
        throw new InvalidBoardException("Attempted to get a board that doesn't exist.");
    }

    @Override
    public BattleshipBoard addShip(Integer boardId, Ship toPlace) throws InvalidShipException, NullInputException, NullBoardException, InvalidPlacementException, InvalidBoardException {
        //Add a ship for the given game and player
        if (boardId == null || toPlace == null || toPlace.getShipType() == null || toPlace.getHorizontal() == null ||
        toPlace.getStartingSquare() == null) {
            throw new NullInputException("Attempted to place a ship with null values.");
        }

        //Get the playerBoard to add a ship
        BattleshipBoard playerBoard = getActualPlayerBoard(boardId);

        //Calculate the size of the ship
        int shipSize;
        switch (toPlace.getShipType()) {
            case "Carrier":
                shipSize = 5;
                break;
            case "Battleship":
                shipSize = 4;
                break;
            case "Cruiser":
            case "Submarine":
                shipSize = 3;
                break;
            case "Destroyer":
                shipSize = 2;
                break;
            default:
                throw new InvalidShipException(toPlace.getShipType() + " is not a valid ship.");
        }

        //Get player's placed ships
        List<Ship> currentShips = playerBoard.getPlacedShips();
        //Check that the ship toAdd is not already placed
        for (Ship currentShip : currentShips) {
            if (currentShip.getShipType().equals(toPlace.getShipType())) {
                throw new InvalidPlacementException("Ship already added for this player.");
            }
        }

        //Retrieve occupied squares
        List<Point> occupiedSquares = getOccupiedSquares(playerBoard);

        //Place the ship based on orientation
        if (toPlace.getHorizontal()) {
            //Ships starting x position
            int shipStartingX = toPlace.getStartingSquare().x;
            //Ships cannot exceed board length
            if (shipStartingX + shipSize > 9) {
                throw new InvalidPlacementException("Tried to place ship on an invalid square.");
            }
            //Ships cannot be placed on squares that already have a ship
            for (int i = 0; i < shipSize; i++) {
                if (occupiedSquares.contains(new Point (shipStartingX + i, toPlace.getStartingSquare().y))) {
                    throw new InvalidPlacementException("Tried to place ship on a space that already contains a ship.");
                }
            }
        } else {
            //Ships starting y position
            int shipStartingY = toPlace.getStartingSquare().y;
            //Ships cannot exceed board height
            if (shipStartingY + shipSize > 9) {
                throw new InvalidPlacementException("Tried to place ship on an invalid square.");
            }
            //Ships cannot be placed on squares that already have a ship
            for (int i = 0; i < shipSize; i++) {
                if (occupiedSquares.contains(new Point(toPlace.getStartingSquare().x,shipStartingY + i))) {
                    throw new InvalidPlacementException("Tried to place ship on a space that already contains a ship.");
                }
            }
            //Add the ship
            currentShips.add(toPlace);
        }
        //Return a copy of the updated board.
        return new BattleshipBoard(playerBoard);
    }

    @Override
    public BattleshipBoard addHit(Integer boardId, Point hitToAdd) throws NullInputException, InvalidHitException, InvalidBoardException {
        //Add a Hit to the game
        if(boardId == null || hitToAdd == null) {
            throw new NullInputException("Attempted to add a hit with null values.");
        }
        BattleshipBoard retrievedBoard = getActualPlayerBoard(boardId);

        //Get a list of current hits
        List<Point> currentHits = retrievedBoard.getBoardHits();

        if (currentHits.contains(hitToAdd)) {
            throw new InvalidHitException("A hit for this square already exists.");
        }
        //Add the hit to the board
        currentHits.add(hitToAdd);

        //Return a copy of the updated board
        return new BattleshipBoard(retrievedBoard);
    }

    @Override
    public void updatePlayerTurn(Integer gameId) throws NullGameException, InvalidIdException, InvalidPlayerTurnException {
        if (gameId == null) {
            throw new NullGameException("Tried to access a game using a null game Id.");
        }

        BattleshipGame currentGame = getActualGameById(gameId);

        switch (currentGame.getPlayerTurn()) {
            case 0:
            case 2:
                currentGame.setPlayerTurn(1);
                break;
            case 1:
                currentGame.setPlayerTurn(2);
                break;
            default:
                throw new InvalidPlayerTurnException("Player turn is set to an invalid value.");
        }
    }

    @Override
    public List<Point> getOccupiedSquares(BattleshipBoard playerBoard) throws InvalidShipException, NullBoardException {
        //Calculate squares occupied by ships
        if (playerBoard == null) {
            throw new NullBoardException("Tried to access a null player board.");
        }
        List<Point> occupiedSquares = new ArrayList<>();
        List<Ship> placedShips = playerBoard.getPlacedShips();

        for(Ship ships : placedShips) {
            int xPos = ships.getStartingSquare().x;
            int yPos = ships.getStartingSquare().y;
            boolean shipHorizontal = ships.getHorizontal();
            switch(ships.getShipType()) {
                case "Carrier":
                    calculateOccupied(occupiedSquares, 5, xPos, yPos, shipHorizontal);
                    break;
                case "Battleship":
                    calculateOccupied(occupiedSquares, 4, xPos, yPos, shipHorizontal);
                    break;
                case "Cruiser":
                case "Submarine":
                    calculateOccupied(occupiedSquares, 3, xPos, yPos, shipHorizontal);
                    break;
                case "Destroyer":
                    calculateOccupied(occupiedSquares, 2, xPos, yPos, shipHorizontal);
                    break;
                default:
                    throw new InvalidShipException("Tried to calculate size of an invalid ship.");
            }
        }
        return occupiedSquares;
    }

    // Clears all current games.
    @Override
    public void reset() {
        //Clear all games
        allGames.clear();
        allPlayers.clear();
        validShipTypes.clear();

        //Build validShipTypes
        validShipTypes.add("Carrier");
        validShipTypes.add("Battleship");
        validShipTypes.add("Cruiser");
        validShipTypes.add("Submarine");
        validShipTypes.add("Destroyer");

        //Build a default game into allGames
        BattleshipGame toAdd = new BattleshipGame();

        Ship exampleShip = new Ship();
        exampleShip.setShipType("Carrier");
        exampleShip.setHorizontal(true);
        exampleShip.setStartingSquare(new Point(0, 0));
        List<Ship> shipToAdd = new ArrayList<>();
        shipToAdd.add(exampleShip);

        List<Point> hitToAdd = new ArrayList<>();
        Point newHit = new Point(0, 0);
        hitToAdd.add(newHit);

        BattleshipBoard player1 = new BattleshipBoard();
        allPlayers.add(1);
        player1.setBoardId(1);
        player1.setPlayerId(1);
        player1.setPlacedShips(shipToAdd);
        player1.setBoardHits(hitToAdd);

        BattleshipBoard player2 = new BattleshipBoard();
        allPlayers.add(2);
        player2.setBoardId(2);
        player2.setPlayerId(2);
        player2.setPlacedShips(new ArrayList<>());
        player2.setBoardHits(new ArrayList<>());

        toAdd.setGameId(1);
        toAdd.setPlayer1(player1);
        toAdd.setPlayer2(player2);
        toAdd.setPlayerTurn(0);

        allGames.add(toAdd);
    }

    private BattleshipGame getActualGameById(Integer gameId) throws InvalidIdException {
        if (gameId == null) {
            throw new InvalidIdException("Tried to retrieve a game using an invalid id.");
        }
        //Search for a game with the passed Id
        BattleshipGame retrieved = allGames.stream().filter( g -> g.getGameId().equals(gameId)).findAny().orElse(null);
        //Return a copy of the game
        if (retrieved == null) {
            throw new InvalidIdException("A game with id " + gameId + " does not exist.");
        }
        return retrieved;
    }

    private BattleshipBoard getActualPlayerBoard(Integer boardId) throws InvalidBoardException {
        //boardId cannot be null
        if (boardId == null) {
            throw new InvalidBoardException("Attempted to get a board using a null boardId.");
        }
        //Search for the player board
        for (BattleshipGame currentGame : allGames) {
            //Check player1
            if (currentGame.getPlayer1().getBoardId() == boardId) {
                return currentGame.getPlayer1();
            }
            //Check player2
            if (currentGame.getPlayer2().getBoardId() == boardId) {
                return currentGame.getPlayer2();
            }
        }
        throw new InvalidBoardException("Attempted to get a board that doesn't exist.");
    }

    private void calculateOccupied(List<Point> occupiedSquares, int length, int xPos, int yPos, boolean isHorizontal ){
        //Calculate squares occupied of passed length from passed xPos and yPos
        if (isHorizontal) {
            for (int i = 0; i < length; i++) {
                occupiedSquares.add(new Point(xPos + i, yPos));
            }
        } else {
            for (int i = 0; i < length; i++) {
                occupiedSquares.add(new Point(xPos, yPos + i));
            }
        }
    }
}
