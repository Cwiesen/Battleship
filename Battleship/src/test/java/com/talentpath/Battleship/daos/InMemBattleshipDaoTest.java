package com.talentpath.Battleship.daos;

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
class InMemBattleshipDaoTest {

    @Autowired
    InMemBattleshipDao daoToTest;

    @BeforeEach
    void setUp() {
        //Reset database
        daoToTest.reset();

    }

    @Test
    void getAllGames() {
        List<BattleshipGame> allGames = daoToTest.getAllGames();
        assertEquals(1, allGames.size());
    }

    @Test
    void addGame() {
        Integer player1 = 1;
        Integer player2 = 2;

        try {
            //Add the game
            daoToTest.addGame(player1, player2);

            //Test that a game was added
            List<BattleshipGame> allGames = daoToTest.getAllGames();
            assertEquals(2, allGames.size());
            //Test that the game added has the same values as was passed
            BattleshipGame retrievedGame = daoToTest.getGameById(2);
            assertEquals(2, retrievedGame.getGameId());
            assertEquals(1, retrievedGame.getPlayer1().getPlayerId());
            assertEquals(2, retrievedGame.getPlayer2().getPlayerId());
            assertEquals(0, retrievedGame.getPlayer1().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer2().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer1().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayer2().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayerTurn());

        } catch (InvalidPlayerException | InvalidIdException ex) {
            fail("Unexpected exception in golden path test. " + ex.getMessage());
        }
    }

