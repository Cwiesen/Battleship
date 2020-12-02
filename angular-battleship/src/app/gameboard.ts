import { Point } from './point'
import { Ship } from './ship'

export interface Gameboard {
    boardId: number;
    playerId: number;
    placedShips: Ship[];
    boardHits: Point[];
  }