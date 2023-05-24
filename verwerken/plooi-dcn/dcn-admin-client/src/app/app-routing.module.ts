import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeOverviewComponent} from './process/components/home-overview/home-overview.component';
import {AuthGuardService as AuthGuard} from './core/services/auth-guard.service';
import {AuthenticationRoutingModule} from "./authentication/authentication-routing.module";
import {CoreRoutingModule} from "./core/core-routing.module";
import {ProcessRoutingModule} from "./process/process-routing.module";

const routes: Routes = [
    {path: '',  loadChildren: () => AuthenticationRoutingModule},
    {path: 'home', component: HomeOverviewComponent, data: {'breadcrumb': 'Home'}, canActivate : [AuthGuard],
        loadChildren: () => ProcessRoutingModule},
    {path: '**', loadChildren: () => CoreRoutingModule}

];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
