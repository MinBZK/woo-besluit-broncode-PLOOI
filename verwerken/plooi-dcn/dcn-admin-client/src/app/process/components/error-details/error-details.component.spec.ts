import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorDetailsComponent } from './error-details.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {ProcessingError} from "../../models/processing-error";
import {AdminApiService} from "../../services/admin-api.service";
import {of} from "rxjs";

describe('ErrorDetailsComponent', () => {
  let component: ErrorDetailsComponent;
  let fixture: ComponentFixture<ErrorDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorDetailsComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorDetailsComponent);
    component = fixture.componentInstance;

  });

  it('Details Table should create with correct labels and data when process error is not empty', () => {
    const restService = TestBed.inject(AdminApiService);

    let processingError  = new ProcessingError();
    processingError.id = 1234;
    processingError.timeCreated = new Date('2022-01-17T03:24:00');
    processingError.fromRoute = 'RONL';
    processingError.statusText = 'status text'
    processingError.statusCode = 100;
    processingError.exceptionClass = 'NullPointer';
    processingError.exceptionMessage = 'exception message';
    processingError.exceptionStacktrace = 'exception stacktrace';
    processingError.messageBody = 'message body';

    spyOn(restService, "getErrorDetails").and.returnValue(of(processingError));
    component.processingError = processingError;
    fixture.detectChanges();
    const detailsTable = document.querySelectorAll('div.container')[0].querySelector('table');
    expect(detailsTable).toBeTruthy();
    const rows = detailsTable.querySelectorAll('tr');
    expect(rows).toBeTruthy();
    expect(rows.length).toBe(8);
    console.log(component.processingError);


    expect(rows[0].querySelector('th').textContent).toBe('Tijdstip creatie');
    expect(rows[0].querySelector('td').textContent).toBe('Jan 17, 2022, 3:24:00 AM');

    expect(rows[1].querySelector('th').textContent).toBe('From Route');
    expect(rows[1].querySelector('td').textContent).toBe(component.processingError.fromRoute);

    expect(rows[2].querySelector('th').textContent).toBe('Status text');
    expect(rows[2].querySelector('td').textContent).toBe(component.processingError.statusText);

    expect(rows[3].querySelector('th').textContent).toBe('Status code');
    expect(rows[3].querySelector('td').textContent).toBe(component.processingError.statusCode.toString());

    expect(rows[4].querySelector('th').textContent).toBe('Exception Class');
    expect(rows[4].querySelector('td').textContent).toBe(component.processingError.exceptionClass);

    expect(rows[5].querySelector('th').textContent).toBe('Exception Message');
    expect(rows[5].querySelector('td').textContent).toBe(component.processingError.exceptionMessage);

    expect(rows[6].querySelector('th').textContent).toBe('Exception Stacktrace');
    expect(rows[6].querySelector('td').textContent).toBe(component.processingError.exceptionStacktrace);

    expect(rows[7].querySelector('th').textContent).toBe('Message Body');
    expect(rows[7].querySelector('td').textContent).toBe(component.processingError.messageBody);
  });
});