import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";
import {SolrJsonDetailsComponent} from "../solr-json-details/solr-json-details.component";

@Component({
  selector: 'dcn-document-solr',
  templateUrl: './document-solr.component.html',
  styleUrls: ['./document-solr.component.css']
})
export class DocumentSolrComponent extends BaseComponent implements OnInit, OnChanges {
  @Input() internalId;
  solrDocument: any;
  portalUrl: string;
  duplicateList: any[];
  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService) {
    super(adminApi, state, route, router, utility, dialog);
  }

  ngOnInit(): void {}

  ngOnChanges() {
    this.getData();
  }

  getData(){
    if(this.internalId) {
      this.adminApi.getSolrDocument("dcn_id:".concat(this.internalId).concat(" ").concat("OR").concat(" ").concat("id:".concat(this.internalId)))
          .subscribe(result => {
            this.solrDocument = result[0];
          },error => {
            this.errorMsg = error;
          }, () => {
            if(this.solrDocument?.identifier)  {
              this.adminApi.getPortalUrl().subscribe(url => {
                this.portalUrl = url + '/Details/' + this.solrDocument.identifier + '/1'
              }, error => {
                this.errorMsg = error;
              })
            }
          });
    }
  }


  openSolr(){
    var myjson = JSON.stringify(this.solrDocument, null, 4);
    var x = window.open();
    x.document.open();
    x.document.write('<html><body><pre>' + myjson + '</pre></body></html>');
    x.document.close();
  }

  openPortal(){
    window.open( this.portalUrl, "_blank");
  }

  openDuplication() {
    this.dialog.open(SolrJsonDetailsComponent, {
      data: {
        document: this.duplicateList,
        dcnId: this.internalId
      }
    });
  }
}
