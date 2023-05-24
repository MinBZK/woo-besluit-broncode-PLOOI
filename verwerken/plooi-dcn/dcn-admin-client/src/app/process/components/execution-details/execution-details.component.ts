import {Component, OnInit, ViewChild} from '@angular/core';
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";
import {Severity} from "../../models/Severity";
import {ProcesVerwerkingsSeverityStatus} from "../../models/procesVerwerkingsSeverityStatus";
import {ProcessingErrorRowComponent} from "../processing-error-row/processing-error-row.component";
import {DocumentSeverityComponent} from "../document-severity/document-severity.component";
import {DocumentEventStatusResponse} from "../../models/document-event-status-response";
import {Proces} from "../../models/proces";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'app-execution-details',
    templateUrl: './execution-details.component.html',
    styleUrls: ['./execution-details.component.css']
})
export class ExecutionDetailsComponent extends BaseComponent implements OnInit {
    static readonly TITLE: string = "Execution Details";
    readonly title: string = ExecutionDetailsComponent.TITLE;
    executionSummary: Proces;
    executionId: string;
    eDSS : ProcesVerwerkingsSeverityStatus;
    severity: string;
    errors: DocumentEventStatusResponse[];
    @ViewChild(ProcessingErrorRowComponent) processingErrorRowComponent:ProcessingErrorRowComponent;
    @ViewChild(DocumentSeverityComponent) documentSeverityComponent:DocumentSeverityComponent;


    constructor(public adminApi: AdminApiService,
        public route: ActivatedRoute,
        public state: StateService,
        public router: Router,
        public dialog: MatDialog, public utility: ModalService) {
        super(adminApi, state, route, router, utility, dialog);
    }

    clickOnDocumentEvent(internalId) {
        let queryParams = {
            'plooiId' : internalId,
            'searchType' : 'plooi'
        }
        this.router.navigate(['/home/document-details'], { queryParams: queryParams  });
    }

    ngOnInit(): void {
      this.route.queryParamMap
        .subscribe(params => {
          this.executionId = params.get('executionId');
          this.adminApi.getExecutionSummary(this.executionId)
            .subscribe(
                result => {
                        this.executionSummary = result
                },
                error => { this.errorMsg = error}
            );
        });
    }

    getDocumentEventWithsSeverity(severity?: string){
        this.documentSeverityComponent.page.pageable.pageNumber = 0;
        this.documentSeverityComponent.severity = severity;
        this.documentSeverityComponent.ngOnInit();
    }

    getStyle(severity: string, number: number) {
        return severity === Severity.EXCEPTION.toString() && number ?"background-color: #d52b1e; color: white" : '';
    }

    goToErrorDetails(processingError: any) {
        this.state.updateProcessError(processingError);
        this.router.navigate( ['/home/errors/document', ""]);

    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        let params = (route.queryParams as BehaviorSubject<any>).value;
        return this.TITLE+" voor "+params['executionId'];
    }
}
