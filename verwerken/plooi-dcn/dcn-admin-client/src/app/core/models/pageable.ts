import  { Sort } from './sort';

export class Pageable {
    sort: Sort;
    pageSize: number;
    pageNumber: number;
    offset:number;
    unpaged:boolean;
    paged:boolean;

    static readonly DEFAULT_PAGE_SIZE = 10;
    static readonly FIRST_PAGE_NUMBER = 0;

    public constructor() {

    }
}
