package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;

import java.awt.*;
import java.util.List;

public interface BattleshipDao {
    List<BattleshipGame> getAllGames() throws InvalidIdException, InvalidPlayerException, InvalidBoardException;

    BattleshipGame addGame(Integer player1, Integer player2) throws InvalidPlayerException, InvalidIdException;

    Integer createNewPlayer();

    BattleshipGame getGameById(Integer gameId) throws InvalidIdException, InvalidPlayerException;

    BattleshipBoard getPlayerBoard(Integer boardId) throws InvalidPlayerException, InvalidIdException, InvalidBoardException;

    BattleshipBoard addShip(Integer boardId, Ship toPlace) throws InvalidIdException, InvalidPlayerException, InvalidShipException, NullInputException, NullBoardException, InvalidPlacementException, InvalidBoardException;

    BattleshipBoard addHit(Integer boardId, Point hitToAdd) throws NullInputException, InvalidIdException, InvalidPlayerException, InvalidHitException, InvalidBoardException;

    void updatePlayerTurn(Integer gameId) throws NullGameException, InvalidIdException, InvalidPlayerTurnException;

    List<Point> getOccupiedSquares(BattleshipBoard playerBoard) throws InvalidShipException, NullBoardException;

    void reset();

}
