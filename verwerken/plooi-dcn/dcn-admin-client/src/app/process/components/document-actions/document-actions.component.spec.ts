import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentActionsComponent } from './document-actions.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";

describe('DocumentActionsComponent', () => {
  let component: DocumentActionsComponent;
  let fixture: ComponentFixture<DocumentActionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentActionsComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
