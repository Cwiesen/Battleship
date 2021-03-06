package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;

import java.util.List;

public interface BattleshipDao {
    List<BattleshipGame> getAllGames() throws InvalidIdException, InvalidPlayerException, InvalidBoardException;

    BattleshipGame addGame(Integer player1, Integer player2) throws InvalidPlayerException, InvalidIdException;

//    Integer createNewPlayer();

    BattleshipGame getGameById(Integer gameId) throws InvalidIdException, InvalidPlayerException;

    BattleshipBoard getPlayerBoard(Integer boardId) throws InvalidPlayerException, InvalidIdException, InvalidBoardException;

    BattleshipBoard addShip(Integer boardId, Ship toPlace) throws InvalidIdException, InvalidPlayerException, InvalidShipException, NullInputException, NullBoardException, InvalidPlacementException, InvalidBoardException;

    BattleshipBoard addHit(Integer boardId, Point hitToAdd) throws NullInputException, InvalidIdException, InvalidPlayerException, InvalidHitException, InvalidBoardException, InvalidShipException, NullBoardException, InvalidGameException;

    void updatePlayerTurn(Integer gameId) throws NullGameException, InvalidIdException, InvalidPlayerTurnException, InvalidPlayerException;

    List<Point> getOccupiedSquares(BattleshipBoard playerBoard) throws InvalidShipException, NullBoardException;

    List<BattleshipGame> getGamesByUsername(String playerId) throws InvalidPlayerException;

    void reset();

    List<HitPoint> getBoardHits(Integer boardId) throws InvalidBoardException, InvalidShipException, NullBoardException;
}
