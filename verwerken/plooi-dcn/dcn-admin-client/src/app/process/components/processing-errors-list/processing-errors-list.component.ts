import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { BaseComponent } from "../base/base.component";
import { AdminApiService } from "../../services/admin-api.service";
import { ActivatedRoute, Router } from "@angular/router";
import { StateService } from "../../../core/services/state.service";
import { MatDialog } from "@angular/material/dialog";
import { ModalService } from "../../services/modal.service";
import { ProcessingError } from "../../models/processing-error";

@Component({
  selector: 'dcn-processing-errors-list',
  templateUrl: './processing-errors-list.component.html',
  styleUrls: ['./processing-errors-list.component.css']
})
export class ProcessingErrorsListComponent extends BaseComponent implements OnChanges {

  @Input() documentEventId;
  @Input() excepties = [];
  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog,
              public utility: ModalService) {
    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnChanges(): void {
    this.page.content = this.excepties;
 }

  openErrorDetail(processingError: ProcessingError) {
    this.router.navigate([ `home/errors/${processingError.sourceName}/document`, processingError.id]);
  }

}
