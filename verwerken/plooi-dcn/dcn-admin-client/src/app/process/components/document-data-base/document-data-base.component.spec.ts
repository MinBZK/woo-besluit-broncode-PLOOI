import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentDataBaseComponent } from './document-data-base.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {of} from "rxjs";
import {AdminApiService} from "../../services/admin-api.service";
import files from "../../services/data/document-files.json";
import {DocumentState} from "../../models/document-state";

describe('DocumentDataBaseComponent', () => {
  let component: DocumentDataBaseComponent;
  let fixture: ComponentFixture<DocumentDataBaseComponent>;
  let restService;
  let fileList: any = JSON.parse(JSON.stringify(files));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentDataBaseComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentDataBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    restService = TestBed.inject(AdminApiService);
  });

  it('Geen document gevonden is shown when document is null', () => {
    expect(component.document).toBeFalsy();
    const databaseTable = document.getElementsByTagName('container')[0];
    expect(databaseTable).toBeFalsy();
    const noResult = document.getElementsByTagName('p')[0];
    expect(noResult).toBeTruthy();
    expect(noResult.textContent).toEqual("Geen document gevonden");
  });

  it('Document gevonden is shown in table', () => {
    component.document =  new DocumentState();
    component.document.dcnId = "oep-12345";
    component.document.extId = "ext-1234"
    component.document.sourceLabel = "oep"
    component.document.lastSeverity ="OK"
    component.document.lastStage = "PROCESS"

    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.document).toBeTruthy();
    const databaseTable = document.getElementsByTagName('table')[0];
    expect(databaseTable).toBeTruthy();

    const tableHeader  = databaseTable.getElementsByTagName('thead');
    expect(tableHeader.length).toBe(1);

    const headerRow  = tableHeader[0].getElementsByTagName('tr');
    expect(headerRow.length).toBe(1);
    const headerRowCells = headerRow[0].querySelectorAll('th');

    expect(headerRowCells[0].textContent).toEqual('Veld');
    expect(headerRowCells[1].textContent).toEqual('Waarde');

    const tableBody  = databaseTable.getElementsByTagName('tbody');
    expect(tableBody.length).toBe(1);
    const bodyRow  = tableBody[0].getElementsByTagName('tr');
    expect(bodyRow.length).toBe(5);

    const firstRowCells = tableBody[0].querySelectorAll('td');
    expect(firstRowCells.length).toBe(10);
    expect(firstRowCells[0].textContent).toEqual("bron");
    expect(firstRowCells[1].textContent).toEqual("oep");
    expect(firstRowCells[2].textContent).toEqual("externe identifier");
    expect(firstRowCells[3].textContent).toEqual("ext-1234");
    expect(firstRowCells[4].textContent).toEqual("dcn identifier");
    expect(firstRowCells[5].textContent).toEqual("oep-12345");
    expect(firstRowCells[6].textContent).toEqual("laatste severity");
    expect(firstRowCells[7].textContent).toEqual("OK");
    expect(firstRowCells[8].textContent).toEqual("laatste stage");
    expect(firstRowCells[9].textContent).toEqual("PROCESS");
  });
});