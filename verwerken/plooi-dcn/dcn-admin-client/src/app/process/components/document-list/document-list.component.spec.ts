import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentListComponent } from './document-list.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {ActivatedRoute, convertToParamMap} from "@angular/router";
import {of} from "rxjs";

describe('DocumentListComponent', () => {
  let component: DocumentListComponent;
  let fixture: ComponentFixture<DocumentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentListComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ],
      providers : [
        {
          provide: ActivatedRoute, useValue: {
            parent : {params: of({ source: 'test' , type : 'type'})},
            snapshot : {
              paramMap: convertToParamMap({
                target: 'target',
                lable: 'label'
              })
            },

          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create test..', () => {
    expect(component).toBeTruthy();
  });
});
