package com.talentpath.Battleship.services;

import com.talentpath.Battleship.daos.BattleshipDao;
import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("servicetesting")
class BattleshipServiceTest {

    BattleshipService serviceToTest;

    @Autowired
    BattleshipDao dao;

    @BeforeEach
    void setUp() {
        serviceToTest = new BattleshipService(dao);
        dao.reset();
    }

    @Test
    void getAllGames() {
        try {
            List<BattleshipGame> allGames = serviceToTest.getAllGames();
            assertEquals(1, allGames.size());

        } catch (NullGameException | InvalidIdException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected error during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void beginGame() {
        Integer player1 = 1;
        Integer player2 = 2;

        try {
            BattleshipGame addedGame = serviceToTest.beginGame(player1, player2);
            assertEquals(2, addedGame.getGameId());
            assertEquals(1, addedGame.getPlayer1().getPlayerId());
            assertEquals(2, addedGame.getPlayer2().getPlayerId());
            assertEquals(0, addedGame.getPlayerTurn());

            //Check that the game was actually added
            List<BattleshipGame> allGames = serviceToTest.getAllGames();
            assertEquals(2, allGames.size());

            BattleshipGame retrievedGame = dao.getGameById(2);
            assertEquals(2, retrievedGame.getGameId());
            assertEquals(1, retrievedGame.getPlayer1().getPlayerId());
            assertEquals(2, retrievedGame.getPlayer2().getPlayerId());
            assertEquals(0, retrievedGame.getPlayer1().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer2().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer1().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayer2().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayerTurn());

        } catch (InvalidPlayerException | NullGameException | InvalidIdException | InvalidBoardException ex) {
            fail("Unexpected error during golden path: " + ex.getMessage());
        }
    }

    @Test
    void beginGameNewPlayers() {
        Integer player1 = 0;
        Integer player2 = 0;

        try {
            BattleshipGame addedGame = serviceToTest.beginGame(player1, player2);
            assertEquals(2, addedGame.getGameId());
            assertEquals(3, addedGame.getPlayer1().getPlayerId());
            assertEquals(4, addedGame.getPlayer2().getPlayerId());
            assertEquals(0, addedGame.getPlayerTurn());

            //Check that the game was actually added
            List<BattleshipGame> allGames = serviceToTest.getAllGames();
            assertEquals(2, allGames.size());

            BattleshipGame retrievedGame = dao.getGameById(2);
            assertEquals(2, retrievedGame.getGameId());
            assertEquals(3, retrievedGame.getPlayer1().getPlayerId());
            assertEquals(4, retrievedGame.getPlayer2().getPlayerId());
            assertEquals(0, retrievedGame.getPlayer1().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer2().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer1().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayer2().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayerTurn());

        } catch (InvalidPlayerException | NullGameException | InvalidIdException | InvalidBoardException ex) {
            fail("Unexpected error during new player test: " + ex.getMessage());
        }
    }

    @Test
    void beginGameNullPlayer() {
        Integer player1 = 1;
        Integer player2 = null;

        try {
            BattleshipGame addedGame = serviceToTest.beginGame(player1, player2);
            fail("Expected a InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        } catch (InvalidIdException ex) {
            fail("Unexpected error during NullPlayer test: " + ex.getMessage());
        }
    }

    @Test
    void beginGameDuplicatePlayer() {
        Integer player1 = 1;
        Integer player2 = 1;

        try {
            BattleshipGame addedGame = serviceToTest.beginGame(player1, player2);
            fail("Expected a InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        } catch (InvalidIdException ex) {
            fail("Unexpected error during NullPlayer test: " + ex.getMessage());
        }
    }

    @Test
    void beginGameInvalidPlayer() {
        Integer player1 = 3;
        Integer player2 = 2;

        try {
            BattleshipGame addedGame = serviceToTest.beginGame(player1, player2);
            fail("Expected a InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        } catch (InvalidIdException ex) {
            fail("Unexpected error during NullPlayer test: " + ex.getMessage());
        }
    }

    @Test
    void getGameById() {
        try {
            BattleshipGame retrievedGame = serviceToTest.getGameById(1);

            assertEquals(1, retrievedGame.getGameId());
            assertEquals(1, retrievedGame.getPlayer1().getPlayerId());
            assertEquals(2, retrievedGame.getPlayer2().getPlayerId());
            assertEquals(1, retrievedGame.getPlayer1().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer2().getPlacedShips().size());
            assertEquals(1, retrievedGame.getPlayer1().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayer2().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayerTurn());

        } catch (InvalidIdException | NullGameException | InvalidPlayerException ex) {
            fail("Hit unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void getGameByNullId() {
        try {
            BattleshipGame retrievedGame = serviceToTest.getGameById(null);
            fail("Expected InvalidIdException");

        } catch (NullGameException | InvalidPlayerException ex) {
            fail("Hit unexpected exception during getGameByNullId: " + ex.getMessage());
        } catch (InvalidIdException ex) {

        }
    }

    @Test
    void getGameByInvalidId() {
        try {
            BattleshipGame retrievedGame = serviceToTest.getGameById(2);
            fail("Expected InvalidIdException");

        } catch (NullGameException | InvalidPlayerException ex) {
            fail("Hit unexpected exception during getGameByInvalidId: " + ex.getMessage());
        } catch (InvalidIdException ex) {

        }
    }

    @Test
    void placeShip() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            assertEquals(2, updatedBoard.getPlacedShips().size());
            assertEquals("Battleship", updatedBoard.getPlacedShips().get(1).getShipType());
            assertFalse(updatedBoard.getPlacedShips().get(1).getHorizontal());
            assertEquals(3, updatedBoard.getPlacedShips().get(1).getStartingSquare().x);
            assertEquals(4, updatedBoard.getPlacedShips().get(1).getStartingSquare().y);

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | NullInputException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void placeShipNullToPlace() {
        try {
            BattleshipBoard updatedBoard = serviceToTest.placeShip(null);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null toPlace test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullGameId() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(null);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null Game Id test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullBoardId() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(null);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null Game Id test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullShipType() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType(null);
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null Ship Type test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullHorizontal() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(null);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null horizontal test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullxPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(null);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null xPos test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullyPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(null);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during null yPos test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipDuplicateShipType() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Carrier");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(2);
            toPlace.setyPos(5);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during duplicate ship type test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipDuplicateShipPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(0);
            toPlace.setyPos(0);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during duplicate ship type test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipInvalidXPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(true);
            toPlace.setxPos(8);
            toPlace.setyPos(0);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during invalid x pos test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipInvalidYPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setGameId(1);
            toPlace.setBoardId(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(0);
            toPlace.setyPos(8);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during invalid y pos test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeHit(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            //Miss test
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(9);
            toPlace1.setyPos(9);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            assertEquals("Miss!", hitStatus1);

            //Hit test
            PlaceHit toPlace2 = new PlaceHit();
            toPlace2.setGameId(1);
            toPlace2.setBoardId(1);
            toPlace2.setxPos(1);
            toPlace2.setyPos(0);

            String hitStatus2 = serviceToTest.placeHit(toPlace2);
            assertEquals("Hit!", hitStatus2);

            //Still Player 2 turn test
            PlaceHit toPlace3 = new PlaceHit();
            toPlace3.setGameId(1);
            toPlace3.setBoardId(1);
            toPlace3.setxPos(2);
            toPlace3.setyPos(0);

            String hitStatus3 = serviceToTest.placeHit(toPlace3);
            assertEquals("Hit!", hitStatus3);

            //Game Over test
            dao.reset();
            buildGameReadyBoards();

            String response = "";
            BattleshipBoard p1Board = serviceToTest.getPlayerBoard(1);
            List<Point> occupied = dao.getOccupiedSquares(p1Board);
            for (Point shipSquare : occupied) {
                PlaceHit toPlace4 = new PlaceHit();
                toPlace4.setGameId(1);
                toPlace4.setBoardId(2);
                toPlace4.setxPos(shipSquare.x);
                toPlace4.setyPos(shipSquare.y);

                response = serviceToTest.placeHit(toPlace4);
            }
            assertEquals("Game Over!", response);

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | NullInputException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void placeHitNullPlace(){
        try{
            String hitStatus1 = serviceToTest.placeHit(null);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during NullPlace test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullGameId(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(null);
            toPlace1.setBoardId(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during NullGameId test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullBoardId(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(null);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during NullGameId test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullX(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(1);
            toPlace1.setxPos(null);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during NullX test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullY(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(null);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during NullY test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitInvalidGameId(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(2);
            toPlace1.setBoardId(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during InvalidGameId test: " + ex.getMessage());
        } catch(InvalidIdException ex) {

        }
    }

    @Test
    void placeHitInvalidBoardId(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(3);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidIdException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during InvalidGameId test: " + ex.getMessage());
        } catch(InvalidBoardException ex) {

        }
    }

    @Test
    void placeHitExistingPoint(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(1);
            toPlace1.setyPos(1);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected InvalidHit exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during ExistingPoint test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooHighXPos(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(10);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during TooHighX test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooHighYPos(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(2);
            toPlace1.setyPos(10);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during TooHighY test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooLowXPos(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(-1);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during TooLowX test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooLowYPos(){
        try{
            //Build a game-ready board
            buildGameReadyBoards();

            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setGameId(1);
            toPlace1.setBoardId(2);
            toPlace1.setxPos(2);
            toPlace1.setyPos(-1);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException | InvalidPlayerTurnException | NullGameException ex) {
            fail("Unexpected exception during TooLowY test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    private void buildGameReadyBoards() {
        try {
            Ship carrierToAdd = new Ship();
            carrierToAdd.setShipType("Carrier");
            carrierToAdd.setHorizontal(true);
            carrierToAdd.setStartingSquare(new Point(0, 0));

            Ship battleshipToAdd = new Ship();
            battleshipToAdd.setShipType("Battleship");
            battleshipToAdd.setHorizontal(false);
            battleshipToAdd.setStartingSquare(new Point(3, 5));

            //Add ship Horizontal
            Ship cruiserToAdd = new Ship();
            cruiserToAdd.setShipType("Cruiser");
            cruiserToAdd.setHorizontal(true);
            cruiserToAdd.setStartingSquare(new Point(1, 1));

            Ship submarineToAdd = new Ship();
            submarineToAdd.setShipType("Submarine");
            submarineToAdd.setHorizontal(true);
            submarineToAdd.setStartingSquare(new Point(1, 2));

            Ship destroyerToAdd = new Ship();
            destroyerToAdd.setShipType("Destroyer");
            destroyerToAdd.setHorizontal(true);
            destroyerToAdd.setStartingSquare(new Point(1, 3));

            //Check that game starts when all ships placed
            //Player 1 finish
            dao.addShip(1, battleshipToAdd);
            dao.addShip(1, cruiserToAdd);
            dao.addShip(1, submarineToAdd);
            dao.addShip(1, destroyerToAdd);

            //Player 2
            dao.addShip(2, carrierToAdd);
            dao.addShip(2, battleshipToAdd);
            dao.addShip(2, cruiserToAdd);
            dao.addShip(2, submarineToAdd);

            //Add final one through service to test turn change
            PlaceShip destroyerToAddService = new PlaceShip();
            destroyerToAddService.setGameId(1);
            destroyerToAddService.setBoardId(2);
            destroyerToAddService.setShipType("Destroyer");
            destroyerToAddService.setisHorizontal(true);
            destroyerToAddService.setxPos(1);
            destroyerToAddService.setyPos(3);
            serviceToTest.placeShip(destroyerToAddService);

        } catch (InvalidBoardException | InvalidIdException | InvalidPlayerException | InvalidShipException | NullInputException | NullBoardException | InvalidPlacementException | NullGameException | InvalidPlayerTurnException ex) {
            System.out.println("Improper Game Ready Board setup.");
        }
    }
}