import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import localeNL from '@angular/common/locales/nl';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {CommonModule, registerLocaleData} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ProcessModule} from './process/process.module';
import {AuthenticationModule} from "./authentication/authentication.module";
import {CoreModule} from "./core/core.module";
import {SearchDocumentModule} from "./search-document/search-document.module";

registerLocaleData(localeNL);

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        CommonModule,
        BrowserAnimationsModule, 
        CoreModule, 
        ProcessModule, 
        AuthenticationModule,
        SearchDocumentModule
    ],
    providers: [{ provide: LOCALE_ID, useValue: 'nl-NL' }],
    bootstrap: [AppComponent]
})
export class AppModule {
}
