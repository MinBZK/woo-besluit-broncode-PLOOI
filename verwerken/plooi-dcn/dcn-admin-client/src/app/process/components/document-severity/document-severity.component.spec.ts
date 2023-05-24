import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentSeverityComponent } from './document-severity.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";

describe('DocumentSeverityComponent', () => {
  let component: DocumentSeverityComponent;
  let fixture: ComponentFixture<DocumentSeverityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentSeverityComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentSeverityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
