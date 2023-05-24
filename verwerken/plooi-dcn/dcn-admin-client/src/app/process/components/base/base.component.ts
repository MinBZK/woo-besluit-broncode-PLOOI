import {Component, OnInit} from '@angular/core';
import {AdminApiService} from "../../services/admin-api.service";
import {StateService} from "../../../core/services/state.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Page} from "../../../core/models/page";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {MetaDetailComponent} from "../meta-detail/meta-detail.component";
import {ModalService} from "../../services/modal.service";
import {MatDialog} from "@angular/material/dialog";
import {ProcessingError} from "../../models/processing-error";

@Component({
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.css']
})
export class BaseComponent implements OnInit {
  readonly unsubscribe$: Subject<void> = new Subject();
  page: Page<any> = new Page();
  source = this.route.snapshot.paramMap.get('source');
  errorMsg: string;
  private readonly exceptionLength = 100;


  constructor(public adminApi: AdminApiService,
              public state: StateService,
              public route: ActivatedRoute,
              public router: Router,
              public utility: ModalService, public dialog: MatDialog) {
    this.state.itemsPerPage.subscribe(itemsPerPage => this.page.size = itemsPerPage);

  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  public getNextPage(item: number): void {
    this.page.pageable.pageNumber = item;
    this.ngOnInit();
  }

  updateItemsPerPage(number) {
    this.page.pageable.pageNumber = 0;
    this.state.updateItemsPerPage(number);
    this.ngOnInit();
  }

  openModel(elementId: string) {
    this.utility.updateSelectedModel(elementId);
    this.getDocument(elementId);
  }

  getDocument(elementId: string) {
    this.adminApi.getMetaDetails(elementId)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe(result => {
          const dialogRef = this.dialog.open(MetaDetailComponent, {
            data: {
              message: result,
              internalId: elementId
            }
          });
          dialogRef.afterClosed().subscribe(() => {
            this.utility.scrollToSelectedModal(elementId);
          });
        }, error => {
          const dialogRef = this.dialog.open(MetaDetailComponent, {
            data: {
              errorMsg: error,
              internalId: elementId
            }
          });
        })
  }

  toon(internalId) {
    this.adminApi.getMetaDetailsFile(internalId)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe(file => {
          if(file.type == 'application/xml') {
            window.open(URL.createObjectURL(file), '_blank');
          } else {
            this.openJson(file);
          }

        }, error => {
          const dialogRef = this.dialog.open(MetaDetailComponent, {
            data: {
              errorMsg: error,
              internalId: internalId
            }
          });
        });
  }

  openJson(file){
    var myReader: FileReader = new FileReader();
    myReader.onloadend = function (e) {
      var myjson = JSON.stringify(myReader.result, null, 4);
      var x = window.open();
      x.document.open();
      x.document.write('<html><body><pre>' + myReader.result.toString().replace(/[{]/g, "{<br/>").replace(/[}]/g, "<br/>}").replace(/[,]/g, ",<br/>")+'</pre></body></html>')
      x.document.close();
    }

    myReader.readAsText(file);
  }

  public getErrorMessage(error) : string {
    let exceptionClass = '';
    let exceptionMessage = '';
    if(error) {
      if(error.exceptionClass) {
        exceptionClass  =   error.exceptionClass.substring(error.exceptionClass.lastIndexOf('.') + 1);
      }
      if(error.exceptionMessage) {
        exceptionMessage = error.exceptionMessage.length > this.exceptionLength? error.exceptionMessage.slice(0, this.exceptionLength) +'....':error.exceptionMessage;
      }
      return exceptionClass.concat(' ', exceptionMessage);
    }
    else return  '<' +'leeg' + '>';
  }

  goToErrorDetails(processingError: ProcessingError) {
    this.router.navigate(['./home/errors/document', processingError.id]);
  }

  getSeverityColor(severity: string): string {
    switch (severity) {
      case 'WARNING': {
        return 'background-color: #e17000; color: white'
      }
      case 'EXCEPTION': {
        return 'background-color: #d52b1e; color: white'
      }
      default: {
        return 'none'
      }
    }
  }

  getSeverityFontColor(severity){
    switch (severity) {
      case 'WARNING': {
        return 'color: white;'
      }
      case 'EXCEPTION': {
        return 'color: white;'
      }
      default: {
        return 'none'
      }
    }
  }
}