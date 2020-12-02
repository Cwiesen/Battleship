import { Gameboard } from "./gameboard";

export interface Game {
    gameId: number;
    player1: Gameboard;
    player2: Gameboard;
    playerTurn: number;
}