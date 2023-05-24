import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AdminApiService} from "../../services/admin-api.service";
import {SolrSearchFilter} from "../../models/solr-search-filter";
import {Page} from "../../../core/models/page";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseComponent} from "../base/base.component";
import {StateService} from "../../../core/services/state.service";
import {ModalService} from "../../services/modal.service";

@Component({
  selector: 'app-solr-json-details',
  templateUrl: './solr-json-details.component.html',
  styleUrls: ['./solr-json-details.component.css']
})
export class SolrJsonDetailsComponent extends BaseComponent implements OnInit {
  query: string;
  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService, public dialogRef: MatDialogRef<SolrJsonDetailsComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    super(adminApi, state, route, router, utility, dialog);
    this.getData();
  }


  onNoClick(): void {
    this.dialogRef.close();
  }

  documentDetails(id: string) {
    let queryParams = {
      'searchType': 'plooi',
      'plooiId': id
    }

    const url = this.router.serializeUrl(
        this.router.createUrlTree(['home/document-details'], {queryParams: queryParams})
    );

    window.open(url, '_blank');
  }

  getData(){
    this.query = this.data.document.map(item => 'dcn_id:'.concat(item.fromId)).join(' OR ');
    let solrFilter = new SolrSearchFilter();
    solrFilter.query = this.query;
    console.log(solrFilter);
    this.adminApi.getSolrDocumentFilter(solrFilter, this.page).subscribe(result => {
      console.log(result);
      this.page =result;
    }, error => {
      this.errorMsg =  error;
    });
  }
}
