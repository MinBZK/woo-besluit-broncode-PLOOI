import {Component, OnInit} from '@angular/core';
import {ProcessingError} from '../../models/processing-error';
import {MatDialog} from '@angular/material/dialog';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {ModalService} from "../../services/modal.service";

@Component({
    selector: 'app-error-details',
    templateUrl: './error-details.component.html',
    styleUrls: ['./error-details.component.css']
})
export class ErrorDetailsComponent extends BaseComponent implements OnInit {
    verwerkingId = this.route.snapshot.paramMap.get('errorId');
    processingError: ProcessingError;
    errorMsg: string;

    constructor(public adminApi: AdminApiService,
                public route: ActivatedRoute,
                public state: StateService,
                public router: Router,
                public dialog: MatDialog, public utility: ModalService) {
        super(adminApi, state, route, router, utility, dialog);
    }

    ngOnInit(): void {
        if(this.verwerkingId == "") {
            this.state.processError.subscribe(e => {
                this.processingError = e;
            });
        } else if(this.verwerkingId) {
           this.adminApi.getErrorDetails(this.verwerkingId).subscribe(e => {
               this.verwerkingId = e.dcnId;
               this.processingError = e.exceptie
           }, error => {
               this.errorMsg = error;
           })
       }
    }

    clickOnDocument(verwerkingId) {
        let queryParams = {
            'plooiId' : verwerkingId,
            'searchType' : 'plooi'
        }
        this.router.navigate(['/home/document-details'], { queryParams: queryParams  });
    }


}
