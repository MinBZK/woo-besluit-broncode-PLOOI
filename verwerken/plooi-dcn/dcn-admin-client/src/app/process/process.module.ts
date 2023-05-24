import { NgModule } from '@angular/core';
import { ProcessRoutingModule } from './process-routing.module';
import { HomeOverviewComponent } from './components/home-overview/home-overview.component';
import { ErrorsOverviewComponent } from './components/errors-overview/errors-overview.component';
import { MappingErrorOverviewComponent } from './components/mapping-error-overview/mapping-error-overview.component';
import { MetaDetailComponent } from './components/meta-detail/meta-detail.component';
import { DocumentListComponent } from './components/document-list/document-list.component';
import { ErrorDetailsComponent } from './components/error-details/error-details.component';
import { SharedModule } from '../shared/shared.module';
import { BaseComponent } from "./components/base/base.component";
import { SearchDocumentModule } from "../search-document/search-document.module";
import { DocumentDetailsComponent } from "./components/document-details/document-details.component";
import { DocumentDataBaseComponent } from './components/document-data-base/document-data-base.component';
import { DocumentActionsComponent } from './components/document-actions/document-actions.component';
import { DocumentSolrComponent } from './components/document-solr/document-solr.component';
import { SolrJsonDetailsComponent } from "./components/solr-json-details/solr-json-details.component";
import { SolrDocumentListComponent } from "./components/solr-document-list/solr-document-list.component";
import { DiagnosticsListComponent } from './components/diagnostics-list/diagnostics-list.component';
import { ProcessingErrorsListComponent } from './components/processing-errors-list/processing-errors-list.component';
import { StatisticsComponent } from './components/statistics/statistics.component';
import { ExecutionDetailsComponent } from './components/execution-details/execution-details.component';
import { ExecutionStatisticsComponent } from './components/execution-statistics/execution-statistics.component';
import { ProcessingErrorRowComponent } from './components/processing-error-row/processing-error-row.component';
import { DocumentSeverityComponent } from './components/document-severity/document-severity.component';
import { PublishStateComponent } from './components/publish-state/publish-state.component';

@NgModule({
  declarations: [BaseComponent,
      HomeOverviewComponent,
      ErrorsOverviewComponent,
      MappingErrorOverviewComponent,
      MetaDetailComponent,
      DocumentListComponent,
      DocumentDetailsComponent,
      ErrorDetailsComponent,
      DocumentDataBaseComponent,
      DocumentActionsComponent,
      DocumentSolrComponent,
      SolrDocumentListComponent,
      SolrJsonDetailsComponent,
      DiagnosticsListComponent,
      ProcessingErrorsListComponent,
      StatisticsComponent,
      ExecutionStatisticsComponent,
      ExecutionDetailsComponent,
      ProcessingErrorRowComponent,
      DocumentSeverityComponent,
      PublishStateComponent],
  imports: [
    ProcessRoutingModule,
    SharedModule,
    SearchDocumentModule,
  ]
})
export class ProcessModule {}