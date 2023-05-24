import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchFilterComponent } from './search-filter.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {ReactiveFormsModule} from "@angular/forms";

describe('SearchFilterComponent', () => {
  let component: SearchFilterComponent;
  let fixture: ComponentFixture<SearchFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchFilterComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule,ReactiveFormsModule ]

    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Search for plooi id without underscore and one', () => {
    expect(component.searchForm.get('searchType').value).toBe('plooi');
    component.searchForm.setValue({
      plooiId: '123451230123',
      searchType : 'plooi'
    });

    fixture.detectChanges();
  });

  it('Search for plooi id with underscore', () => {
    expect(component.searchForm.get('searchType').value).toBe('plooi');
    component.searchForm.setValue({
      plooiId: '123451230123',
      searchType : 'plooi'
    });

    fixture.detectChanges();
  });
});