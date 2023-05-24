import { NgModule } from '@angular/core';
import {LoginComponent} from "./components/login/login.component";
import {SharedModule} from "../shared/shared.module";
import {AuthenticationRoutingModule} from "./authentication-routing.module";



@NgModule({
  declarations: [LoginComponent],
  imports: [
      SharedModule, AuthenticationRoutingModule
  ]
})
export class AuthenticationModule { }
