import {ComponentFixture, TestBed} from '@angular/core/testing';
import { NoResultComponent } from './no-result.component';
import {Page} from "../../../core/models/page";


describe('NoResultComponent', () => {
  let component: NoResultComponent;
  let fixture: ComponentFixture<NoResultComponent>;


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NoResultComponent ]

    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoResultComponent);
    component = fixture.componentInstance;
  });

  it('No results component shown when page elements empty', () => {
    let page = new Page<any>();
    page.totalElements = 0;
    component.page = page;
    fixture.detectChanges()
    expect(component).toBeTruthy();
    expect(component.page.totalElements).toBe(0);
    const noResult = document.querySelector('p');
    expect(noResult.textContent).toBe("Geen resultaten gevonden.");
  });
});
