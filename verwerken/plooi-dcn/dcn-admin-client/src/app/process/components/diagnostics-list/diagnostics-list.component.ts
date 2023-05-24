import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";

@Component({
  selector: 'dcn-diagnostics-list',
  templateUrl: './diagnostics-list.component.html',
  styleUrls: ['./diagnostics-list.component.css']
})
export class DiagnosticsListComponent  extends BaseComponent implements OnChanges {
  @Input() documentEventId;
  @Input() id;
  @Input() showPaging : boolean = true;
  @Input() diagnoses= [];
  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService) {
    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnChanges(): void {
    if(this.id) {
      this.adminApi.getErrorDetails(this.id).subscribe(result => {
        this.page.content  = result.diagnoses;
      })
    }

  }
}