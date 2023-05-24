import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {TokenStorageService} from "./token-storage.service";

@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate {
    constructor(private tokenStorageService: TokenStorageService, private router: Router) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (this.tokenStorageService.getToken()) {
            return true
        } else {
            this.tokenStorageService.setUrl(state.url)
            this.router.navigate(['login']);
            return false;
        }
    }
}