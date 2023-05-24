import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublishStateComponent } from './publish-state.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import publishing from "../../services/data/publishing-state.json";
import {Page} from "../../../core/models/page";
import {PublishingState} from "../../models/publishing-state";
import {AdminApiService} from "../../services/admin-api.service";

describe('PublishStateComponent', () => {
  let component: PublishStateComponent;
  let fixture: ComponentFixture<PublishStateComponent>;
  let publishingStates: Page<PublishingState[]> = JSON.parse(JSON.stringify(publishing));
  let restService;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PublishStateComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublishStateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    restService = TestBed.inject(AdminApiService);
  });

  it('Publishing state Table header and titles should not appear when list is empty', () => {
    const publishingState = document.querySelector('table');
    expect(publishingState).toBeFalsy();

  });

  it('Show Publishing state data when it loading ', () => {

    component.page = new Page();
    component.page = publishingStates;
    fixture.detectChanges();

    const publishingState = document.querySelector('table');

    const publishingStatetHeader = publishingState.querySelectorAll('thead');
    expect(publishingStatetHeader).toBeTruthy();
    expect(publishingStatetHeader.length).toBe(2);
    const publishingStateTitleRow = publishingStatetHeader[0].querySelector('tr')
    expect(publishingStateTitleRow.querySelectorAll('td').length).toBe(1);
    expect(publishingStateTitleRow.querySelectorAll('td')[0].textContent).toBe("Documenten met indexatie-issues");
    const publishingStateTitleRow2 = publishingStatetHeader[1].querySelector('tr')
    expect(publishingStateTitleRow2).toBeTruthy();
    const publishingStateTitleRow2Headers = publishingStateTitleRow2.querySelectorAll('th')
    expect(publishingStateTitleRow2Headers).toBeTruthy();
    expect(publishingStateTitleRow2Headers.length).toBe(3);
    expect(publishingStateTitleRow2Headers[0].textContent).toBe("Dcn Id");
    expect(publishingStateTitleRow2Headers[1].textContent).toBe("Indexed");
    expect(publishingStateTitleRow2Headers[2].textContent).toBe("Datum/Tijd");


    const publishingBody = publishingState.querySelector('tbody');
    expect(publishingBody).toBeTruthy();
    const publishingRows = publishingBody.querySelectorAll('tr');
    expect(publishingRows).toBeTruthy();
    expect(publishingRows.length).toBe(2);
    const publishingRowsTds = publishingRows[0].querySelectorAll('td');
    expect(publishingRowsTds.length).toBe(3);
    expect(publishingRowsTds[0].textContent).toBe("1");
    expect(publishingRowsTds[1].textContent).toBe("TODO");
  });
});
