import {Component} from '@angular/core';
import {TokenStorageService} from "./core/services/token-storage.service";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {delay, filter} from "rxjs/operators";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'dcn-admin',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  static readonly DEFAULT_TITLE: string = "DCN-Admin";
  static readonly TITLE_SUFFIX: string = " - " + AppComponent.DEFAULT_TITLE;

  title = "dcn-admin";
  isLoggedIn = false;

  constructor(private tokenStorageService: TokenStorageService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private titleService: Title) {
  }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        let route: ActivatedRoute = this.rootRoute(this.activatedRoute);
        if (typeof route.component['getTitleFromRoute'] === 'function') {
          this.titleService.setTitle(route.component['getTitleFromRoute'](route) + AppComponent.TITLE_SUFFIX);
        } else {
          this.titleService.setTitle(AppComponent.DEFAULT_TITLE);
        }
      });
  }

  getLoggedStatus() {
    return this.tokenStorageService.getToken();
  }

  private rootRoute(route: ActivatedRoute): ActivatedRoute {
    while (route.firstChild) {
      route = route.firstChild;
    }
    return route;
  }
}
