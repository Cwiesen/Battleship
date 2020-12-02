import { ApplicationRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenStorageService } from '../token-storage.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  loggedIn:boolean = false;
  timerInterval: any;
  username: string;

  constructor(
    private appRef: ApplicationRef,
    private activeRoute: ActivatedRoute,
    private route: Router,
    private tokenStorage: TokenStorageService,
  ) { }

  ngOnInit(): void {
    this.checkLoggedIn();
    this.timerInterval = setInterval(()=>{ 
      this.checkLoggedIn();
    }, 300);
  }

  checkLoggedIn() {
    if (this.tokenStorage.getToken() != null) {
      this.loggedIn=true
      this.username = this.tokenStorage.getUserData().username;
    }
    else this.loggedIn = false;
  }
  logout(){
    window.sessionStorage.clear();
  }
}
