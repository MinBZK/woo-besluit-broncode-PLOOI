import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentDetailsComponent } from './document-details.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {Page} from "../../../core/models/page";
import documentEventPage from "../../services/data/documentevent-internalId.json";
import {of} from "rxjs";
import {AdminApiService} from "../../services/admin-api.service";

describe('DocumentDetailsComponent', () => {
  let component: DocumentDetailsComponent;
  let fixture: ComponentFixture<DocumentDetailsComponent>;
  let documentEvent: Page<any> = JSON.parse(JSON.stringify(documentEventPage));
  let restServic;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentDetailsComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    restServic = TestBed.inject(AdminApiService);
    spyOn(restServic, "getDocumentEvents").and.returnValue(of(documentEvent));

  });

  it('Document details trail Table header and titles should appear always', () => {
    component.page = new Page();
    component.page = documentEvent;
    component.ngOnInit();

    fixture.detectChanges();
    const containers = document.getElementsByClassName('container');
    expect(containers.length).toBe(4);

    fixture.detectChanges();
    const table = containers[3].getElementsByTagName('table');
    expect(table).toBeTruthy();
    const tableHead = table[0].getElementsByTagName('thead');
    expect(tableHead).toBeTruthy();
    const headerRowCells = tableHead[0].getElementsByTagName('th');
    expect(headerRowCells).toBeTruthy();
    expect(headerRowCells.length).toBe(4);
    expect(headerRowCells[0].textContent).toEqual('verwerkingsfase');
    expect(headerRowCells[1].textContent).toEqual('tijdstip');
    expect(headerRowCells[2].textContent).toEqual('execution_id');
    expect(headerRowCells[3].textContent).toEqual('severity');

  });

  it('Document details should show database, solr, action and document events tables should be filled with correct values', () => {
    component.page = new Page();
    component.page = documentEvent;
    component.ngOnInit();

    fixture.detectChanges();
    const containers = document.getElementsByClassName('container');
    expect(containers.length).toBe(4);

    const databaseComponent =   containers[0].querySelector('dcn-document-data-base');
    expect(databaseComponent).toBeTruthy();

    const solrComponent =   containers[0].querySelector('dcn-document-solr');
    expect(solrComponent).toBeTruthy();

    const actionComponent =   containers[0].querySelector('dcn-document-actions');
    expect(actionComponent).toBeTruthy();

    const eventContainer =   document.getElementById('events');
    expect(eventContainer).toBeTruthy();
    expect(eventContainer.getElementsByTagName('h4')[0]).toBeTruthy();

    const eventTable =   eventContainer.getElementsByClassName('table');
    expect(eventTable.length).toBe(1);

    const tableHeader  = eventTable[0].getElementsByTagName('thead');
    expect(tableHeader.length).toBe(1);

    const headerRow  = tableHeader[0].getElementsByTagName('tr');
    expect(headerRow.length).toBe(1);
    const headerRowCells = headerRow[0].querySelectorAll('th');

    expect(headerRowCells[0].textContent).toEqual('verwerkingsfase');

    expect(headerRowCells[1].textContent).toEqual('tijdstip');
    expect(headerRowCells[2].textContent).toEqual('execution_id');
    expect(headerRowCells[3].textContent).toEqual('severity');

    const tableBody = eventTable[0].getElementsByTagName('tbody');
    expect(tableBody.length).toBe(1);

    const bodyRow  = tableBody[0].getElementsByTagName('tr');

     const firstRowCells = bodyRow[0].querySelectorAll('td');
     expect(firstRowCells.length).toBe(4);
     expect(firstRowCells[0].textContent).toEqual("INGRESS");
     expect(firstRowCells[1].textContent).toEqual("");
    expect(firstRowCells[3].textContent).toEqual("OK");
  });
});
