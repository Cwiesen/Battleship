import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { Game } from './game';
import { Gameboard } from './gameboard';
import { ShipPlacer } from './ship-placer';
import { TokenStorageService } from './token-storage.service';
import { HitPoint } from './hit-point';
import { Point } from './point';
import { HitPlacer } from './hit-placer';
import { NewGameBuilder } from './new-game-builder';

@Injectable({
  providedIn: 'root'
})

export class GameboardService {
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  private gamesUrl = 'http://localhost:8080/api/games';

  constructor(
    private tokenStorage: TokenStorageService,
    private http: HttpClient,
  ) { }

  getGames(): Observable<Game[]> {
      return this.http.get<Game[]>(this.gamesUrl)
      .pipe(
        catchError(this.handleError<Game[]>('getGames', []))
      );
  }
  
  getGame(id: number): Observable<Game> {
    const gameUrl = 'http://localhost:8080/api/gamestate/' + id; 
    return this.http.get<Game>(gameUrl)
      .pipe(
      catchError(this.handleError<Game>(`getGame id=${id}`,))
      );
  }

  beginGame(newGameData: NewGameBuilder): Observable<Game> {
    const newGameUrl = 'http://localhost:8080/api/begin';
    return this.http.post<Game>(newGameUrl, newGameData)
    .pipe(
      catchError(this.handleError<Game>(`beginGame p1=${newGameData.player1} p2=${newGameData.player2}`))
    );
  }

  getPlayerGames(): Observable<Game[]> {
    const username: string = this.tokenStorage.getUserData().username;
    const playerBoardUrl = 'http://localhost:8080/api/gamesByUsername/' + username;
    return this.http.get<Game[]>(playerBoardUrl)
      .pipe(
      catchError(this.handleError<Game[]>(`getPlayerGames id=${username}`,))
      );
  }

  getBoard(id: number): Observable<Gameboard> {
    const gameboardUrl = 'http://localhost:8080/api/playerboard/' + id; 
    return this.http.get<Gameboard>(gameboardUrl)
      .pipe(
      catchError(this.handleError<any>(`getBoard id=${id}`,))
      );
  }

  getBoardHits(id: number): Observable<HitPoint[]> {
    const boardHitsUrl = 'http://localhost:8080/api/boardHits/' + id; 
    return this.http.get<HitPoint[]>(boardHitsUrl)
      .pipe(
      catchError(this.handleError<HitPoint[]>(`getBoardHits id=${id}`,))
      );
  }

  placeShip(shipPlacer: ShipPlacer): Observable<Gameboard> {
    const shipPlacerUrl = 'http://localhost:8080/api/placeship';
    return this.http.put<Gameboard>(shipPlacerUrl, shipPlacer, this.httpOptions).pipe(
      catchError(this.handleError<Gameboard>('placeShip'))
    );
  }

  attack(square: HitPlacer): Observable<string> {
    const attackUrl = 'http://localhost:8080/api/attack';
    return this.http.put<string>(attackUrl, square, this.httpOptions).pipe(
      catchError(this.handleError<string>('attack'))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
  
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
  
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}