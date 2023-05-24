import { Component, OnInit } from '@angular/core';
import { BaseComponent } from "../base/base.component";
import { AdminApiService } from "../../services/admin-api.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ModalService } from "../../services/modal.service";
import { StateService } from "../../../core/services/state.service";
import { MatDialog } from "@angular/material/dialog";
import { ExecutionStatistics } from "../../models/executionStatistics";
import { Page } from 'src/app/core/models/page';
import {Severity} from "../../models/Severity";

@Component({
  selector: 'execution-statistics',
  templateUrl: './execution-statistics.component.html',
  styleUrls: ['./execution-statistics.component.css']
})
export class ExecutionStatisticsComponent extends  BaseComponent  implements OnInit {

  errors: ExecutionStatistics[];
  withErrors: boolean = false;
  constructor(public adminApi: AdminApiService,
              public router: Router,
              public route: ActivatedRoute,
              public utility: ModalService,
              public state: StateService,
              public dialog: MatDialog) { 
    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnInit() {
    this.getData();
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  getData() {
    this.adminApi.getExecutionStatistics(this.page, this.withErrors)
        .subscribe(result => {
          this.page = result;
        }, error => {
          this.errorMsg = error;
        })
  }

  toMapping(executionId){
    let queryParams = {
      'executionId' : executionId
    }
    this.router.navigate(['/home/execution-details'], { queryParams: queryParams });
  }

  onChange(isChecked) {
    this.withErrors = isChecked.target.checked;
    this.resetPage();
    this.getData();
  }

  resetPage() {
    this.page = new Page();
    this.page.size = 20;
    this.page.number = 0;
  }

  getStyle(severity: string, number: number) {
    return severity === Severity.EXCEPTION.toString() && number ?"background-color: #d52b1e; color: white" : '';
  }
}