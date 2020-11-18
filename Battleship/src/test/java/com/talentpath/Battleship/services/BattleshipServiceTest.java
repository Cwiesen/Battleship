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
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
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

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void placeShipNullToPlace() {
        try {

            BattleshipBoard updatedBoard = serviceToTest.placeShip(null);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null toPlace test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullGameId() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(null);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null Game Id test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullPlayerId() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(null);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null Player Id test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullShipType() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType(null);
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null Ship Type test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullHorizontal() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(null);
            toPlace.setxPos(3);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null horizontal test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullxPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(null);
            toPlace.setyPos(4);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null xPos test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipNullyPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(3);
            toPlace.setyPos(null);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during null yPos test: " + ex.getMessage());
        } catch (NullInputException ex) {

        }
    }

    @Test
    void placeShipDuplicateShipType() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Carrier");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(2);
            toPlace.setyPos(5);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during duplicate ship type test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipDuplicateShipPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(0);
            toPlace.setyPos(0);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during duplicate ship type test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipInvalidXPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(true);
            toPlace.setxPos(8);
            toPlace.setyPos(0);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");


        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during invalid x pos test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeShipInvalidYPos() {
        try {
            PlaceShip toPlace = new PlaceShip();
            toPlace.setBoardId(1);
            toPlace.setPlayerNumber(1);
            toPlace.setShipType("Battleship");
            toPlace.setisHorizontal(false);
            toPlace.setxPos(0);
            toPlace.setyPos(8);

            BattleshipBoard updatedBoard = serviceToTest.placeShip(toPlace);
            fail("Expected error to be thrown.");

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during invalid y pos test: " + ex.getMessage());
        } catch (InvalidPlacementException ex) {

        }
    }

    @Test
    void placeHit(){
        try{
            //Miss test
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(1);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            assertEquals("Miss!", hitStatus1);

            //Hit test
            PlaceHit toPlace2 = new PlaceHit();
            toPlace2.setBoardId(1);
            toPlace2.setPlayerNumber(1);
            toPlace2.setxPos(1);
            toPlace2.setyPos(0);

            String hitStatus2 = serviceToTest.placeHit(toPlace2);
            assertEquals("Hit!", hitStatus2);

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void placeHitNullPlace(){
        try{
            String hitStatus1 = serviceToTest.placeHit(null);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during NullPlace test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullGameId(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(null);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during NullGameId test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullPlayerNumber(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(1);
            toPlace1.setPlayerNumber(null);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during NullPlayerNumber test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullX(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(1);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(null);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during NullX test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitNullY(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(1);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(null);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during NullY test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void placeHitInvalidGameId(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during InvalidGameId test: " + ex.getMessage());
        } catch(InvalidIdException ex) {

        }
    }

    @Test
    void placeHitInvalidPlayerNumber(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception during InvalidPlayer test: " + ex.getMessage());
        } catch(InvalidIdException ex) {

        }
    }

    @Test
    void placeHitExistingPoint(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(1);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(0);
            toPlace1.setyPos(0);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected InvalidHit exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during ExistingPoint test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooHighXPos(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(10);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during TooHighX test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooHighYPos(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(10);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during TooHighY test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooLowXPos(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(-1);
            toPlace1.setyPos(3);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during TooLowX test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }

    @Test
    void placeHitTooLowYPos(){
        try{
            PlaceHit toPlace1 = new PlaceHit();
            toPlace1.setBoardId(2);
            toPlace1.setPlayerNumber(1);
            toPlace1.setxPos(2);
            toPlace1.setyPos(-1);

            String hitStatus1 = serviceToTest.placeHit(toPlace1);
            fail("Expected NullInput exception");

        } catch(NullInputException | InvalidIdException | InvalidShipException | NullBoardException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during TooLowY test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }
}