import {Component, Input, OnInit, QueryList, ViewChildren} from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {ProcessingErrorRowComponent} from "../processing-error-row/processing-error-row.component";
import {DiagnosticsListComponent} from "../diagnostics-list/diagnostics-list.component";

@Component({
  selector: 'dcn-document-severity',
  templateUrl: './document-severity.component.html',
  styleUrls: ['./document-severity.component.css']
})
export class DocumentSeverityComponent extends BaseComponent implements OnInit {

  @Input() executionId;
  @Input() severity;
  @Input() internalId;
  @Input() errors: any[];
  @ViewChildren(ProcessingErrorRowComponent) pErrorRows: QueryList<any>;
  @ViewChildren(DiagnosticsListComponent) diagnosticErrors: QueryList<any>;

  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService) {
    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(){
    this.adminApi.getExecutionDocumentEvents(this.executionId,  this.page, this.severity ? this.severity: null).subscribe(result => {
      this.page = result;
    }, error => {
      this.errors = error;
    })
  }


  getException(item: any) {
    let display = document.getElementById(item.id).style.display;
    if(display === 'none') {
      document.getElementById(item.id).removeAttribute('style');
    } else {
      document.getElementById(item.id).style.display = 'none';
    }
    if(item.severity === 'EXCEPTION') {
      let row: ProcessingErrorRowComponent = this.pErrorRows.filter(e => e.id == item.id).pop()
      row.documentEventId = item.id;
      row.ngOnInit();
    } else {
      let row: DiagnosticsListComponent = this.diagnosticErrors.filter(e => e.id == item.id).pop()
      row.documentEventId = item.id;
      row.ngOnChanges();
    }
  }
}
