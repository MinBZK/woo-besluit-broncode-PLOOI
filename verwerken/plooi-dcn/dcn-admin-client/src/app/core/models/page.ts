import  { Sort } from './sort';
import  { Pageable } from './pageable';

export class Page<T> {
    content: Array<T>;
    pageable: Pageable;
    last: boolean;
    totalPages: number;
    totalElements: number;
    first: boolean;
    sort: Sort;
    numberOfElements: number;
    size: number;
    number: number;

    public constructor() {
        this.pageable = new Pageable();
        this.size = 100;
        this.pageable.pageNumber = 0;
    }
}
