import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {ProcessingError} from "../../models/processing-error";

@Component({
  selector: 'dcn-processing-error-row',
  templateUrl: './processing-error-row.component.html',
  styleUrls: ['./processing-error-row.component.css']
})
export class ProcessingErrorRowComponent extends BaseComponent  implements OnInit, OnChanges {
  @Input() documentEventId: any;
  @Input() id: any;
  @Input() processingError = new ProcessingError();
  @Input() exceptie: any;


  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService) {

    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnInit(): void {
    if(this.documentEventId) {
      this.adminApi.getErrorDetails(this.documentEventId).subscribe(e => {
        this.processingError = e.exceptie
      }, error => {
        this.errorMsg = error;
      })
    }


  }

  ngOnChanges() {
    this.ngOnInit();
  }

  goToErrorDetails() {
    this.router.navigate( ['/home/errors/document', this.id]);
  }

}