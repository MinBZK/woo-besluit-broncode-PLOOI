export class DateRangeField {

    fieldName: string;
    fromDate: number;
    toDate: number;

    constructor(fieldName: string, fromDate: number, toDate: number) {
        this.fieldName = fieldName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}