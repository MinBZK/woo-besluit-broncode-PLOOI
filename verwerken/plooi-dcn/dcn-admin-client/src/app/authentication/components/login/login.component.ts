import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {TokenStorageService} from '../../../core/services/token-storage.service';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService,
              private tokenStorage: TokenStorageService,
              private router: Router) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.router.navigate(["/home"]);
    }
  }

  onSubmit(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe(
      data => {
        this.tokenStorage.saveToken(data.body.token);
        this.tokenStorage.saveUserName(data.body.userName)
        this.tokenStorage.saveRefreshToken(data.body.refreshToken);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.tokenStorage.getUrl() == null ? this.router.navigateByUrl('home') :  this.router.navigateByUrl( this.tokenStorage.getUrl());
      },
      err => {
        this.errorMessage = err.statusText ;
        this.isLoginFailed = true;
      }
    );
  }

  ngOnDestroy(): void {
  }

  static getTitleFromRoute(route:ActivatedRoute):string{
    return "Aanmelden";
  }
}
