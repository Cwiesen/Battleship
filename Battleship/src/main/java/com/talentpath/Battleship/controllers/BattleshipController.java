package com.talentpath.Battleship.controllers;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import com.talentpath.Battleship.services.BattleshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BattleshipController {

    @Autowired
    BattleshipService service;

    @PostMapping("/begin/{player1}/{player2}")
    public BattleshipGame beginGame(@PathVariable Integer player1, @PathVariable Integer player2) throws InvalidPlayerException, InvalidIdException {
        return service.beginGame(player1, player2);
    }

    @GetMapping("/games")
    public List<BattleshipGame> getAllGames() throws NullGameException, InvalidIdException, InvalidPlayerException, InvalidBoardException {
        return service.getAllGames();
    }

    @GetMapping("/gamestate/{gameId}")
    public BattleshipGame getGame(@PathVariable Integer gameId) throws InvalidIdException, NullGameException, InvalidPlayerException {
        return service.getGameById(gameId);
    }

    @GetMapping("/playerboard/{boardId}")
    public BattleshipBoard getBoard(@PathVariable Integer boardId) throws InvalidIdException, NullGameException, InvalidPlayerException, InvalidBoardException {
        return service.getPlayerBoard(boardId);
    }

    @PutMapping("/placeship")
    public BattleshipBoard placeShip(@RequestBody PlaceShip toPlace) throws InvalidPlacementException, InvalidIdException, InvalidShipException, NullGameException, InvalidPlayerException, NullBoardException, NullInputException, InvalidBoardException {
        return service.placeShip(toPlace);
    }

    @PutMapping("/attack")
    public String attack(@RequestBody PlaceHit toPlace) throws InvalidIdException, NullBoardException, InvalidPlayerException, InvalidShipException, InvalidHitException, NullInputException, InvalidBoardException {
        return service.placeHit(toPlace);
    }
}
