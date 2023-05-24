import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SearchFilterModalComponent} from './search-filter-modal.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

describe('OrganisationsModalComponent', () => {
    let component: SearchFilterModalComponent;
    let fixture: ComponentFixture<SearchFilterModalComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SearchFilterModalComponent],
            providers: [
                {
                    provide: MatDialogRef,
                    useValue: {},
                }, {provide: MAT_DIALOG_DATA, useValue: {}},]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(SearchFilterModalComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
