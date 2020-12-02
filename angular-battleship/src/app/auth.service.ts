import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from './login-request';
import { MessageResponse } from './message-response';
import { RegistrationRequest } from './registration-request';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  constructor(
    private client: HttpClient
  ) { }

  login(request: LoginRequest): Observable<User> {
    return this.client.post<User>(
    "http://localhost:8080/api/auth/login", 
    request,
    {
      headers: new HttpHeaders({"Content-Type": "application/json"})
    });
  }

  register(registrationData: RegistrationRequest) {
    return this.client.post<MessageResponse>(
      "http://localhost:8080/api/auth/register", 
      registrationData,
      {
        headers: new HttpHeaders({"Content-Type": "application/json"})
      }
    )
  }
}