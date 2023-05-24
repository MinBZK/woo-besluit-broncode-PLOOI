import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DocumentSolrComponent } from './document-solr.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {AdminApiService} from "../../services/admin-api.service";
import {of} from "rxjs";

describe('DocumentSolrComponent', () => {
  let component: DocumentSolrComponent;
  let fixture: ComponentFixture<DocumentSolrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentSolrComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule,MatDialogModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentSolrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Solr document table details should not be shown when document object is empty', ()=> {
    const restService = TestBed.inject(AdminApiService);
    let solrDocument  = null
    spyOn(restService, "getSolrDocument").and.returnValue(of(solrDocument));
    component.ngOnInit();
    expect(component.solrDocument).toBeFalsy();
    const detailsTable = document.querySelectorAll('div.container');
    expect(detailsTable.length).toBe(0);
  });

  it('Solr document table details should be shown when document object is not empty', ()=> {
    const restService = TestBed.inject(AdminApiService);
    component.internalId = 12345;
    let results = [{
      "identifier": "123456",
      "title":"title",
      "type" : "type",
      "topthema" : ["thema1", "thema2"],
      "creator" : ["MBZ"],
      "verantwoordelijke" : "MBZ"
    }];
    spyOn(restService, "getSolrDocument").and.returnValue(of(results));
    spyOn(restService, "getPortalUrl").and.returnValue(of("https://open.overheid.nl"));

    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.solrDocument).toBeTruthy();
    expect(component.portalUrl).toBeTruthy();
    expect(component.portalUrl).toEqual('https://open.overheid.nl/Details/123456/1');

    const detailsTable = document.querySelectorAll('div.container')[0].querySelector('table');
    expect(detailsTable).toBeTruthy();
    const rows = detailsTable.querySelectorAll('tr');
    expect(rows).toBeTruthy();
    expect(rows.length).toBe(9);

    expect(rows[0].querySelectorAll('th')[0].textContent).toBe('Veld');
    expect(rows[0].querySelectorAll('th')[1].textContent).toBe('Waarde');

    expect(rows[1].querySelectorAll('td')[0].textContent).toBe('documenttype');
    expect(rows[1].querySelectorAll('td')[1].textContent).toBe('type');

    expect(rows[2].querySelectorAll('td')[0].textContent).toBe('topthema');
    expect(rows[2].querySelectorAll('td')[1].textContent).toBe('thema1 - thema2');

    expect(rows[3].querySelectorAll('td')[0].textContent).toBe('document titel');
    expect(rows[3].querySelectorAll('td')[1].textContent).toBe('title');

    expect(rows[4].querySelectorAll('td')[0].textContent).toBe('creator');
    expect(rows[4].querySelectorAll('td')[1].textContent).toBe('Mbz');

    expect(rows[5].querySelectorAll('td')[0].textContent).toBe('available_start');

    expect(rows[6].querySelectorAll('td')[0].textContent).toBe('issued');

    expect(rows[7].querySelectorAll('td')[0].textContent).toBe('verantwoordelijke');
    expect(rows[7].querySelectorAll('td')[1].textContent).toBe('MBZ');

    expect(rows[8].querySelectorAll('td')[0].textContent).toBe('timestamp');
  });
});