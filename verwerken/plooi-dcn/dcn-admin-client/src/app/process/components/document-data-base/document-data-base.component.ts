import {Component, Input, EventEmitter, OnChanges, OnInit, Output} from '@angular/core';
import {BaseComponent} from "../base/base.component";
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {DocumentState} from "../../models/document-state";

@Component({
    selector: 'dcn-document-data-base',
    templateUrl: './document-data-base.component.html',
    styleUrls: ['./document-data-base.component.css']
})
export class DocumentDataBaseComponent extends BaseComponent implements OnInit, OnChanges {
    document: DocumentState;
    @Input() plooiId;
    @Output() deletedFile = new EventEmitter<boolean>();
    isLoading:boolean = false;

    constructor(public adminApi: AdminApiService,
                public route: ActivatedRoute,
                public state: StateService,
                public router: Router,
                public dialog: MatDialog, public utility: ModalService) {
        super(adminApi, state, route, router, utility, dialog);

    }

    ngOnChanges() {
        this.getData();
    }

    ngOnInit(): void {
    }
    getData() {
        if (this.plooiId) {
            this.isLoading = true;
            this.adminApi.getDocumentsEventState(this.plooiId)
                .subscribe(documentEventState => {
                    this.isLoading = false;
                    this.document = documentEventState;
                }, error => {
                    this.isLoading = false;
                    this.errorMsg = error;
                });
        }
    }
}
