import {Component, OnDestroy, OnInit} from '@angular/core';
import {AdminApiService} from '../../services/admin-api.service';
import {ActivatedRoute, Router} from "@angular/router";
import {ModalService} from "../../services/modal.service";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {BaseComponent} from "../base/base.component";

@Component({
    selector: 'app-home-overview',
    templateUrl: './home-overview.component.html',
    styleUrls: ['./home-overview.component.css']
})
export class HomeOverviewComponent extends  BaseComponent implements OnInit, OnDestroy {
    constructor(public adminApi: AdminApiService,
                public router: Router,
                public route: ActivatedRoute,
                public utility: ModalService,
                public state: StateService,
                public dialog: MatDialog) {
        super(adminApi, state, route, router, utility, dialog);
    }

    static getTitleFromRoute(route:ActivatedRoute):string{
        return "Home";
    }
}


