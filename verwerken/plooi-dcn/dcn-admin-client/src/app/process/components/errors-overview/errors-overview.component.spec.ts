import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorsOverviewComponent } from './errors-overview.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";

describe('ErrorsOverviewComponent', () => {
  let component: ErrorsOverviewComponent;
  let fixture: ComponentFixture<ErrorsOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorsOverviewComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorsOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
