import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MetaDetailComponent} from './meta-detail.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Action, ActionName, Trigger, TriggerAction} from "../../models/trigger-action";

describe('MetaDetailComponent', () => {
  let component: MetaDetailComponent;
  let fixture: ComponentFixture<MetaDetailComponent>;
  let triggerAction : TriggerAction;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MetaDetailComponent ],
      imports : [ReactiveFormsModule
        , FormsModule],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {},
        },   { provide: MAT_DIALOG_DATA, useValue: {} },]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetaDetailComponent);
    component = fixture.componentInstance;
  });


  it('toon waarschuwing als interekken meer dan 1000', () => {
    triggerAction = new TriggerAction(Trigger.DELETION, Action.DELETION, ActionName.DELETION)
    component.triggerAction = triggerAction;
    component.numberOfDocuments =  1001;
    component.ngOnInit();
    component.viewWarningsAndForm();
    fixture.detectChanges();
    expect( component.approveBulkAction).toBeFalsy();
    expect( component.showTrekInMessage).toBeTruthy();
    expect(document.querySelectorAll('div')[11]).toBeTruthy();
    expect(document.querySelectorAll('div')[11].textContent).toBe(component.intrekkenWarning);
    });

  it('toon waarschuwing als opnieuw verwerking meer dan 1000 and show form after approve', () => {
    triggerAction = new TriggerAction(Trigger.REPROCESS, Action.REPROCESS, ActionName.REPROCESS)
    component.triggerAction = triggerAction;
    component.numberOfDocuments =  1001;
    component.ngOnInit();
    component.viewWarningsAndForm();
    fixture.detectChanges();
    expect( component.approveBulkAction).toBeFalsy();
    expect( component.showOpnieuwVerwerkingsMessage).toBeTruthy();
    expect(document.querySelectorAll('div')[11]).toBeTruthy();
    expect(document.querySelectorAll('div')[11].textContent).toBe('Je gaat de actie verwerk opnieuw uitvoeren op 1001 documenten. Dit kan even duren. Weet je het zeker?');
    component.updateApproveAction();
    fixture.detectChanges();
    expect(document.querySelector('form')).toBeTruthy();
  });

  it('Bulk Action form invalid when reason is empty', () => {
    expect(component.form.valid).toBeFalsy();
    let errors = {};
    let reason = component.form.controls['reason'];
    errors = reason.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('Bulk Action form valid when reason is not empty', () => {
    expect(component.form.valid).toBeFalsy();
    component.form.controls['reason'].setValue("voer set documenten");
    expect(component.form.valid).toBeTruthy();
    component.submit();
    expect(component.form.get('reason').value).toBe("voer set documenten");
  });

});
