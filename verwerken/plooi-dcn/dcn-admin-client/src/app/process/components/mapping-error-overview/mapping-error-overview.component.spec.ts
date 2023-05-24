import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingErrorOverviewComponent } from './mapping-error-overview.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";

describe('MappingErrorOverviewComponent', () => {
  let component: MappingErrorOverviewComponent;
  let fixture: ComponentFixture<MappingErrorOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MappingErrorOverviewComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MappingErrorOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
