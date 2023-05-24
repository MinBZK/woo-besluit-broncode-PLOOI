import 'zone.js/dist/zone-testing';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StatisticsComponent} from './statistics.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {AdminApiService} from "../../services/admin-api.service";
import {of} from "rxjs";
import {DocumentStatistics} from "../../models/documentStatistics";
import staticsJson from "../../services/data/example.json";

describe('StatisticsComponent', () => {
    let component: StatisticsComponent;
    let fixture: ComponentFixture<StatisticsComponent>;
    let statics: DocumentStatistics [] = JSON.parse(JSON.stringify(staticsJson));
    let restService;
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [RouterTestingModule, HttpClientTestingModule, MatDialogModule],
            declarations: [StatisticsComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(StatisticsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        restService = TestBed.inject(AdminApiService);
    });

    it('Statistics Table header and titles should appear always', () => {
        const staticsTableHead = document.querySelector('thead');
        const staticHeaderRow = staticsTableHead.querySelectorAll('th');
        expect(staticHeaderRow.length).toBe(5);
        expect(staticsTableHead.querySelector('a').textContent).toEqual('Bron');
        expect(staticHeaderRow[1].textContent).toEqual('Verwerkingsfouten');
        expect(staticHeaderRow[2].textContent).toEqual('Mappingwaarschuwingen');
        expect(staticHeaderRow[3].textContent).toEqual('Mappingfouten');
        expect(staticHeaderRow[4].textContent).toEqual('Totaal');
    });

    it('Totals should not appear when data is empty', () => {
        spyOn(restService, "getDocumentStatistics").and.returnValue(of([]));
        component.getData();
        expect(component.errors.length).toBe(0);
        expect(component.totalErrors).toBe(0);
        expect(component.totalWarnings).toBe(0);
        expect(component.totalMappings).toBe(0);

        const staticsTableFootRow = document.querySelector('tfoot').querySelector('tr');
        expect(staticsTableFootRow).toBeFalsy();

    });

    it('Statistics rows appears based on data', () => {
        spyOn(restService, "getDocumentStatistics").and.returnValue(of(statics));
        component.getData();

        expect(component.errors.length).toBe(2);
        expect(component.totalErrors).toBe(4);
        expect(component.totalWarnings).toBe(10);
        expect(component.totalMappings).toBe(2);

        fixture.detectChanges();

        const staticsTableBody = document.querySelector('tbody');
        expect(staticsTableBody).toBeTruthy();

        const staticTableRows = staticsTableBody.querySelectorAll('tr');
        expect(staticTableRows.length).toBe(2);

        const staticTableFirstRowCells = staticTableRows[0].querySelectorAll('td');

        expect(staticTableFirstRowCells[0].textContent).toBe('oep');
        expect(staticTableFirstRowCells[1].textContent).toBe('1');
        expect(staticTableFirstRowCells[2].textContent).toBe('3');
        expect(staticTableFirstRowCells[3].textContent).toBe('2');

        const staticTableSecondRowCells = staticTableRows[1].querySelectorAll('td');

        expect(staticTableSecondRowCells[0].textContent).toBe('ronl');
        expect(staticTableSecondRowCells[1].textContent).toBe('3');
        expect(staticTableSecondRowCells[2].textContent).toBe('7');
        expect(staticTableSecondRowCells[3].textContent).toBe('0');

    });

    it('Totals should be shown and calculated based on data', () => {

        spyOn(restService, "getDocumentStatistics").and.returnValue(of(statics));

        component.getData();

        expect(component.errors.length).toBe(2);
        expect(component.totalErrors).toBe(4);
        expect(component.totalWarnings).toBe(10);
        expect(component.totalMappings).toBe(2);

        fixture.detectChanges();

        const staticsTableFoot = document.querySelector('tfoot');
        expect(staticsTableFoot).toBeTruthy();

        const staticsTableFootRowCells = document.querySelector('tfoot').querySelectorAll('td');
        expect(staticsTableFootRowCells.length).toBe(5);
        expect(staticsTableFootRowCells[0].textContent).toBe('Totaal');
        expect(staticsTableFootRowCells[1].textContent).toBe('4');
        expect(staticsTableFootRowCells[2].textContent).toBe('10');
        expect(staticsTableFootRowCells[3].textContent).toBe('2');
        expect(staticsTableFootRowCells[4].textContent).toBe('16');

    });

});