    @Test
    void addGameNullP1(){
        Integer player1 = null;
        Integer player2 = 2;

        try {
            //Add the game
            daoToTest.addGame(player1, player2);
            fail("Expected InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        }
    }

    @Test
    void addGameNullP2(){
        Integer player1 = 1;
        Integer player2 = null;

        try {
            //Add the game
            daoToTest.addGame(player1, player2);
            fail("Expected InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        }
    }

    @Test
    void addGameIdenticalPlayers(){
        Integer player1 = 1;
        Integer player2 = 1;

        try {
            //Add the game
            daoToTest.addGame(player1, player2);
            fail("Expected InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        }
    }

    @Test
    void addGamePlayerDNE(){
        Integer player1 = 1;
        Integer player2 = 3;

        try {
            //Add the game
            daoToTest.addGame(player1, player2);
            fail("Expected InvalidPlayerException");

        } catch (InvalidPlayerException ex) {
        }
    }

    @Test
    void createNewPlayer() {
        //Test that next highest playerId is generated.
        Integer playerId = daoToTest.createNewPlayer();
        assertEquals(3, playerId);
    }

    @Test
    void getGameById() {

        try {
            BattleshipGame retrievedGame = daoToTest.getGameById(1);
            assertEquals(1, retrievedGame.getGameId());
            assertEquals(1, retrievedGame.getPlayer1().getPlayerId());
            assertEquals(2, retrievedGame.getPlayer2().getPlayerId());
            assertEquals(1, retrievedGame.getPlayer1().getPlacedShips().size());
            assertEquals(0, retrievedGame.getPlayer2().getPlacedShips().size());
            assertEquals(1, retrievedGame.getPlayer1().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayer2().getBoardHits().size());
            assertEquals(0, retrievedGame.getPlayerTurn());

        } catch (InvalidIdException ex) {
            fail("Unexpected exception in golden path test." + ex.getMessage());
        }
    }

    @Test
    void getGameByNullId() {
        try {
            BattleshipGame retrievedGame = daoToTest.getGameById(null);
            fail("Expected error on getGameByNullId");

        } catch (InvalidIdException ex) {
        }
    }

    @Test
    void getGameByInvalidId() {
        try {
            BattleshipGame retrievedGame = daoToTest.getGameById(0);
            fail("Expected error on getGameByInvalidId");

        } catch (InvalidIdException ex) {
        }
    }

    @Test
    void getPlayerBoard() {
        try {
            BattleshipBoard retrievedP1Board = daoToTest.getPlayerBoard(1);
            assertEquals(1, retrievedP1Board.getPlayerId());
            assertEquals(1, retrievedP1Board.getPlacedShips().size());
            assertEquals(1, retrievedP1Board.getBoardHits().size());

            BattleshipBoard retrievedP2Board = daoToTest.getPlayerBoard(2);
            assertEquals(2, retrievedP2Board.getPlayerId());
            assertEquals(0, retrievedP2Board.getPlacedShips().size());
            assertEquals(0, retrievedP2Board.getBoardHits().size());

        } catch (InvalidIdException | InvalidPlayerException | InvalidBoardException ex) {
            fail("Unexpected exception during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void getPlayerBoardNullBoardId() {
        try {
            BattleshipBoard retrievedP1Board = daoToTest.getPlayerBoard(null);
            fail("Expected error for null playerId");

        } catch (InvalidBoardException  ex) {
        } catch (InvalidIdException |  InvalidPlayerException ex) {
            fail("Unexpected error for null boardId: " + ex.getMessage());
        }
    }

    @Test
    void getPlayerBoardInvalidBoardId() {
        try {
            BattleshipBoard retrievedP1Board = daoToTest.getPlayerBoard(3);
            fail("Expected error for invalid playerId");

        } catch (InvalidBoardException  ex) {
        } catch (InvalidIdException | InvalidPlayerException ex) {
            fail("Unexpected error for invalid playerId: " + ex.getMessage());
        }
    }

    @Test
    void updatePlayerTurn() {
        try {
            //Player Turn 0 to 1
            daoToTest.updatePlayerTurn(1);

            BattleshipGame retrievedGame = daoToTest.getGameById(1);
            assertEquals(1, retrievedGame.getPlayerTurn());

            //Player Turn 1 to 2
            daoToTest.updatePlayerTurn(1);

            retrievedGame = daoToTest.getGameById(1);
            assertEquals(2, retrievedGame.getPlayerTurn());

            //Player Turn 2 to 1
            daoToTest.updatePlayerTurn(1);

            retrievedGame = daoToTest.getGameById(1);
            assertEquals(1, retrievedGame.getPlayerTurn());
        } catch(InvalidIdException | NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected error during golden path test: " + ex.getMessage());
        }
    }

    @Test
    void updatePlayerTurnInvalidGame() {
        try {
            //Test on game that doesn't exist
            daoToTest.updatePlayerTurn(2);
            fail("Expected InvalidIdException");
            } catch(NullGameException | InvalidPlayerTurnException ex) {
            fail("Unexpected exception during InvalidGameId test." + ex.getMessage());
        } catch(InvalidIdException ex) {

        }
    }

    @Test
    void addShip() {

        try {
            Ship battleshipToAdd = new Ship();
            battleshipToAdd.setShipType("Battleship");
            battleshipToAdd.setHorizontal(false);
            battleshipToAdd.setStartingSquare(new Point(3, 5));

            //Add ship Vertical
            daoToTest.addShip(1, battleshipToAdd);

            BattleshipGame retrievedGame = daoToTest.getGameById(1);
            List<Ship> player1Ships = retrievedGame.getPlayer1().getPlacedShips();
            assertEquals(2, player1Ships.size());
            assertEquals("Battleship", player1Ships.get(1).getShipType());
            assertFalse(player1Ships.get(1).getHorizontal());
            assertEquals(3, player1Ships.get(1).getStartingSquare().x);
            assertEquals(5, player1Ships.get(1).getStartingSquare().y);

            //Add ship Horizontal
            Ship cruiserToAdd = new Ship();
            cruiserToAdd.setShipType("Cruiser");
            cruiserToAdd.setHorizontal(true);
            cruiserToAdd.setStartingSquare(new Point(1, 1));

            daoToTest.addShip(2, cruiserToAdd);
            retrievedGame = daoToTest.getGameById(1);
            List<Ship> player2Ships = retrievedGame.getPlayer2().getPlacedShips();
            assertEquals(1, player2Ships.size());
            assertEquals("Cruiser", player2Ships.get(0).getShipType());
            assertTrue(player2Ships.get(0).getHorizontal());
            assertEquals(1, player2Ships.get(0).getStartingSquare().x);
            assertEquals(1, player2Ships.get(0).getStartingSquare().y);

        } catch (InvalidIdException | InvalidShipException | NullInputException | NullBoardException | InvalidPlacementException | InvalidBoardException ex) {
            fail("Unexpected exception during golden path test" + ex.getMessage());
        }
    }

    @Test
    void getOccupiedSquares() {
        try {
            BattleshipBoard playerBoard = daoToTest.getPlayerBoard(1);
            List<Point> occupiedSquares = daoToTest.getOccupiedSquares(playerBoard);

            assertEquals(5, occupiedSquares.size());
            assertEquals(0, occupiedSquares.get(0).x);
            assertEquals(0, occupiedSquares.get(0).y);
            assertEquals(1, occupiedSquares.get(1).x);
            assertEquals(0, occupiedSquares.get(1).y);
            assertEquals(2, occupiedSquares.get(2).x);
            assertEquals(0, occupiedSquares.get(2).y);
            assertEquals(3, occupiedSquares.get(3).x);
            assertEquals(0, occupiedSquares.get(3).y);
            assertEquals(4, occupiedSquares.get(4).x);
            assertEquals(0, occupiedSquares.get(4).y);

        } catch (InvalidIdException | InvalidPlayerException | InvalidShipException | NullBoardException | InvalidBoardException ex) {
            fail("Unexpected exception in golden path test: " + ex.getMessage());
        }
    }

    @Test
    void getOccupiedSquaresNullBoard() {
        try {
            List<Point> occupiedSquares = daoToTest.getOccupiedSquares(null);
            fail("Expected NullBoardException");

        } catch (InvalidShipException ex) {
            fail("Unexpected exception in null board test: " + ex.getMessage());
        } catch (NullBoardException ex) {

        }
    }

    @Test
    void addHit() {
        try {
            // Add a new hit
            Point newHit = new Point(1, 1);
            daoToTest.addHit(1, newHit);
            //Check that the hit was added
            BattleshipBoard retrievedBoard = daoToTest.getPlayerBoard(1);
            assertEquals(2,retrievedBoard.getBoardHits().size());
            assertEquals(1,retrievedBoard.getBoardHits().get(1).x);
            assertEquals(1,retrievedBoard.getBoardHits().get(1).y);

        } catch(InvalidHitException | NullInputException | InvalidBoardException | InvalidPlayerException | InvalidIdException ex) {
            fail("Unexpected exception in golden path test: " + ex.getMessage());
        }
    }

    @Test
    void addHitNullBoard() {
        try {
            // Add a new hit
            Point newHit = new Point(1, 1);
            daoToTest.addHit(null, newHit);
            //Check that the hit was added
            fail("Expected error in NullPoint test");

        } catch(InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception in NullPoint test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void addHitNullPoint() {
        try {
            daoToTest.addHit(1, null);
            //Check that the hit was added
            fail("Expected error in NullPoint test");

        } catch(InvalidHitException | InvalidBoardException ex) {
            fail("Unexpected exception in NullPoint test: " + ex.getMessage());
        } catch(NullInputException ex) {

        }
    }

    @Test
    void addHitInvalidBoard() {
        try {
            // Add a new hit
            Point newHit = new Point(1, 1);
            daoToTest.addHit(3, newHit);
            //Check that the hit was added
            fail("Expected error in NullPoint test");

        } catch(InvalidHitException | NullInputException ex) {
            fail("Unexpected exception in NullPoint test: " + ex.getMessage());
        } catch(InvalidBoardException ex) {

        }
    }

    @Test
    void addHitExistingPoint() {
        try {
            // Add a new hit
            Point newHit = new Point(0, 0);
            daoToTest.addHit(1, newHit);
            fail("Expected error in ExistingPoint test");

        } catch(NullInputException | InvalidBoardException ex) {
            fail("Unexpected exception in NullPoint test: " + ex.getMessage());
        } catch(InvalidHitException ex) {

        }
    }
}
