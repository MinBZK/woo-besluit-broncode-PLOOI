import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {ProcessingError} from "../../process/models/processing-error";

const DEFAULT_NUMBER_ITEM = 20;
@Injectable({
  providedIn: 'root'
})
export class StateService {

    private _processError = new BehaviorSubject<ProcessingError>(null);
    public readonly processError = this._processError.asObservable();

    private _httpError = new BehaviorSubject<string>("");
    public readonly httpError =  this._httpError.asObservable();

    private _itemsPerPage = new BehaviorSubject<number>(DEFAULT_NUMBER_ITEM);
    public readonly itemsPerPage = this._itemsPerPage.asObservable();


    constructor() {}

    updateProcessError(item : any) {
        this._processError.next(item);
    }

    updateHttpError(error: string) {
        this._httpError.next(error);
    }

    updateItemsPerPage(number: number) {
        this._itemsPerPage.next(number);
    }

}
