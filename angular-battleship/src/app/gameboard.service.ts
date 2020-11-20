import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GameboardService {
  private gameboardUrl = 'api/heroes';

  constructor(
    private http: HttpClient,
  ) { }

  getBoards(): Observable<Gameboard[]> {
    return of(HEROES);
  }
}
