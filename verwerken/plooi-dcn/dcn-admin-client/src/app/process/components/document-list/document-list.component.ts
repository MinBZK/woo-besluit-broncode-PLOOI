import {Component, OnDestroy, OnInit} from '@angular/core';
import {AdminApiService} from '../../services/admin-api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {takeUntil} from 'rxjs/operators';
import {StateService} from "../../../core/services/state.service";
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";
import {BehaviorSubject} from "rxjs";


@Component({
    selector: 'app-document-list',
    templateUrl: './document-list.component.html',
    styleUrls: ['./document-list.component.css']
})
export class DocumentListComponent extends BaseComponent implements OnInit, OnDestroy {
    targetElement = this.route.snapshot.paramMap.get('target');
    mappingLabel = this.route.snapshot.paramMap.get('label');
    severity: string;

    constructor(public adminApi: AdminApiService,
                public route: ActivatedRoute,
                public state: StateService,
                public router: Router,
                public dialog: MatDialog, public utility: ModalService) {
        super(adminApi, state, route, router, utility, dialog);

    }

    ngOnInit(): void {
        this.route.parent.params.subscribe(params => {
            this.source = params['source'];
            this.severity = params['type'];
            this.getData();
        });

    }

    ngOnDestroy() {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    private getData(): void {
        this.targetElement = this.targetElement == 'empty'? '': this.targetElement;
        this.mappingLabel = this.mappingLabel == 'empty'? '': this.mappingLabel;

        this.adminApi.getMappingErrorDocuments(this.source,
            this.mappingLabel, this.targetElement,this.severity, this.page)
            .pipe(takeUntil(this.unsubscribe$))
            .subscribe(page => {
                this.page = page;
            }, error => {

                this.errorMsg = error.message;
            });
    }

    getTitle():string{
      return DocumentListComponent.getTitleFromParams(this.severity,this.source);
    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        let type:string  = (route.parent.params as BehaviorSubject<any>).value.type;
        let source:string  = (route.parent.params as BehaviorSubject<any>).value.source;
        return this.getTitleFromParams(type,source);
    }

    static getTitleFromParams(severity:string, source: string) {
        return (severity == 'WARNING'? 'Mappingwaarschuwingen': 'Mappingfouten') +" voor "+ source;
    }
}
