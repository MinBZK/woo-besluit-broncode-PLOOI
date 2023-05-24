import {Component, OnInit} from '@angular/core';
import {SearchService} from "../../services/search.service";
import {FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {SearchFilterModalComponent} from "../search-modal/search-filter-modal.component";
import {Option} from "../../models/option";
import {TokenStorageService} from "../../../core/services/token-storage.service";

@Component({
    selector: 'dcn-search-filter',
    templateUrl: './search-filter.component.html',
    styleUrls: ['./search-filter.component.css']
})
export class SearchFilterComponent implements OnInit {

    bronList: [];
    organisaties: Option[] = [];
    documentTypes: any[] = [];
    searchForm: FormGroup;
    validationError: any;
    selectedOrgs: Option[] = [];
    selectedTypes: Option[] = [];
    errorMessage: string;

    constructor(private searchService: SearchService,
                public fb: FormBuilder, private router: Router,
                private route: ActivatedRoute, public dialog: MatDialog,
                private tokenService: TokenStorageService) {
        this.searchForm = this.fb.group({
            plooiId: [null, Validators.required],
            searchType: ['plooi']
        });
    }

    ngOnInit(): void {
        this.restoreFilter();
    }

    onChange() {
        this.searchForm.get('searchType').valueChanges.subscribe(val => {
            this.resetForm(val);
        });
    }

    clearSearch() {
        this.resetForm(this.searchForm.get('searchType').value);
        this.selectedOrgs = [];
        this.selectedTypes = [];
        this.organisaties.forEach(e => e.selected = false)
        this.tokenService.removeSearchFilter();
        this.errorMessage = null;
    }

    private createIndexFilter() {
        this.getSolrSources();
        this.searchForm = this.fb.group({
            title: new FormControl(''),
            creator: [''],
            type: [''],
            fromDate: [''],
            toDate: [''],
            searchType: [this.searchForm.get('searchType').value]
        });
        this.searchForm.setValidators(this.comparisonValidator())
        this.searchForm.updateValueAndValidity();
        this.validationError = {isError: false, errorMessage: ''};
    }

    public comparisonValidator(): ValidatorFn {
        return (group: FormGroup): ValidationErrors => {
            const fromDate = group.controls['fromDate'].value;
            const toDate = group.controls['toDate'].value;

            if (!fromDate || !toDate) {
                return
            } else if (new Date(toDate) < new Date(fromDate)) {
                this.validationError = {isError: true, errorMessage: 'Einddatum mag niet voor startdatum liggen'};
                group.controls['fromDate'].setErrors({'incorrect': true});
                return
            } else {
                group.controls['fromDate'].setErrors(null);
            }
        };
    }

    private createPlooiIdFilter() {
        this.searchForm = this.fb.group({
            plooiId: [null, Validators.required],
            searchType: ['plooi']
        });
    }

    private createExternalIdFilter() {
        this.getSourcesList();
        this.searchForm = this.fb.group({
            externalId: new FormControl(),
            bron: ['', [Validators.required]],
            searchType: ['externalId', Validators.required]
        });
    }

    private createGeavanceerdFilter() {
        this.searchForm = this.fb.group({
            query: [null, Validators.required],
            searchType: ['geavanceerd']
        });
    }


    private resetForm(val) {
        if (val == "externalId") {
            this.createExternalIdFilter();
        } else if (val == "plooi") {
            this.createPlooiIdFilter();
        } else if (val == "index") {
            this.createIndexFilter();
        } else if (val == "geavanceerd") {
            this.createGeavanceerdFilter();
        }
    }

    onSubmit() {
        let searchType = this.searchForm.controls['searchType'].value;
        if (searchType == 'plooi') {
            this.processDcnId();
        } else if (searchType == 'externalId') {
            this.processExternalId();
        } else {
            this.router.navigate(['./search-documents'], {relativeTo: this.route});
        }
    }


    openOrganizationsModal() {
        const dialogRef = this.dialog.open(SearchFilterModalComponent, {
                data: {
                    items: this.organisaties,
                    title: 'Selecteer organisatie(s)',
                    error: this.errorMessage
                }
            }
        );
        dialogRef.componentInstance.event.subscribe(selectedOrgs => {
            this.selectedOrgs = this.organisaties.filter(e => e.selected);
            this.searchForm.get('creator').setValue(this.organisaties.filter(e => e.selected));
            dialogRef.close();
        })
    }

    removeSelectedOrganization(option) {
        const index = this.organisaties.findIndex(opt => option.target.id == opt.name);
        if (index > -1) {
            this.organisaties[index].selected = !this.organisaties[index].selected;
        }
        this.selectedOrgs = this.organisaties.filter(e => e.selected);
        this.searchForm.get('creator').setValue(this.selectedOrgs);
    }

    removeTypeSelection(option) {
        const index = this.documentTypes.findIndex(opt => option.target.id == opt.name);
        if (index > -1) {
            this.documentTypes[index].selected = !this.documentTypes[index].selected;
        }
        this.selectedTypes = this.documentTypes.filter(e => e.selected);
        this.searchForm.get('type').setValue(this.selectedTypes);

    }

    openDocumentTypesModal() {
        const dialogRef = this.dialog.open(SearchFilterModalComponent, {
                data: {
                    items: this.documentTypes,
                    title: 'Selecteer documentsoort(en)',
                    error: this.errorMessage
                }
            }
        );
        dialogRef.componentInstance.event.subscribe(documentTypes => {
            this.selectedTypes = this.documentTypes.filter(e => e.selected);
            this.searchForm.get('type').setValue(this.documentTypes.filter(e => e.selected));

            dialogRef.close();
        })

    }

    extractPlooiId() {
        const db = /"|“|'/g;
        const url = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
        let plooiId = this.searchForm.get('plooiId').value.trim();
        if (!!plooiId) {
            if (db.test(plooiId)) {
                plooiId = plooiId.replace(db, '');
            } else if (url.test(plooiId)) {
                plooiId = plooiId.match(/.*\/([^/]+)\/[^/]+/)[1];
            }
        }
        this.searchForm.controls['plooiId'].setValue(plooiId);
    }

    restoreFilter() {
        let filter = this.tokenService.getSearchFilter();
        if (filter) {
            this.resetForm(filter['searchType'])
            Object.keys(filter)
                .forEach(k => {
                    if (filter[k] !== null && filter[k].constructor == Array) {
                        this.searchForm.controls[k].setValue(filter[k]);
                        if (k == 'creator') {
                            this.selectedOrgs = filter[k];
                        }
                        if (k == 'type') {
                            this.selectedTypes = filter[k];
                        }
                    } else {
                        this.searchForm.controls[k].setValue(filter[k])
                    }
                })
            this.searchForm.markAsDirty();
        }
    }

    ngOnDestroy() {
        this.tokenService.saveFilter(JSON.stringify(this.searchForm.value));
    }

    getCurrentDate() {
        var d = new Date(),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2)
            month = '0' + month;
        if (day.length < 2)
            day = '0' + day;
        return [year, month, day].join('-');
    }

    applyValidation() {
        this.validationError = {};
        this.searchForm.updateValueAndValidity();
    }

    private processDcnId(): void{
        let id:string;
        this.searchService.getDcnIdForInternalId(this.searchForm.controls['plooiId'].value)
            .subscribe(result => {
                id = result.dcnId;
            }, error => {
                this.errorMessage = error;
            }, () => {
                this.goToDocumentDetails(id);
            })
    }

    private processExternalId(): void {
        let externalId =  this.searchForm.get('externalId')?.value;
       /* externalId = externalId.replaceAll('/', '%2F');
        console.log(externalId);
        console.log(encodeURI(externalId));
        console.log("\\+".match(encodeURI(externalId)));*/

        let id:string;
        this.searchService.getDcnIdForExternalIdAndSource(externalId, this.searchForm.get('bron').value)
            .subscribe(result => {
                id = result.dcnId;
            }, error => {
                this.errorMessage = error
            }, () => {
                this.goToDocumentDetails(id);
            })
    }

    goToDocumentDetails(id:string) {
        let queryParams = {'searchType': 'plooi', 'plooiId': id}
        this.router.navigate(['document-details'], {queryParams: queryParams, relativeTo: this.route});
    }

    getSourcesList(){
        this.searchService.getSources().subscribe(result => {
            this.bronList = result.map(e => e.sourceName);
        });
    }

    getSolrSources(){
        this.searchService.getSolrSources().subscribe(result => {
            this.organisaties = result[result.findIndex(e => e.name == 'creator')].options;
            this.documentTypes = result[result.findIndex(e => e.name == 'type')].options;
        }, error => this.errorMessage = error);
    }

}
