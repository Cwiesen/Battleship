import { Point } from './point'

export interface Ship {
    shipType: string;
    isHorizontal: boolean;
    startingSquare: Point;
  }