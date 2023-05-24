import {ComponentFixture, TestBed} from '@angular/core/testing';
import 'zone.js/dist/zone-testing';

import {SolrDocumentListComponent} from './solr-document-list.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import solrdocuments from "../../services/data/solr-documents.json";
import {AdminApiService} from "../../services/admin-api.service";
import {Page} from "../../../core/models/page";
import {TokenStorageService} from "../../../core/services/token-storage.service";


describe('SolrDocumentListComponent', () => {
    let component: SolrDocumentListComponent;
    let fixture: ComponentFixture<SolrDocumentListComponent>;
    let solrDocuments: Page<any[]> = JSON.parse(JSON.stringify(solrdocuments));
    let restService;
    let tokenService;


    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SolrDocumentListComponent],
            imports: [RouterTestingModule, HttpClientTestingModule, MatDialogModule]
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(SolrDocumentListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        restService = TestBed.inject(AdminApiService);
        tokenService = TestBed.inject(TokenStorageService);
    });

    it('Solr documents Table header and titles should appear always', () => {
        const solrListTable = document.querySelector('table');
        const solrListHeader = solrListTable.querySelector('thead');
        expect(solrListHeader).toBeTruthy();
        const solrListHeaderTitleRow = solrListHeader.querySelector('tr');
        expect(solrListHeaderTitleRow).toBeTruthy();
        const solrListHeaderTitleRowCells = solrListHeaderTitleRow.querySelectorAll('th');
        expect(solrListHeaderTitleRowCells.length).toBe(5);

        expect(solrListHeaderTitleRowCells[0].textContent).toEqual('Tijdstip publicatie');
        expect(solrListHeaderTitleRowCells[1].textContent).toEqual('DCN Identifier');
        expect(solrListHeaderTitleRowCells[2].textContent).toEqual('Organisatie');
        expect(solrListHeaderTitleRowCells[3].textContent).toEqual('Documentsoort');
        expect(solrListHeaderTitleRowCells[4].textContent).toEqual('Titel');
    });

    it('Table rows should appear if table is not empty', () => {
        component.page.totalElements = 2;
        fixture.detectChanges();
        const solrListTable = document.querySelector('table');
        const solrListBody = solrListTable.querySelector('tbody');
        expect(solrListBody).toBeTruthy();
    });



    it('Bulk action should not appear if table is empty', () => {
        const solrListTable = document.querySelector('table');
        const solrListBody = solrListTable.querySelector('tbody');
        expect(solrListBody).toBeFalsy();
        const bulkActionTable = document.getElementById('bulk-action');
        expect(bulkActionTable).toBeFalsy();

    });


    it('Bulk action should appear if table is not empty', () => {

        component.page = new Page();
        component.page.content = solrdocuments;
        fixture.detectChanges();

        const solrListTable = document.querySelector('table');
        const solrListBody = solrListTable.querySelector('tbody');
        expect(solrListBody).toBeFalsy();
        const bulkActionTable = document.getElementById('bulk-action');
        expect(bulkActionTable).toBeTruthy();
    });

});
