import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchFilterComponent } from './components/search-filter/search-filter.component';
import {SharedModule} from "../shared/shared.module";
import { SearchFilterModalComponent } from './components/search-modal/search-filter-modal.component';




@NgModule({
    declarations: [
        SearchFilterComponent,
        SearchFilterModalComponent
    ],
    exports: [
        SearchFilterComponent
    ],
    imports: [CommonModule, SharedModule]
})
export class SearchDocumentModule { }
