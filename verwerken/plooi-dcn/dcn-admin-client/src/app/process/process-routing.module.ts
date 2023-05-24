import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MappingErrorOverviewComponent} from "./components/mapping-error-overview/mapping-error-overview.component";
import {DocumentListComponent} from "./components/document-list/document-list.component";
import {ErrorsOverviewComponent} from "./components/errors-overview/errors-overview.component";
import {ErrorDetailsComponent} from "./components/error-details/error-details.component";
import {DocumentDetailsComponent} from "./components/document-details/document-details.component";
import {SolrDocumentListComponent} from "./components/solr-document-list/solr-document-list.component";
import { ExecutionDetailsComponent } from './components/execution-details/execution-details.component';

const routes: Routes = [
    {
        path: 'mapping/:source/:type',
        component: MappingErrorOverviewComponent,
        data: {'breadcrumb': 'Mappingfouten'},
        children: [
            {
                path: 'documents/:label/:target',
                component: DocumentListComponent,
                data: {'breadcrumb': 'Veldfouten'}
            }
        ]
    },{
        path: 'errors/:source',
        component: ErrorsOverviewComponent,
        data: {'breadcrumb': 'Verwerkingsfouten'},
        children: [
            {
                path: 'document/:errorId',
                component: ErrorDetailsComponent,
                data: {'breadcrumb': 'Details Foutmelding'}
            }
        ]
    },{
        path: 'errors',
        component: ErrorsOverviewComponent,
        data: {'breadcrumb': 'Verwerkingsfouten'},
        children: [
            {
                path: 'document/:errorId',
                component: ErrorDetailsComponent,
                data: {'breadcrumb': 'Details Foutmelding'}
            }
        ]
    },{
        path: 'execution-details',
        component: ExecutionDetailsComponent,
        data: {'breadcrumb': 'Execution Details'}
    },{
        path: 'document-details',
        component: DocumentDetailsComponent,
        data: {'breadcrumb': 'Document Details'}
    },{
        path: 'search-documents',
        component: SolrDocumentListComponent,
        data: {'breadcrumb': 'gevonden documenten'}, children : [
            {
                path: 'document-details',
                component: DocumentDetailsComponent,
                data: {'breadcrumb': 'Document Details'}
            }
        ]
    }];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ProcessRoutingModule { }