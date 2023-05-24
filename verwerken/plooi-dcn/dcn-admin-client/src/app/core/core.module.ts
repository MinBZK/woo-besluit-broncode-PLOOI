import { NgModule } from '@angular/core';
import {BreadcrumbComponent} from "./components/breadcrumb/breadcrumb.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {LoaderComponent} from "./components/loader/loader.component";
import {HeaderComponent} from "./components/header/header.component";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {HttpCallInterceptor} from "./interceptors/http-call.interceptor";
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import {SharedModule} from "../shared/shared.module";
import {RouterModule} from "@angular/router";

@NgModule({
  declarations: [HeaderComponent, LoaderComponent, PageNotFoundComponent, BreadcrumbComponent],
  exports : [LoaderComponent, HeaderComponent],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: HttpCallInterceptor,
    multi: true,
  }, {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  imports: [
    SharedModule,
    RouterModule
  ]
})
export class CoreModule { }