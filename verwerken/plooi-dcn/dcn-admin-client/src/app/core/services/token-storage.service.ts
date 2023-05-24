import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../authentication/services/auth.service";

const TOKEN_KEY = 'auth-token';
const USER_NAME = 'user-name';
const REFRESH_KEY= 'refresh-token'
const PREVIOUS_URL= 'last_URL'
const SEARCH_FILTER = 'search_filter'
@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor(private router: Router, private authService : AuthService) { }
  refreshToken(): void {
   this.authService.refreshToken(window.localStorage.getItem(USER_NAME),window.localStorage.getItem(REFRESH_KEY)).subscribe(
       data => {
         this.saveToken(data.body.token);
         this.saveUserName(data.body.userName)
         this.saveRefreshToken(data.body.refreshToken);
         window.location.reload();
       }, error => {
          this.logout();
       }
   );
  }

  public logout(){
      window.localStorage.clear();
      this.router.navigate(["login"]);
  }

  public saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public saveUserName(username: string): void {
    window.localStorage.removeItem(USER_NAME);
    window.localStorage.setItem(USER_NAME, username);
  }

  public saveRefreshToken(refreshToken: string): void {
    window.localStorage.removeItem(REFRESH_KEY);
    window.localStorage.setItem(REFRESH_KEY, refreshToken);
  }

  public getToken(): string | null {
    return window.localStorage.getItem(TOKEN_KEY);
  }

  public getUser(): string | null {
    return window.localStorage.getItem(USER_NAME);
  }

  public setUrl(url:string) {
      window.localStorage.setItem(PREVIOUS_URL, JSON.stringify(url));
  }
  public getUrl(): string {
        return  JSON.parse(window.localStorage.getItem(PREVIOUS_URL));
  }

  public saveFilter(filer: string){
      window.localStorage.setItem(SEARCH_FILTER ,filer)
  }

  public getSearchFilter(){
        return JSON.parse(window.localStorage.getItem(SEARCH_FILTER));
  }

  public removeSearchFilter(){
        window.localStorage.removeItem(SEARCH_FILTER);
  }
}