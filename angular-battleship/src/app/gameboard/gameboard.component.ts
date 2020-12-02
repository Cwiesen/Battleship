import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { GameboardService } from '../gameboard.service'
import { Game } from '../game';
import { Gameboard } from '../gameboard';
import { Ship } from '../ship';
import { ShipPlacer } from '../ship-placer';
import { TokenStorageService } from '../token-storage.service';
import { HitPoint } from '../hit-point';
import { Point } from '../point';
import { HitPlacer } from '../hit-placer';

const GAME_KEY:string = "player-game";

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.css']
})
export class GameboardComponent implements OnInit {
  boardSize: number;
  boardHeaders: number[] = [];
  userId: number;
  boardPlayer: number;
  gameId: number;
  boardId: number;
  opponentId: number;
  game: Game = {
    gameId: 0,
    player1: null,
    player2: null,
    playerTurn: 0
  };
  board: Gameboard;
  opponentBoardHits: HitPoint[] = [];
  boardArray: string[] = [];
  opponentArray: string[] = [];
  shipsToPlace: string[];
  shipToPlace: ShipPlacer;
  isHorizontal: boolean = true;
  message: string;
  hitStatus: string;
  timerInterval: any;

  constructor(
    private route: ActivatedRoute,
    private tokenStorage: TokenStorageService,
    private gameboardService: GameboardService,
    ) { }

  ngOnInit(): void {
    this.boardSize = 10;
    this.buildBoardHeaders();
    this.userId = this.tokenStorage.getUserData().id;
    this.gameId = +window.sessionStorage.getItem(GAME_KEY);
    this.buildBoard();
    
    this.timerInterval = setInterval(()=>{ 
        this.refreshBoard();
      }, 1000);
  }

  routerOnDeactivate() {
    clearInterval(this.timerInterval);
  }

  buildBoard(){
    this.gameboardService.getGame(this.gameId)
      .subscribe(game => {
        this.game = game;
        if (this.game.player1.playerId === this.userId || this.userId == 1) {
          this.boardId = this.game.player1.boardId;
          this.opponentId = this.game.player2.boardId;
          this.boardPlayer = 1;
        }
        else if (this.game.player2.playerId === this.userId) {
          this.boardId = this.game.player2.boardId;
          this.opponentId = this.game.player1.boardId;
          this.boardPlayer = 2;
        } 
        this.gameboardService.getBoard(this.boardId)
            .subscribe(board => {
              this.board = board ; 
              this.shipsToPlace = ["Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"];
              this.buildBoardArray(); 
              if (this.game.playerTurn !== 0) this.getOpponentBoard();
              this.playGame();
            }); 
    });
  }

  refreshBoard(){
    this.gameboardService.getGame(this.gameId)
      .subscribe(game => {
        this.game = game;
      });
    this.gameboardService.getBoard(this.boardId)
      .subscribe(board => {
        this.board = board; 
        this.shipsToPlace = ["Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"];
        this.buildBoardArray(); 
        if (this.game.playerTurn !== 0) this.getOpponentBoard();
        this.playGame(); 
      }
    );
  }

  getBoard(id: number):void {
    if (!id) { return; }
    this.gameboardService.getBoard(id)
      .subscribe(board => this.board = board);
  }

  buildBoardArray() {
    this.boardArray = [];
    let occupiedSquares: number[] = [];
    let hitSquares: number[] = [];

    for (let ship of this.board.placedShips) {
      let shipSize: number = this.getShipSize(ship.shipType);
      for (let i = 0; i < shipSize; i++) {
        if (ship.isHorizontal) {
          occupiedSquares.push(ship.startingSquare.x + (ship.startingSquare.y * this.boardSize) + i);
        } else {
          occupiedSquares.push(ship.startingSquare.x + ((ship.startingSquare.y + i) * this.boardSize));
        }
      }
    }

    for (let hit of this.board.boardHits) {
      hitSquares.push(hit.x + (hit.y * this.boardSize));
    }

    for (let length = 0; length < this.boardSize; length++) {
      for (let width = 0; width < this.boardSize; width++) {
        let currentSquare = width + (length * this.boardSize);
        if (occupiedSquares.includes(currentSquare) && hitSquares.includes(currentSquare)) {
          this.boardArray.push("h");
        } else if (hitSquares.includes(currentSquare)) {
          this.boardArray.push("H");
        } else if (occupiedSquares.includes(currentSquare)) {
          this.boardArray.push("s");
        } else {
          this.boardArray.push("e");
        }
      }
    }
  }

