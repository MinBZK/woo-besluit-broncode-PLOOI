/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from "rxjs";
import { MatDialogModule } from "@angular/material/dialog";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { ExecutionStatisticsComponent } from './execution-statistics.component';
import { AdminApiService } from '../../services/admin-api.service';
import { Page } from 'src/app/core/models/page';
import executionJson from "../../services/data/execution_statistics.json";
import {Proces} from "../../models/proces";

describe('ExecutionStatisticsComponent', () => {
  let component: ExecutionStatisticsComponent;
  let fixture: ComponentFixture<ExecutionStatisticsComponent>;
  let executions: Page<Proces> = JSON.parse(JSON.stringify(executionJson));
  let restService;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ RouterTestingModule,
                 HttpClientTestingModule,
                 MatDialogModule ],
      declarations: [ ExecutionStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExecutionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    restService = TestBed.inject(AdminApiService);
  });

  it('Statistics Table header and titles should appear always', () => {
    const staticsTableHeads = document.querySelector('table').querySelectorAll('thead');

    const staticHeaderRowHeaderTitleRows = staticsTableHeads[0].querySelectorAll('tr');
    expect(staticHeaderRowHeaderTitleRows.length).toBe(2);

    expect(staticHeaderRowHeaderTitleRows[0].querySelector('label').textContent).toBe('Toon alleen verwerkingen met fouten');
    expect(staticHeaderRowHeaderTitleRows[1].textContent).toBe('Klik op een regel voor de verwerkingsdetails (execution details)');


    const staticHeaderRowHeaders = staticsTableHeads[1].querySelectorAll('th');
    expect(staticHeaderRowHeaders.length).toBe(6);
    expect(staticHeaderRowHeaders[0].textContent).toEqual('Bron');
    expect(staticHeaderRowHeaders[1].textContent).toEqual('Datum/Tijd');
    expect(staticHeaderRowHeaders[2].textContent).toEqual('Trigger type');
    expect(staticHeaderRowHeaders[3].textContent).toEqual('Documenten');
    expect(staticHeaderRowHeaders[4].textContent).toEqual('Execution errors');
    expect(staticHeaderRowHeaders[5].textContent).toEqual('Event errors');
  });

  it('Rows should not appear when data is empty', () => {

    let page = new Page();
    page.size = 0;
    page.totalElements = 0;

    spyOn(restService, "getExecutionStatistics").and.returnValue(of(page));
    component.getData();
    const tableRows = document.querySelector('tbody').querySelectorAll('tr');
    expect(tableRows.length).toBe(0);
  });


  it('Execution Statistics tables should be filled with correct values', () => {
    spyOn(restService, "getExecutionStatistics").and.returnValue(of(executions));
    component.getData();
    fixture.detectChanges();

    const tableRows = document.querySelector('tbody').querySelectorAll('tr');

    expect(tableRows.length).toBe(4);
    const firstRow =   tableRows[0].querySelectorAll('td');
    expect(firstRow.length).toBe(5);
    expect(firstRow[0].textContent).toEqual('ronl');
    expect(firstRow[1].textContent).toEqual('');
    expect(firstRow[2].textContent).toEqual('INGRESS');
    expect(firstRow[3].textContent).toEqual('1');
    expect(firstRow[4].textContent).toEqual('0');

    const secondRow =   tableRows[1].querySelectorAll('td');
    expect(secondRow.length).toBe(5);
    expect(secondRow[0].textContent).toEqual('source2');
    expect(secondRow[1].textContent).toEqual('');
    expect(secondRow[2].textContent).toEqual('INGRESS');
    expect(secondRow[3].textContent).toEqual('1');
    expect(secondRow[4].textContent).toEqual('0');

    const thirdRow =   tableRows[2].querySelectorAll('td');
    expect(thirdRow.length).toBe(5);
    expect(thirdRow[0].textContent).toEqual('source3');
    expect(thirdRow[1].textContent).toEqual('');
    expect(thirdRow[2].textContent).toEqual('INGRESS');
    expect(thirdRow[3].textContent).toEqual('1');
    expect(thirdRow[4].textContent).toEqual('0');


    const fourthRow =   tableRows[3].querySelectorAll('td');
    expect(fourthRow.length).toBe(5);
    expect(fourthRow[0].textContent).toEqual('source4');
    expect(fourthRow[1].textContent).toEqual('');
    expect(fourthRow[2].textContent).toEqual('INGRESS');
    expect(fourthRow[3].textContent).toEqual('1');
    expect(fourthRow[4].textContent).toEqual('0');

  });

  it('Execution Statistics tables should be filled with correct values when checkbox is clicked', () => {
    const staticsTableHeads = document.querySelector('table').querySelectorAll('thead');
    const checkbox = staticsTableHeads[0].querySelectorAll('input')[0];
    expect(checkbox.checked).toBeFalsy()
    checkbox.click();
    spyOn(restService, "getExecutionStatistics").and.returnValue(of(executions));
    component.getData();
    fixture.detectChanges();
    expect(checkbox.checked).toBeTruthy()
    const tableRows = document.querySelector('tbody').querySelectorAll('tr');
    expect(tableRows.length).toBe(4);
    const firstRow =   tableRows[0].querySelectorAll('td');
    expect(firstRow[4].textContent).toEqual('0');

    const secondRow =   tableRows[1].querySelectorAll('td');
    expect(secondRow[4].textContent).toEqual('0');
  });

});
