import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessingErrorRowComponent } from './processing-error-row.component';
import {ProcessingErrorsListComponent} from "../processing-errors-list/processing-errors-list.component";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";

describe('ProcessingErrorRowComponent', () => {
  let component: ProcessingErrorRowComponent;
  let fixture: ComponentFixture<ProcessingErrorRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessingErrorRowComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
        .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessingErrorRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
