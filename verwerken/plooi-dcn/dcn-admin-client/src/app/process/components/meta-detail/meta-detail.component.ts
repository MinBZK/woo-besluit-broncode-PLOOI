import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActionName, TriggerAction} from "../../models/trigger-action";

@Component({
    selector: 'app-meta-detail',
    templateUrl: './meta-detail.component.html',
    styleUrls: ['./meta-detail.component.css']
})
export class MetaDetailComponent implements OnInit {
    readonly maxNumberDocuments =  1000;
    readonly intrekkenWarning = 'Het is niet mogelijk om in een actie meer dan ' + this.maxNumberDocuments + ' documenten in te trekken.';
    errorMsg: string;
    numberOfDocuments: number;
    triggerAction: TriggerAction;
    form: FormGroup;
    approveBulkAction: boolean;
    showOpnieuwVerwerkingsMessage: boolean;
    showTrekInMessage: boolean;
    @Output() passEntry: EventEmitter<any> = new EventEmitter();
    constructor(public dialogRef: MatDialogRef<MetaDetailComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private fb: FormBuilder){
        this.createForm();
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    updateApproveAction(): void {
        this.approveBulkAction = true;
    }

    ngOnInit() {
        this.viewWarningsAndForm();
    }

    get f(){
        return this.form.controls;
    }

    submit(){
        this.passEntry.emit(this.form.get('reason').value);
    }

    createForm() {
        this.form = this.fb.group({
            reason: ['', Validators.required ]
        });
    }

    viewWarningsAndForm(){
        if(this.numberOfDocuments >= this.maxNumberDocuments) {
            this.triggerAction.actionName == ActionName.REPROCESS? this.showOpnieuwVerwerkingsMessage = !this.showOpnieuwVerwerkingsMessage : this.showTrekInMessage = !this.showTrekInMessage;

        } else {
            this.approveBulkAction = !this.approveBulkAction;
        }
    }

}
