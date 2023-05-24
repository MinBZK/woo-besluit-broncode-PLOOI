import {TestBed} from '@angular/core/testing';

import {CustomPaginationComponent} from './custom-pagination.component';

describe('CustomPaginationComponent', () => {
    let component: CustomPaginationComponent;
    component = new CustomPaginationComponent();
    component.totalPages = 1;
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [CustomPaginationComponent]
        })
            .compileComponents();
    });

    it('should create', () => {
        expect(component.totalPages).toBe(1);
    });
});
