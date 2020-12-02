import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { LoginRequest } from '../login-request';
import { TokenStorageService } from '../token-storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loggedIn: boolean = false;
  loginData: LoginRequest = {username: "", password: ""};

  constructor(
    private service: AuthService,
    private tokenStorage: TokenStorageService,
    private rte: Router
  ) { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.service.login(this.loginData).subscribe(user => {
      this.tokenStorage.saveUserData(user);
      this.tokenStorage.saveToken(user.token);
      this.loggedIn = true;
      this.redirectTo("dashboard");
  });
  }

  toRegistration(): void {
    this.rte.navigate(["register"]);
  }

  redirectTo(uri:string){
    this.rte.navigateByUrl('/', {skipLocationChange: true}).then(()=>
    this.rte.navigate([uri]));
 }
}
