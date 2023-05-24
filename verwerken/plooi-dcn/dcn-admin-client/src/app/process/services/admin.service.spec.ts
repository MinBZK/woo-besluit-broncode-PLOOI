import {TestBed} from '@angular/core/testing';
import staticsJson from './data/example.json';
import {AdminApiService} from './admin-api.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {DocumentStatistics} from "../models/documentStatistics";

describe('AdminApiService', () => {
    let service: AdminApiService;
    let httpController: HttpTestingController;
    let url = 'api/admin';
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, RouterTestingModule]
        });
        service = TestBed.inject(AdminApiService);
        httpController = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
        let statics: DocumentStatistics [] = JSON.parse(JSON.stringify(staticsJson));
        service.getDocumentStatistics(false).subscribe((res) => {
            expect(res).toEqual(statics);
        });
        const req = httpController.expectOne({
            method: 'GET',
            url: `registrationApi/statistieken?reverseSorted=false`
        });

        req.flush(statics);
    });
});
