package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile( {"production", "daotesting"} )
public class PostgresBattleshipDao implements BattleshipDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public List<BattleshipGame> getAllGames() throws InvalidPlayerException, InvalidBoardException {
        //Get a list of all games
        List<BattleshipGame> returnedGames = template.query("select * from \"Games\" as games order by games.\"gameId\";", new GameMapper());
        //Retrieve each player's board for each game
        for (int i = 0; i < returnedGames.size(); i++) {
            BattleshipGame currentGame = returnedGames.get(i);
            currentGame.setPlayer1(getPlayerBoard(currentGame.getPlayer1().getBoardId()));
            currentGame.setPlayer2(getPlayerBoard(currentGame.getPlayer2().getBoardId()));
        }

        return returnedGames;
    }

    @Override
    public BattleshipGame addGame(Integer player1, Integer player2) throws InvalidPlayerException, InvalidIdException {
        //Check for null values in game to add
        if (player1 == null || player2 == null) {
            throw new InvalidPlayerException("Tried to add a game using null players.");
        }

        //Check that the players are not equal
        if (player1 == player2) {
            throw new InvalidPlayerException("Player 1 and Player 2 cannot use the same board.");
        }

        //Get all player ids
        List<Integer> allPlayers = getAllPlayerIds();
        //If the player doesn't already exist, throw an error.
        if (!allPlayers.contains(player1)){
            throw new InvalidPlayerException("A player with id " + player1 + " does not exist.");
        }
        if (!allPlayers.contains(player2)){
            throw new InvalidPlayerException("A player with id " + player2 + " does not exist.");
        }

        //Build the player boards
        List<Integer> boardIds = template.query("INSERT INTO \"PlayerBoards\" (\"playerId\") VALUES (" + player1 + "), (" + player2 + ") returning \"boardId\";",
                new BoardIdMapper());

        //Build game
        List<Integer> gameId = template.query("INSERT INTO \"Games\" (\"player1\", \"player2\", \"playerTurn\") " +
                "VALUES (" + boardIds.get(0) + ", " + boardIds.get(1) + ", 0) returning \"gameId\";", new GameIdMapper());

        //Get and return the newly added game
        return getGameById(gameId.get(0));
    }

    @Override
    public Integer createNewPlayer() {
        //Build a new player into the Players table
        List<Integer> insertedIds = template.query( "insert into \"Players\" (\"name\") VALUES ('default') returning \"playerId\";", new PlayerMapper());
        //Return the playerId
        return insertedIds.get(0);
    }

    @Override
    public BattleshipGame getGameById(Integer gameId) throws InvalidIdException, InvalidPlayerException {
        //Check for null
        if (gameId == null) {
            throw new InvalidIdException("Tried to search with a null Id");
        }
        try {
            //Get the requested game

            BattleshipGame retrievedGame = template.queryForObject("select * from \"Games\" as games " +
                    "where \"gameId\" = " + gameId + ";", new GameMapper());

            retrievedGame.setPlayer1(getPlayerBoard(retrievedGame.getPlayer1().getBoardId()));
            retrievedGame.setPlayer2(getPlayerBoard(retrievedGame.getPlayer2().getBoardId()));

            return retrievedGame;
        } catch(DataAccessException | InvalidBoardException ex) {
            throw new InvalidIdException("Error retrieving game id: " + gameId, ex);
        }
    }

    @Override
    public BattleshipBoard getPlayerBoard(Integer boardId) throws InvalidBoardException {
        if (boardId == null) {
            throw new InvalidBoardException("Tried to get a null board.");
        }

        //Build the specific board
        BattleshipBoard playerBoard = new BattleshipBoard();
        playerBoard.setBoardId(boardId);
        Integer playerId;
        List<Ship> ships;
        List<Point> hits;
        //Set the PlayerId
        try {
            playerId = template.queryForObject("select * from \"PlayerBoards\" as boards " +
                    "where \"boardId\" = " + boardId + ";", new PlayerMapper());
            //Throw an exception if a playerId does not exist for board
        } catch(DataAccessException ex) {
            throw new InvalidBoardException("Board does not exist with id " + boardId + ": ", ex);
        }
        //Retrieve and set the ships for the board
        try {
            ships = template.query("select * from \"Ships\" as ships " +
                    "where \"boardId\" = " + boardId + ";", new ShipMapper());
        //Build an empty list if Ships don't exist for board
        } catch(DataAccessException ex) {
            ships = new ArrayList<>();
        }
        //Retrieve and set the hits for the board
        try {
            hits = template.query("select * from \"Hits\" as hits " +
                    "where \"boardId\" = " + boardId + ";", new PointMapper());
        //Build an empty list if Hits don't exist for board
        } catch(DataAccessException ex) {
            hits = new ArrayList<>();
        }
        //Build and return the found board
        playerBoard.setPlayerId(playerId);
        playerBoard.setPlacedShips(ships);
        playerBoard.setBoardHits(hits);
        return playerBoard;
    }

    @Override
    public BattleshipBoard addShip(Integer boardId, Ship toPlace) throws NullInputException, InvalidShipException, InvalidBoardException, InvalidPlacementException, NullBoardException {
        //Add a ship for the given game and player
        if (boardId == null || toPlace == null || toPlace.getShipType() == null || toPlace.getHorizontal() == null ||
                toPlace.getStartingSquare() == null) {
            throw new NullInputException("Attempted to place a ship with null values.");
        }

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

        //Retrieve occupied squares
        List<Point> occupiedSquares = getOccupiedSquares(getPlayerBoard(boardId));

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
            try {
                template.update("INSERT INTO \"Ships\" (\"boardId\", \"shipType\", \"isHorizontal\", \"startingRow\", \"startingColumn\") " +
                        "VALUES (" + boardId + ", '" + toPlace.getShipType() + "', '" + toPlace.getHorizontal() + "', " + toPlace.getStartingSquare().x + ", " + toPlace.getStartingSquare().y + ");");
            }catch(DataAccessException ex) {
                throw new InvalidBoardException("Board with id " + boardId + "does not exist. " + ex);
            }
        }
        //Return the updated board;
        return getPlayerBoard(boardId);
    }

    @Override
    public BattleshipBoard addHit(Integer boardId, Point hitToAdd) throws NullInputException, InvalidBoardException, InvalidHitException {
        //Add a Hit to the game
        if(boardId == null || hitToAdd == null) {
            throw new NullInputException("Attempted to add a hit with null values.");
        }
        //Get a list of current hits
        List<Point> currentHits = getPlayerBoard(boardId).getBoardHits();

        if (currentHits.contains(hitToAdd)) {
            throw new InvalidHitException("A hit for this square already exists.");
        }
        //Add the hit to the table
        try{
            template.update("INSERT INTO \"Hits\" (\"boardId\", \"hitRow\", \"hitColumn\") " +
                    "VALUES (" + boardId + ", " + hitToAdd.x + ", " + hitToAdd.y + ");");
        } catch(DataAccessException ex) {
            throw new InvalidBoardException("Board with id " + boardId + "does not exist. " + ex);
        }
        return getPlayerBoard(boardId);
    }

    @Override
    public void updatePlayerTurn(Integer gameId) throws NullGameException, InvalidIdException, InvalidPlayerTurnException, InvalidPlayerException {
        //Get the current game to check the turn
        BattleshipGame retrievedGame = getGameById(gameId);

        Integer newTurn;
        switch(retrievedGame.getPlayerTurn()) {
            case 0:
            case 2:
                newTurn = 1;
                break;
            case 1:
                newTurn = 2;
                break;
            default:
                throw new InvalidPlayerTurnException("Player turn is set to an invalid value.");

        }
        //update the player turn
        try {
            template.update("UPDATE public.\"Games\" SET \"playerTurn\" = " + newTurn + " WHERE \"gameId\" = " +
                    gameId + ";");
        } catch(DataAccessException ex) {
            throw new InvalidIdException("Error retrieving game id: " + gameId, ex);
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


    @Override
    public void reset() {
        //Reset all tables
        template.update("TRUNCATE \"Games\", \"PlayerBoards\", \"Players\", \"Hits\", \"Ships\"");
        //Restart each id sequence
        template.update("ALTER SEQUENCE \"Games_gameId_seq\" RESTART WITH 1");
        template.update("ALTER SEQUENCE \"PlayerBoard_boardId_seq\" RESTART WITH 1");
        template.update("ALTER SEQUENCE \"Players_playerId_seq\" RESTART WITH 1");

        //Add default players
        template.update( "INSERT INTO \"Players\" (\"name\") VALUES ('player1'), ('player2');");

        //Build the player boards
        template.update("INSERT INTO \"PlayerBoards\" (\"playerId\") VALUES (1);");
        template.update("INSERT INTO \"PlayerBoards\" (\"playerId\") VALUES (2);");

        //Build player1 ships; player2 will not have default ships
        template.update("INSERT INTO \"Ships\" (\"boardId\", \"shipType\", \"isHorizontal\", \"startingRow\", \"startingColumn\") " +
                "VALUES (1, 'Carrier', 'true', 0, 0);");

        //Build player1 hits; player2 wil not have default hits
        template.update("INSERT INTO \"Hits\" (\"boardId\", \"hitRow\", \"hitColumn\") " +
                "VALUES (1, 0, 0);");

        //Build default game
        template.update("INSERT INTO \"Games\" (\"player1\", \"player2\", \"playerTurn\") " +
                "VALUES (1, 2, 0);");
    }

    private List<Integer> getAllPlayerIds() {
        return template.query("select * from \"Players\" order by \"playerId\";", new PlayerMapper());
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

    class GameMapper implements RowMapper<BattleshipGame> {

        @Override
        public BattleshipGame mapRow(ResultSet resultSet, int i) throws SQLException {
            //Build a Battleship Game to return
            BattleshipGame toReturn = new BattleshipGame();
            //Set the gameId
            toReturn.setGameId(resultSet.getInt("gameId"));
            //Build a new board for player1
            BattleshipBoard player1 = new BattleshipBoard();
            //We only have enough information to set the board id for now
            player1.setBoardId(resultSet.getInt(("player1")));
            toReturn.setPlayer1(player1);
            // Build a new board for player 2
            BattleshipBoard player2 = new BattleshipBoard();
            //We only have enough information to set the board id for now
            player2.setBoardId(resultSet.getInt(("player2")));
            toReturn.setPlayer2(player2);
            //Set the player turn
            toReturn.setPlayerTurn(resultSet.getInt("playerTurn"));
            return toReturn;
        }
    }

    class BoardMapper implements RowMapper<BattleshipBoard> {

        @Override
        public BattleshipBoard mapRow(ResultSet resultSet, int i) throws SQLException {
            BattleshipBoard toReturn = new BattleshipBoard();
            toReturn.setPlayerId(resultSet.getInt("playerId"));
            return toReturn;
        }
    }

    class ShipMapper implements RowMapper<Ship> {

        @Override
        public Ship mapRow(ResultSet resultSet, int i) throws SQLException {
            Ship toReturn = new Ship();
            toReturn.setShipType(resultSet.getString("shipType"));
            toReturn.setHorizontal(resultSet.getBoolean("isHorizontal"));
            toReturn.setStartingSquare(new Point(resultSet.getInt("startingRow"), resultSet.getInt(("startingColumn"))));
            return toReturn;
        }
    }

    class PointMapper implements RowMapper<Point> {

        @Override
        public Point mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Point(resultSet.getInt("hitRow"), resultSet.getInt("hitColumn"));
        }
    }

    class PlayerMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return (resultSet.getInt("playerId"));
        }
    }

    class GameIdMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return (resultSet.getInt("gameId"));
        }
    }

    class BoardIdMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return (resultSet.getInt("boardId"));
        }
    }
}
