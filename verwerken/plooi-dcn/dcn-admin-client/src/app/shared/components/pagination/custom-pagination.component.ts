import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Page } from "../../../core/models/page";
import { range } from "rxjs";
import { toArray } from "rxjs/operators";

@Component({
    selector: 'dcn-custom-pagination',
    templateUrl: './custom-pagination.component.html',
    styleUrls: ['./custom-pagination.component.css']
})
export class CustomPaginationComponent implements OnInit, OnChanges {
    readonly items = [20,50, 100];
    readonly  range  = 2;
    readonly minimumNumberOfPages = 4;
    @Input() page: Page<any>;
    @Output() nextPageEvent = new EventEmitter<number>();
    @Output() previousPageEvent = new EventEmitter();
    @Output() pageSizeEvent: EventEmitter<number> = new EventEmitter<number>();
    @Output() itemsPerPageEvent = new EventEmitter<number>();
    currentPage: number;
    totalPages : number = 0;
    pageNumbers: Array<number> = []
    showDots : boolean;
    longNumbers : boolean;

    constructor() { }

    ngOnInit() {
        this.getRangeOfPages()
    }

    clickPage(item: number): void {
        this.nextPageEvent.emit(item -1 );
    }


    nextPage(): void {
        this.clickPage(this.currentPage + 1);
    }

    previousPage(): void {
        this.clickPage(this.currentPage - 1);
    }

    ngOnChanges(): void {
        this.getRangeOfPages();
    }

    getRangeOfPages() {
        this.totalPages = this.page.totalPages;
        this.pageNumbers = [];
        this.currentPage = this.page.pageable.pageNumber + 1;
        this.showDots = (this.totalPages - this.currentPage) > this.range;
        this.longNumbers = this.totalPages > this.minimumNumberOfPages;
        if(!this.longNumbers) {
            range(1,this.totalPages).pipe(toArray()).subscribe(result => this.pageNumbers = result);
        } else {
            if(this.page.first) {
                this.pageNumbers.push(this.currentPage, this.currentPage + 1);
            } else if(this.page.last) {
                this.pageNumbers.push(this.currentPage -2, this.currentPage - 1, this.currentPage);
            } else {
                this.pageNumbers.push(this.currentPage - 1, this.currentPage, this.currentPage + 1);
            }
        }
    }

    updateItemsPerPage(number) {
        this.itemsPerPageEvent.emit(number);
    }

}