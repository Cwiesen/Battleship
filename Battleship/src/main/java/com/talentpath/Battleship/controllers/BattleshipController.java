package com.talentpath.Battleship.controllers;

import com.talentpath.Battleship.exceptions.*;
import com.talentpath.Battleship.models.*;
import com.talentpath.Battleship.services.BattleshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:4200")
public class BattleshipController {

    @Autowired
    BattleshipService service;
    @PostMapping("/begin")
    public BattleshipGame beginGame(@RequestBody NewGameBuilder newGame) throws InvalidPlayerException, InvalidIdException {
        return service.beginGame(newGame.getPlayer1(), newGame.getPlayer2());
    }

    @GetMapping("/games")
    public List<BattleshipGame> getAllGames() throws NullGameException, InvalidIdException, InvalidPlayerException, InvalidBoardException {
        return service.getAllGames();
    }

    @GetMapping("/gamestate/{gameId}")
    public BattleshipGame getGame(@PathVariable Integer gameId) throws InvalidIdException, NullGameException, InvalidPlayerException {
        return service.getGameById(gameId);
    }

    @GetMapping("/gamesByUsername/{username}")
        public List<BattleshipGame> getGamesByUsername(@PathVariable String username) throws InvalidPlayerException {
            return service.getGamesByUsername(username);
        }

    @GetMapping("/playerboard/{boardId}")
    public BattleshipBoard getBoard(@PathVariable Integer boardId) throws InvalidIdException, NullBoardException, InvalidPlayerException, InvalidBoardException {
        return service.getPlayerBoard(boardId);
    }

    @GetMapping("/boardHits/{boardId}")
    public List<HitPoint> getBoardHits(@PathVariable Integer boardId) throws InvalidBoardException, NullBoardException, InvalidShipException {
        return service.getBoardHits(boardId);
    }

    @PutMapping("/placeship")
    public BattleshipBoard placeShip(@RequestBody ShipPlacer toPlace) throws InvalidPlacementException, InvalidIdException, InvalidShipException, NullGameException, InvalidPlayerException, NullBoardException, NullInputException, InvalidBoardException, InvalidPlayerTurnException {
        return service.placeShip(toPlace);
    }

    @PutMapping("/attack")
    public String attack(@RequestBody HitPlacer toPlace) throws InvalidIdException, NullBoardException, InvalidPlayerException, InvalidShipException, InvalidHitException, NullInputException, InvalidBoardException, NullGameException, InvalidPlayerTurnException, InvalidGameException {
        return service.placeHit(toPlace);
    }
}
