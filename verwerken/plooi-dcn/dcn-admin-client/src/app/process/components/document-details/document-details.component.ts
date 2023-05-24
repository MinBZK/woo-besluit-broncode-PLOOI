import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";
import {ProcessingErrorRowComponent} from "../processing-error-row/processing-error-row.component";
import {DiagnosticsListComponent} from "../diagnostics-list/diagnostics-list.component";
import {saveAs} from 'file-saver';
import {DocumentVersion} from "../../models/document-version";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'dcn-document-details',
    templateUrl: './document-details.component.html',
    styleUrls: ['./document-details.component.css']
})
export class DocumentDetailsComponent extends BaseComponent implements OnInit {
    plooiId: string;
    documentEventId : string;
    showActions: boolean = false;
    deleteDocument: boolean = false;
    files: DocumentVersion[];
    plooiFiles: any;
    @ViewChildren(ProcessingErrorRowComponent) pErrorRows: QueryList<any>;
    @ViewChildren(DiagnosticsListComponent) diagnosticErrors: QueryList<any>;
    errorId = this.route.snapshot.paramMap.get('plooiId');
    constructor(public adminApi: AdminApiService,
                public route: ActivatedRoute,
                public state: StateService,
                public router: Router,
                public dialog: MatDialog, public utility: ModalService) {
        super(adminApi, state, route, router, utility, dialog);
    }

    ngOnInit(): void {
        this.route.queryParamMap
            .subscribe(params => {
                    this.plooiId = params.get('plooiId');
                this.adminApi.getDocumentEvents( this.plooiId, this.page)
                        .subscribe(page => {
                            this.page = page;
                            this.showActions = this.page.totalElements > 0;
                        }, error => {
                            this.errorMsg = error;
                        });
                },error => this.errorMsg = error
            );
        this.getFiles();
        this.getPlooiFiles();
    }

    showDetails(documentEvent : any) {
        this.documentEventId = documentEvent.id;
        Array.from(document.getElementsByClassName("selected-modal")).forEach((el: Element) => {
            el.classList.remove("selected-modal")
        })
        document.getElementById(documentEvent.id).className += " selected-modal";
    }

    updateDocumentStatus(event){
        this.deleteDocument = event;
    }

    refresh(event) {
        if (event) this.ngOnInit();
    }

    getException(id: string, severity: string) {
        let display = document.getElementById(id).style.display;
        if(display === 'none') {
            document.getElementById(id).removeAttribute('style');
        } else {
            document.getElementById(id).style.display = 'none';
        }
        if(severity === 'EXCEPTION') {
            let row: ProcessingErrorRowComponent = this.pErrorRows.filter(e => e.id == id).pop()
            row.documentEventId = id;
            row.ngOnChanges();
        } else {
            let row: DiagnosticsListComponent = this.diagnosticErrors.filter(e => e.id == id).pop()
             row.ngOnChanges();
        }
    }

    getFiles() {
        this.adminApi.getFiles(this.plooiId).subscribe(result => {
            this.files =  result.versies;
            this.files = this.files.sort((a,b) => a.nummer < b.nummer ? 1 : -1);
        }, error => this.errorMsg = error);
    }

    getPlooiFiles() {
        this.adminApi.getPlooiFiles(this.plooiId).subscribe(result => {
            this.plooiFiles = result.labels;
        }, error => this.errorMsg = error);
    }
    getFile(label :string, versie: string) {
        this.adminApi.getFile(this.plooiId, label, versie);
    }

    openStandardFile(file){
        this.adminApi.getStandardFile(file.value)
    }

    exportZipFile() {
        this.adminApi.getExportFiles(this.plooiId).subscribe(blob =>
            saveAs(blob, this.plooiId + '.zip'));
    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        let params = (route.queryParams as BehaviorSubject<any>).value;
        return "Document details voor "+params['plooiId'];
    }
}
