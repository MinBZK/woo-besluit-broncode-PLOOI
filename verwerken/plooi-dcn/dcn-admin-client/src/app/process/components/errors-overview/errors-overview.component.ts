import {Component, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, of} from 'rxjs';
import {AdminApiService} from '../../services/admin-api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {catchError, takeUntil} from 'rxjs/operators';
import {StateService} from '../../../core/services/state.service';
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";
import {ProcessingError} from "../../models/processing-error";

@Component({
    selector: 'app-mapping-errors-overview',
    templateUrl: './errors-overview.component.html',
    styleUrls: ['./errors-overview.component.css']
})
export class ErrorsOverviewComponent extends BaseComponent implements OnInit, OnDestroy {
    exceptionList: [];
    constructor(public adminApi: AdminApiService,
                public router: Router,
                public route: ActivatedRoute,
                public utility: ModalService,
                public state: StateService,
                public dialog: MatDialog) {
        super(adminApi, state, route, router, utility, dialog);
    }

    ngOnInit(): void {
        this.getExceptionList();
        this.getData();
    }

    ngOnDestroy() {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    openErrorDetailModel(verwerkingId: string) {
        this.router.navigate(['./document', verwerkingId], {relativeTo: this.route});
    }

    redoDocument(internalId: string) {
        this.adminApi.applyDocumentAction('redo', internalId).pipe(takeUntil(this.unsubscribe$)).subscribe(result => {
            this.ngOnInit()
        }, error => {
            catchError(error => {
                this.errorMsg = error;
                return of(false);
            })
        });
    }

    private getData(exception?: string): void {
        this.adminApi.getErrors(this.page, this.source, exception)
            .pipe(takeUntil(this.unsubscribe$))
            .subscribe(page => {
                this.page = page;
            }, error => {
                this.errorMsg = error;
            });
    }

    getErrorList(exception){
        if(exception) {
            this.getData(exception);
        } else {
            this.ngOnInit();
        }
    }

    getExceptionList(){
        if(this.source) {
            this.adminApi.getProcessingErrorExceptions(this.source)
                .subscribe(result => {
                    this.exceptionList = result
                }, error => {
                    this.errorMsg = error;
                });
        }
    }

    getTitle():string{
        return ErrorsOverviewComponent.getTitleFromParams(this.source);
    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        let source:string  = (route.params as BehaviorSubject<any>).value.source;
        return this.getTitleFromParams(source);
    }

    static getTitleFromParams(source: string) {
        return "Documenten met verwerkingsfouten  "+source;
    }


}
