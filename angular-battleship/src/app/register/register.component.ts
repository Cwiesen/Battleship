import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { RegistrationRequest } from '../registration-request';
import { TokenStorageService } from '../token-storage.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  loggedIn: boolean = false;
  registrationData: RegistrationRequest = {username: "", email:"", password: ""};
  confirmPassword: string = "";
  error: string = "";

  constructor(
    private service: AuthService,
    private tokenStorage: TokenStorageService,
    private rte: Router
    ) { }

  ngOnInit(): void {
    if(this.tokenStorage.getToken()){
      this.loggedIn = true;
      this.error = "A user is already logged in."
    }
  }

  onSubmit():void {
    if (this.registrationData.username.length < 6) {
      this.error = "Username must be at least 6 characters."
    }
    else if (this.registrationData.password.length < 6) {
      this.error = "Password must be at least 6 characters."
    }
    else if(this.confirmPassword === this.registrationData.password) {
      this.service.register(this.registrationData).subscribe(message => 
        this.rte.navigate(["login"]));
    }
  }
}
