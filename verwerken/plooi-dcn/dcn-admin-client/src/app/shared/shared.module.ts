import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ConnectionErrorComponent} from "./components/connection-error/connection-error.component";
import {FormsModule} from "@angular/forms";
import {MatDialogModule} from "@angular/material/dialog";
import {CustomPaginationComponent} from "./components/pagination/custom-pagination.component";
import { NoResultComponent } from './components/no-result/no-result.component';
import {XmlPipe} from "../core/filter/xml.pipe";
import {ReactiveFormsModule} from "@angular/forms";

@NgModule({
    declarations: [CustomPaginationComponent, ConnectionErrorComponent, NoResultComponent, XmlPipe],
    imports: [
        CommonModule, FormsModule, MatDialogModule,
    ],
    exports: [CustomPaginationComponent, ConnectionErrorComponent, CommonModule,
        FormsModule, MatDialogModule, NoResultComponent, XmlPipe, ReactiveFormsModule]
})
export class SharedModule {
}