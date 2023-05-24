import {Component, EventEmitter, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
    selector: 'app-organisations-modal',
    templateUrl: './search-filter-modal.component.html',
    styleUrls: ['./search-filter-modal.component.css']
})
export class SearchFilterModalComponent {
    errorMsg: string;
    public event: EventEmitter<any> = new EventEmitter();

    constructor(public dialogRef: MatDialogRef<SearchFilterModalComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any) {
    }


    close() {
        this.dialogRef.close();
    }

    checkValue(option) {
        const index = this.data.items.findIndex(opt => option.target.id == opt.name);
        if (index > -1) {
            this.data.items[index].selected = option.target.checked;
        }
    }

    applySelection() {
        this.event.emit(this.data.items);
    }
}
