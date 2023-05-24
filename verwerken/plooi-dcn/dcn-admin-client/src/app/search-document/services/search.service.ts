import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Page} from '../../core/models/page';
import {catchError} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {TokenStorageService} from "../../core/services/token-storage.service";
import {DocumentStatistics} from "../../process/models/documentStatistics";

@Injectable({
    providedIn: 'root'
})
export class SearchService {
    readonly api = environment.apiUrl + "/admin";
    readonly apiUrlRepo = environment.apiUrlRepo;

    constructor(private http: HttpClient,
                private tokenStorageService: TokenStorageService) { }
    errorMsg: string;


    public findDocument(internalId: string ):Observable<any> {
        return this.http.get(`${this.api}/documentById/${internalId}`, { responseType: 'json' })
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getSources():Observable<any> {
        const params = new HttpParams().set('reverseSorted', String(false))
        return this.http.get<DocumentStatistics[]>(`${environment.apiUrlReg}/statistieken`, {params : params})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getSolrSources():Observable<any> {
        return this.http.get<any>(`${this.api}/solrSources`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getDcnIdForExternalIdAndSource( externalId: string, source : string): Observable<any> {
        return this.http.get<any>(`${this.apiUrlRepo}/aanleveren/${source}/${externalId}`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getDcnIdForInternalId(dcnId: string ): Observable<any> {
        return this.http.get<any>(`${this.apiUrlRepo}/publicatie/${dcnId}`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    private catchApiErrors(error) {
        if (error.error instanceof ErrorEvent) {
            this.errorMsg = `Error: ${error.error.message}`;
        } else {
            this.errorMsg = this.getServerErrorMessage(error);
        }
        return throwError(this.errorMsg);
    }
    private getServerErrorMessage(error: HttpErrorResponse): string {
        switch (error.status) {
            case 400: {
                return 'Illegal DCN identifier';
            }
            case 401 : {
                this.tokenStorageService.refreshToken();
                break;
            }
            case 404: {
                return `Het door u gezochte document is niet gevonden`;
            }
            case 403: {
                return `Access Denied: ${error.message}`;
            }
            case 500: {
                return `Internal Server Error: ${error.message}`;
            }
            case 504: {
                return `DCN backend is down: ${error.message}`;
            }

            default: {
                return `Unknown Server Error: ${error.message}`;
            }

        }
    }

    private static getPageParameters(page : Page<any>) : HttpParams {
        let pageNumber = page?.pageable?.pageNumber ? page?.pageable?.pageNumber : 0;

        return new HttpParams()
            .set('page', '' + pageNumber)
            .set('size', '' + page.size);
    }
}