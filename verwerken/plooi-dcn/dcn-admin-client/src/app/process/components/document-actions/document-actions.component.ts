import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {AdminApiService} from "../../services/admin-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {StateService} from "../../../core/services/state.service";
import {MatDialog} from "@angular/material/dialog";
import {ModalService} from "../../services/modal.service";
import {BaseComponent} from "../base/base.component";

@Component({
  selector: 'dcn-document-actions',
  templateUrl: './document-actions.component.html',
  styleUrls: ['./document-actions.component.css']
})
export class DocumentActionsComponent extends BaseComponent implements OnInit, OnChanges {

  @Input() internalId;
  @Input() showActions;
  @Input() isDeleted;
  @Output() refresh = new EventEmitter<boolean>()
  constructor(public adminApi: AdminApiService,
              public route: ActivatedRoute,
              public state: StateService,
              public router: Router,
              public dialog: MatDialog, public utility: ModalService) {
    super(adminApi, state, route, router, utility, dialog);

  }
  ngOnInit(): void {

  }

  ngOnChanges() {
  }

  applyActionOnDocument(action: string, internalId: string){
    this.adminApi.applyDocumentAction(action, internalId)
        .subscribe( actionResult => this.refresh.emit(true), error => this.errorMsg = error);
  }
}
