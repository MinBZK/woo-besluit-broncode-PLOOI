import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly api = environment.apiUrl;

  constructor(private http: HttpClient) { }
  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.api}/authorization/login`, {"username" : username, "password" : password}, {observe:'response'});
  }

  refreshToken(username: string, refreshToken: string): Observable<any> {
    return this.http.post(`${this.api}/authorization/refreshtoken`, {"username" : username, "refreshToken" : refreshToken}, {observe:'response'});
  }

}
