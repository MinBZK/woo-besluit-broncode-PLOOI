import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolrJsonDetailsComponent } from './solr-json-details.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('SolrJsonDetailsComponent', () => {
  let component: SolrJsonDetailsComponent;
  let fixture: ComponentFixture<SolrJsonDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SolrJsonDetailsComponent ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {},
        },   { provide: MAT_DIALOG_DATA, useValue: {} },],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolrJsonDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
});
