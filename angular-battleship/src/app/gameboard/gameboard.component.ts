import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.css']
})
export class GameboardComponent implements OnInit {
  boardSquares: number[];

  constructor() { }

  ngOnInit(): void {
    this.boardSquares=[1, 2, 3, 4, 5, 6, 7, 8];
  }

}
