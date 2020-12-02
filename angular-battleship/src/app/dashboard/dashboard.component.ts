import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Game } from '../game';
import { GameboardService } from '../gameboard.service';
import { NewGameBuilder } from '../new-game-builder';
import { TokenStorageService } from '../token-storage.service';
import { User } from '../user';

const GAME_KEY:string = "player-game";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  userData: User;
  allGames: Game[];
  playerGames: Game[];
  newGameData: NewGameBuilder = {player1: 0, player2: 0};

  constructor(
    private route: Router,
    private gameboardService: GameboardService,
    private tokenStorage: TokenStorageService
  ) { }

  ngOnInit(): void {
    this.getPlayerGames();
    this.userData = this.tokenStorage.getUserData()
    this.newGameData.player1 = this.userData.id;
    if(this.userData.roles.includes('ROLE_ADMIN')) {
      this.gameboardService.getGames()
        .subscribe(games => this.allGames = games);
    }
  }

  getPlayerGames(): void {
    this.gameboardService.getPlayerGames()
      .subscribe(boards => this.playerGames = boards);
  }

  beginGame() {
    this.gameboardService.beginGame(this.newGameData)
      .subscribe();
    this.ngOnInit();
  }

  selectGame(game: Game) {
    window.sessionStorage.removeItem(GAME_KEY);
    window.sessionStorage.setItem(GAME_KEY, game.gameId.toString());
    this.route.navigate(["gameboard"]);
  }
}
