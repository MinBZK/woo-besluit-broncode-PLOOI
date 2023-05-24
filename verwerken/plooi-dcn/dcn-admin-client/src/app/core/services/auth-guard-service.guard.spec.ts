import {TestBed} from '@angular/core/testing';

import {AuthGuardService} from './auth-guard.service';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('AuthGuardServiceGuard', () => {
    let guard: AuthGuardService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule, HttpClientTestingModule]
        });
        guard = TestBed.inject(AuthGuardService);
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });
});
