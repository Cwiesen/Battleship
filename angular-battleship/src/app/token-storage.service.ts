import { Injectable } from '@angular/core';
import { User } from './user';

const TOKEN_KEY: string = "auth-token";
const USER_KEY: string = "auth-user";

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  saveToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string {
    return window.sessionStorage.getItem(TOKEN_KEY);
  }

  saveUserData(userData: User): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(userData));
  }

  getUserData(): User {
    return JSON.parse(window.sessionStorage.getItem(USER_KEY));
  }

  logout(): void{
    window.sessionStorage.clear();
  }
}