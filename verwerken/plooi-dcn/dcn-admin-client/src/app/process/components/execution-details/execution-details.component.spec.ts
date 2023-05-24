import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from "@angular/material/dialog";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";

import { ExecutionDetailsComponent } from './execution-details.component';
import { AdminApiService } from "../../services/admin-api.service";
import { DocumentEvent } from "../../models/document-event";
import { ProcessingError } from "../../models/processing-error";
import { Page } from "../../../core/models/page";
import {DatePipe} from '@angular/common';
import {Proces} from "../../models/proces";
import {ProcesCounts} from "../../models/proces-counts";

describe('ExecutionDetailsComponent', () => {
  let component: ExecutionDetailsComponent;
  let fixture: ComponentFixture<ExecutionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExecutionDetailsComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule, MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExecutionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Execution Details tables should be filled with correct values', () => {
    const restService = TestBed.inject(AdminApiService);

    let execution: Proces = new Proces();
    execution.timeCreated = new Date('2023-01-24T06:16:00');
    execution.id = 'execution-id-1';
    execution.sourceLabel = 'oep';
    execution.triggerType = 'INGRESS';
    execution.excepties = [];

    let executionDocumentSeverityStats : ProcesCounts = new ProcesCounts();
    executionDocumentSeverityStats.documentCount = 1;
    executionDocumentSeverityStats.totalCount = 10;
    executionDocumentSeverityStats.okCount = 1;
    executionDocumentSeverityStats.okCount = 1;
    executionDocumentSeverityStats.infoCount = 1;
    executionDocumentSeverityStats.warningCount = 1;
    executionDocumentSeverityStats.errorCount = 1;
    executionDocumentSeverityStats.exceptionCount = 1;
    executionDocumentSeverityStats.procesExceptionCount = 1;

    execution.procesCounts = executionDocumentSeverityStats;

    let documentEvent1 = new DocumentEvent();
    let documentEvent2 = new DocumentEvent();
    let processingError1 = new ProcessingError();
    let processingError2 = new ProcessingError();
    let pipe = new DatePipe('en');


    // DocumentEvents
    documentEvent1.id = 1;
    documentEvent1.executionId = 'execution-id-1';
    documentEvent1.sourceName = 'oep';
    documentEvent1.externalId = 'test-external-id';
    documentEvent1.internalId = 'test-internal-id';
    documentEvent1.stage = 'INGRESS';
    documentEvent1.severity = 'WARNING';
    documentEvent1.timeCreated = new Date('2023-01-24T06:16:00');

    documentEvent2.id = 2;
    documentEvent2.executionId = 'execution-id-1';
    documentEvent2.sourceName = 'oep';
    documentEvent2.externalId = 'test-external-id';
    documentEvent2.internalId = 'test-internal-id';
    documentEvent2.stage = 'INGRESS';
    documentEvent2.severity = 'INFO';
    documentEvent2.timeCreated = new Date('2023-01-24T06:16:01');

    // wrap documentEvents in Page
    let documentEvents = [documentEvent1, documentEvent2];
    let documentEventPage = new Page<DocumentEvent>();
    documentEventPage.content = documentEvents;
    documentEventPage.totalElements = 2;
    documentEventPage.first = true;
    documentEventPage.totalPages = 1;

    spyOn(restService, "getExecutionSummary").and.returnValue(of(execution));
    spyOn(restService, "getExecutionDocumentEvents").and.returnValue(of(documentEventPage));
    component.executionSummary = execution;
    component.ngOnInit();
    fixture.detectChanges();

    // execution details table
    const executionDetailsTableTHead = document.querySelectorAll('div.container')[0].querySelectorAll('thead')[0];
    const executionDetailsTableTBody = document.querySelectorAll('div.container')[0].querySelectorAll('tbody')[0];
    expect(executionDetailsTableTHead).toBeTruthy();
    expect(executionDetailsTableTBody).toBeTruthy();
    const executionDetailsHeaders = executionDetailsTableTHead.querySelector('tr').querySelectorAll('th');
    const executionDetailsRowData = executionDetailsTableTBody.querySelector('tr').querySelectorAll('td');
    expect(executionDetailsRowData).toBeTruthy();
    expect(executionDetailsHeaders.length).toBe(6);
    expect(executionDetailsRowData.length).toBe(6);

    // headers & rows
    expect(executionDetailsHeaders[0].textContent).toBe('Tijdstip');
    expect(executionDetailsRowData[0].textContent).toBe(pipe.transform(execution.timeCreated.toString(), 'medium'));

    expect(executionDetailsHeaders[1].textContent).toBe('Execution ID');
    expect(executionDetailsRowData[1].textContent).toBe(execution.id.toString());

    expect(executionDetailsHeaders[2].textContent).toBe('Bron');
    expect(executionDetailsRowData[2].textContent).toBe(execution.sourceLabel.toString());

    expect(executionDetailsHeaders[3].textContent).toBe('Trigger type');
    expect(executionDetailsRowData[3].textContent).toBe(execution.triggerType.toString());

    expect(executionDetailsHeaders[4].textContent).toBe('Documenten');
    expect(executionDetailsRowData[4].textContent).toBe(execution.procesCounts.documentCount.toString());

    expect(executionDetailsHeaders[5].textContent).toBe('Processing errors');
    expect(executionDetailsRowData[5].textContent).toBe(execution?.excepties?.length.toString());

    // // document events table
    const documentEventsTableTHead = document.querySelectorAll('div.container')[0].querySelectorAll('thead')[1];
    const documentEventsTableTBody = document.querySelectorAll('div.container')[0].querySelectorAll('tbody')[1];
    expect(documentEventsTableTHead).toBeTruthy();
    expect(documentEventsTableTBody).toBeTruthy();

    const documentEventsHeaders = documentEventsTableTHead.querySelector('tr').querySelectorAll('th');
    const documentEventsRows = documentEventsTableTBody.querySelectorAll('tr');

    const documentEventRow1 = documentEventsRows[0].querySelectorAll('td');

    expect(documentEventsHeaders).toBeTruthy();
    expect(documentEventsRows).toBeTruthy();
    expect(documentEventsHeaders.length).toBe(6);
    expect(documentEventsRows.length).toBe(1);
    expect(documentEventRow1.length).toBe(6);
    console.log(execution);

    // // headers & rows
    expect(documentEventsHeaders[0].textContent).toBe('Document events');
    expect(documentEventRow1[0].textContent).toBe('10');

    expect(documentEventsHeaders[1].textContent).toBe('EXCEPTION');
    expect(documentEventRow1[1].textContent).toBe('1');

    expect(documentEventsHeaders[2].textContent).toBe('ERROR');
    expect(documentEventRow1[2].textContent).toBe('1');

    expect(documentEventsHeaders[3].textContent).toBe('WARNING');
    expect(documentEventRow1[3].textContent).toBe('1');

    expect(documentEventsHeaders[4].textContent).toBe('INFO');
    expect(documentEventRow1[4].textContent).toBe('1');

    expect(documentEventsHeaders[5].textContent).toBe('OK');
    expect(documentEventRow1[5].textContent).toBe('1');

  });
});
