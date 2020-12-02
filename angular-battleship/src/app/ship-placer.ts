export interface ShipPlacer {
    gameId: number;
    boardId: number;
    shipType: string;
    isHorizontal: boolean;
    xPos: number;
    yPos: number;
  }