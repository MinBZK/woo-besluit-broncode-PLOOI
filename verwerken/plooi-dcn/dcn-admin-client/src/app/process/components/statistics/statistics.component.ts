import { Component, OnInit } from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ModalService} from "../../services/modal.service";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {DocumentStatistics} from "../../models/documentStatistics";

@Component({
  selector: 'dcn-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent extends  BaseComponent  implements OnInit {
  totalErrors: number;
  totalMappings: number;
  totalWarnings: number;

  reverseSorted: boolean = false;
  errors: DocumentStatistics[];

  constructor(public adminApi: AdminApiService,
              public router: Router,
              public route: ActivatedRoute,
              public utility: ModalService,
              public state: StateService,
              public dialog: MatDialog) {
    super(adminApi, state, route, router, utility, dialog);
  }


  ngOnInit(): void {
    this.getData();
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  sortByBron() {
    this.reverseSorted = !this.reverseSorted;
    this.getData();
  }

  currentClass() {
    return this.reverseSorted ? 'sort--descending is-active' : 'sort--ascending is-active';
  }

  getData() {
    this.adminApi.getDocumentStatistics(this.reverseSorted)
        .subscribe(result => {
          this.errors = result;
          this.totalErrors = result.map(e => e.processingErrorCount).reduce((total, val) => total + val, 0);
          this.totalMappings = result.map(e => e.mappingErrorCount).reduce((total, val) => total + val, 0);
          this.totalWarnings = result.map(e => e.mappingWarningCount).reduce((total, val) => total + val, 0);
        }, error => {
          this.errorMsg = error;
        })
  }
}
