import {Component, OnInit} from '@angular/core';
import {AdminApiService} from '../../services/admin-api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {takeUntil} from 'rxjs/operators';
import {StateService} from "../../../core/services/state.service";
import {BaseComponent} from "../base/base.component";
import {ModalService} from "../../services/modal.service";
import {MatDialog} from "@angular/material/dialog";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'app-mapping-error-overview',
    templateUrl: './mapping-error-overview.component.html',
    styleUrls: ['./mapping-error-overview.component.css']
})
export class MappingErrorOverviewComponent extends BaseComponent implements OnInit {
    type = this.route.snapshot.paramMap.get('type');

    constructor(public adminApi: AdminApiService,
                public  state : StateService,
                public route: ActivatedRoute,
                public router: Router, public utility : ModalService, public dialog: MatDialog) {
        super(adminApi, state, route, router, utility, dialog);
    }

    ngOnInit(): void {
        this.getData();
    }

    ngOnDestroy() {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }
    private getData(): void {

            this.adminApi.getMappingErrors(this.source, this.type, this.page)
                .subscribe(page => {
                    this.page = page;
                }, error => {
                    this.errorMsg = error;
                })
    }

    private redoMappingError(error: any) {
        this.adminApi.redoMappingError(this.source, error.targetElementName, error.sourceLabel, this.type)
            .pipe(takeUntil(this.unsubscribe$))
            .subscribe(result => {
                this.ngOnInit()
            }, error => {
                this.errorMsg = error
            });
    }
    getTitle():string{
        return MappingErrorOverviewComponent.getTitleFromParams(this.type,this.source);
    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        let type:string  = (route.params as BehaviorSubject<any>).value.type;
        let source:string  = (route.params as BehaviorSubject<any>).value.source;
        return this.getTitleFromParams(type,source);
    }

    static getTitleFromParams(severity:string, source: string) {
        return (severity == 'WARNING'? 'Mappingwaarschuwingen': 'Mappingfouten') +" voor "+ source;
    }
}
