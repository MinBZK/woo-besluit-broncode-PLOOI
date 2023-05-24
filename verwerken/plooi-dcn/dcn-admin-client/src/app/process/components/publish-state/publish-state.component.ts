import { Component, OnInit } from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {StateService} from "../../../core/services/state.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ModalService} from "../../services/modal.service";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'dcn-publish-state',
  templateUrl: './publish-state.component.html',
  styleUrls: ['./publish-state.component.css']
})
export class PublishStateComponent extends BaseComponent implements OnInit {

  constructor(public adminApi: AdminApiService,
              public  state : StateService,
              public route: ActivatedRoute,
              public router: Router, public utility : ModalService, public dialog: MatDialog) {
    super(adminApi, state, route, router, utility, dialog);
  }


  ngOnInit(): void {
    this.adminApi.getNotIndexedPublishStateList(this.page).subscribe(result =>
            this.page =  result
        , error => this.errorMsg =  error)
  }

  toDocumentDetails(dcnId){
    let queryParams = {
      'plooiId' : dcnId,
      'searchType' : 'plooi'
    }
    this.router.navigate(['/home/document-details'], { queryParams: queryParams  });
  }
}