  playGame(){
    if (this.game.playerTurn === 0) {
      let shipsPlaced: Ship[] = this.board.placedShips;
      if (shipsPlaced.length === 5) {
        this.message = "Opponent still setting up."
      }
      else if (shipsPlaced.length >= 0) {
        for (let placedShip of shipsPlaced) {
          if (this.shipsToPlace.includes(placedShip.shipType)) {
            let tempShips: string[] = this.shipsToPlace.slice(0, this.shipsToPlace.indexOf(placedShip.shipType));
            tempShips = tempShips.concat(this.shipsToPlace.slice((this.shipsToPlace.indexOf(placedShip.shipType) + 1)));
            this.shipsToPlace = tempShips;
          }
        }
        this.message = "Place " + this.shipsToPlace[0] + " (" + this.getShipSize(this.shipsToPlace[0]) + " squares)";
      } 
    } else if (this.game.playerTurn == this.boardPlayer) {
      this.message = "Click on a square on the opponent board to fire.";
    } else {
      this.message = "Wait for opponent to finish turn.";
    }
  }

  getOpponentBoard() {
    this.opponentArray = [];
    this.gameboardService.getBoardHits(this.opponentId)
      .subscribe(board => this.opponentBoardHits = board);

    let hitSquares: pointHit[] = [];
    for (let hits of this.opponentBoardHits) {
      let newHit: pointHit = {
        square: hits.x + (hits.y * this.boardSize),
        status: hits.hitStatus
      }
      hitSquares.push(newHit);
    }

    for (let length = 0; length < this.boardSize; length++) {
      for (let width = 0; width < this.boardSize; width++) {
        let currentSquare = width + (length * this.boardSize);
        let hitOne: boolean = false;
        for (let hit of hitSquares) {
          if (hit.square === currentSquare && hit.status == 'Hit') {
            this.opponentArray.push("h");
            hitOne = true;
            break;
          } else if (hit.square === currentSquare && hit.status == 'Miss') {
            hitOne = true;
            this.opponentArray.push("H")
            break;
        } 
        // if (hitSquares.k.includes(currentSquare)) {
        //   this.opponentArray.push("H");
        // } else {
        //   this.opponentArray.push("e");
        // }
      }
      if (!hitOne) {
        this.opponentArray.push("e");
      }
    }
   }
  }

  placeShip(index: number):void {
    let square: Point = {
      x: index % 10,
      y: Math.floor(index / 10)
    }
    let ship: ShipPlacer = {
      gameId: this.game.gameId,
      boardId: this.boardId,
      shipType: this.shipsToPlace[0],
      isHorizontal: this.isHorizontal,
      xPos: square.x,
      yPos: square.y
    }
    this.gameboardService.placeShip(ship)
      .subscribe(board => this.board = board);
  }

  attack(index: number) {
    let attackSquare: HitPlacer = {
      gameId: this.gameId,
      boardId: this.opponentId,
      xPos: index % 10,
      yPos: Math.floor(index / 10)
    }
    this.gameboardService.attack(attackSquare)
      .subscribe(hitStatus => this.hitStatus = hitStatus);
  }

  checkHorizontal(event) {
    if (event.target.checked) {
      this.isHorizontal = true;
    } else {
      this.isHorizontal = false;
    }
  }

  private buildBoardHeaders() {
    for (let i = 0; i < this.boardSize; i++) {
      this.boardHeaders[i] = i + 1;
    }
  }

  private getShipSize(shipType: string): number {
      switch (shipType) {
        case "Carrier":
          return 5;
        case "Battleship":
          return 4;
        case "Cruiser":
        case "Submarine":
          return 3;
        case "Destroyer":
          return 2;
      }
  }
}

interface pointHit {
  square: number,
  status: string
}