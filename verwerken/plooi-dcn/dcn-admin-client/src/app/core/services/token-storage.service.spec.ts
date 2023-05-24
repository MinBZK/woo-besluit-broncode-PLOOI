import {TestBed} from '@angular/core/testing';

import {TokenStorageService} from './token-storage.service';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('TokenStorageService', () => {
    let service: TokenStorageService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule, HttpClientTestingModule]
        });
        service = TestBed.inject(TokenStorageService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
