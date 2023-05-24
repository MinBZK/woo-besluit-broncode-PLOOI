import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {DateRangeField} from "../../models/date-range-field";
import {SolrSearchFilter} from "../../models/solr-search-filter";
import {SimpleField} from "../../../search-document/models/simple-field";
import {OptionField} from "../../../search-document/models/option-field";
import {MetaDetailComponent} from "../meta-detail/meta-detail.component";
import {BulkActionRequest} from "../../models/bulk-action-request";
import {Action, ActionName, Trigger, TriggerAction} from "../../models/trigger-action";
import {TokenStorageService} from "../../../core/services/token-storage.service";

@Component({
    selector: 'app-solr-document-list',
    templateUrl: './solr-document-list.component.html',
    styleUrls: ['./solr-document-list.component.css']
})
export class SolrDocumentListComponent extends BaseComponent implements OnInit {
    solrFilter : SolrSearchFilter;
    static readonly title_field = 'title';
    static readonly creator = 'creator';
    REPROCESS: TriggerAction;
    DELETE : TriggerAction;
    queryParams : any;
    constructor(public adminApi: AdminApiService,
                public route: ActivatedRoute,
                public state: StateService,
                public router: Router,
                public dialog: MatDialog, public utility: ModalService, private tokenService: TokenStorageService) {
        super(adminApi, state, route, router, utility, dialog);
        this.REPROCESS = new TriggerAction(Trigger.REPROCESS, Action.REPROCESS, ActionName.REPROCESS);
        this.DELETE = new TriggerAction(Trigger.DELETION, Action.DELETION, ActionName.DELETION);
    }

    ngOnInit(): void {
            let parms = this.tokenService.getSearchFilter();
            this.solrFilter = SolrDocumentListComponent.extractFilterFields(parms);
            this.adminApi.getSolrDocumentFilter(this.solrFilter, this.page)
                .subscribe(result => {
                    this.page = result;
                    if (this.page.content.length == 1) {
                        let queryParams = {'searchType': 'plooi', 'plooiId': this.page.content[0]?.dcn_id}
                        this.router.navigate(['document-details'], {queryParams: queryParams, relativeTo: this.route});
                    }
                }, error => {
                    this.errorMsg = error;
                });
    }
    private static extractFilterFields(params: any) {
        let solrFilter = new SolrSearchFilter();
        if(params ==  null) {
            return;
        }
        let fromDate = new Date(params['fromDate']).getTime();
        let toDate = new Date(params['toDate']).getTime();

        if (params['query']) {
            solrFilter.query = params['query'];
        }
        if (params[this.title_field]) {
            solrFilter.textFields.push( new SimpleField(this.title_field, params[this.title_field]));
        }
        if (params[this.creator] && Array.from(params[this.creator]).length) {
            solrFilter.filterFields.push(new OptionField('verantwoordelijke', params[this.creator]));
        }
        if (params['type'] && Array.from(params['type']).length) {
            solrFilter.filterFields.push(new OptionField("type", params['type']));
        }
        if (params['plooiId']) {
            solrFilter.textFields.push( new SimpleField("id", params['plooiId']));
        }
        if (params['externalId']) {
            solrFilter.textFields.push(new SimpleField("external_id",  params['externalId']));
        }

        solrFilter.dateRangeField = new DateRangeField('available_start', fromDate, toDate);
        return solrFilter;
    }

    documentDetails(document) {
        let queryParams = {
            'searchType': 'plooi',
            'plooiId': document.dcn_id ? document.dcn_id : document.id
        }
        this.router.navigate(['document-details'], {queryParams: queryParams, relativeTo: this.route});
    }

    applyBulkDocumentAction(triggerAction : TriggerAction){
        let dialogRef = this.dialog.open(MetaDetailComponent);
        let instance = dialogRef.componentInstance;
        instance.numberOfDocuments = this.page.totalElements;
        instance.triggerAction = triggerAction;
        instance.passEntry.subscribe(reason => {
            if(reason !== null) {
                let bulkRequest = new BulkActionRequest(triggerAction.trigger, this.solrFilter, triggerAction.action, reason);
                this.adminApi.applyDocumentActionBulk(bulkRequest)
                    .subscribe( actionResult =>  this.router.navigate([ `home`])
                        , error => this.errorMsg = error);

            }
            instance.onNoClick();
        });

    }
}
