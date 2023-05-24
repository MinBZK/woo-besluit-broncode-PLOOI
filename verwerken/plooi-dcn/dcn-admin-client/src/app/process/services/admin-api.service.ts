import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Page } from '../../core/models/page';
import { DocumentStatistics } from '../models/documentStatistics';
import { catchError } from 'rxjs/operators';
import { ProcessingError } from '../models/processing-error';
import { MappingErrorSummaryResponse } from '../models/mapping-error-summary-response';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from "../../core/services/token-storage.service";
import { SolrSearchFilter } from "../models/solr-search-filter";
import {LastDocumentState} from "../models/last-document-state";
import {BulkActionRequest} from "../models/bulk-action-request";
import {DocumentState} from "../models/document-state";
import {PublishingStateSummary} from "../models/publishing-state-summary";
import {Proces} from "../models/proces";

@Injectable({
  providedIn: 'root'
})
export class AdminApiService {
    readonly api = environment.apiUrl + "/admin";
    readonly apiUrlReg = environment.apiUrlReg;
    readonly apiUrlRepo = environment.apiUrlRepo;

    constructor(private http: HttpClient,
                private tokenStorageService: TokenStorageService) { }
    errorMsg: string;

    public getDocumentStatistics(reverseSorted?: boolean):Observable<DocumentStatistics[]> {
        const params = new HttpParams().set('reverseSorted', String(reverseSorted))
        return this.http.get<DocumentStatistics[]>(`${this.apiUrlReg}/statistieken`, {params : params})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getErrors( page: Page<any> , sourceName?: string, exception?: string):Observable<Page<ProcessingError>> {
        let params = AdminApiService.getPageParameters(page);
        params =   sourceName ? params.set("sourceName", sourceName): params;
        params =   exception ? params.set("exception", exception): params;
        return this.http.get<Page<ProcessingError>>(`${this.apiUrlReg}/statistieken/excepties`, {params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getMappingErrors(sourceLabel: string , severity: string,  page: Page<any>):Observable<Page<MappingErrorSummaryResponse>> {
        let params = AdminApiService.getPageParameters(page).set("sourceLabel", sourceLabel).set("severity", severity);
        return this.http.get<Page<MappingErrorSummaryResponse>>(`${this.apiUrlReg}/statistieken/diagnoses`, {params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getMappingErrorDocuments(sourceName: string, label: string, target: string, severity: string, page: Page<any>):Observable<Page<LastDocumentState>> {
        let params = AdminApiService.getPageParameters(page)
            .set('sourceName', sourceName)
            .set('target', target)
            .set('sourceLabel', label)
            .set('severity' ,severity);
        return this.http.get<Page<LastDocumentState>>(`${this.apiUrlReg}/statistieken/diagnoses/${sourceName}`, {params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getProcessingErrorExceptions(bron?: string):Observable<any> {
        return this.http.get(`${this.apiUrlReg}/statistieken/excepties/${bron}`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getDocumentsEventState(dcnId: string ):Observable<DocumentState> {
        let params = new HttpParams().set('dcnId', dcnId );

        return this.http.get<DocumentState>(`${this.apiUrlReg}/verwerkingen/status`, {params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getDocumentEvents(dcnId: string , page: Page<any>):Observable<Page<DocumentEvent>> {
        let params = AdminApiService.getPageParameters(page).set('dcnId', dcnId );

        return this.http.get<Page<DocumentEvent>>(`${this.apiUrlReg}/verwerkingen`, {params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));

    }

    public getExecutionStatistics(page: Page<any>, onlyErroneous?: boolean):Observable<Page<Proces>> {
        let params = AdminApiService.getPageParameters(page).set("alleenFalend", String(onlyErroneous));
        return this.http.get<Page<Proces>>(`${this.apiUrlReg}/processen`, {params : params})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getExecutionSummary(id: string):Observable<Proces> {
        return this.http.get<Proces>(`${this.apiUrlReg}/processen/${id}`, {responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getExecutionDocumentEvents(id: string, page: Page<any>, severity?: string):Observable<Page<DocumentEvent>> {
        let params = AdminApiService.getPageParameters(page);
        params = severity? params.set('severity', severity): params;
        return this.http.get<Page<DocumentEvent>>(`${this.apiUrlReg}/processen/${id}/verwerkingen`, { params: params, responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getErrorDetails(id: string):Observable<any> {
        return  this.http.get<any>(`${this.apiUrlReg}/verwerkingen/${id}`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getMetaDetails(internalId: string):Observable<string> {
        const headers = new HttpHeaders().set('Content-Type', 'text/json; charset=utf-8');
        return this.http.get(`${this.api}/metadata/${internalId}`, { headers, responseType: 'text' })
            .pipe(catchError(error => this.catchApiErrors(error)));
   }

    public getMetaDetailsFile(internalId: string):any {
        return this.http.get(`${this.api}/metadata/${internalId}`, { responseType: 'blob' })
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public downloadDocument(internalId: string ):Observable<any> {
        return this.http.get(`${this.api}/document/${internalId}`, { responseType: 'blob' })
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public redoMappingError(sourceName: string, targetElementName:string, sourceLabel:string, severity: string):Observable<number> {
        const params = new HttpParams()
            .set('sourceName', sourceName)
            .set('targetElementName', targetElementName)
            .set('sourceLabel', sourceLabel)
            .set('severity', severity)

         return  this.http.post<number>(`${this.api}/redo-mapping-error-docs`, params)
             .pipe(catchError(error => this.catchApiErrors(error)));
    }


    public applyDocumentAction(action: string , internalId: string):Observable<any> {
        return  this.http.post<any>(`${this.api}/${action}/${internalId}`, null)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getSolrDocument(query: string): Observable<any> {
        return  this.http.get<any>(`${this.api}/solrDocument/${query}`, { responseType: 'json' })
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getSolrDocumentFilter(solrSearchFilter: SolrSearchFilter, page: Page<any> ): Observable<Page<any>> {
        let params = AdminApiService.getPageParameters(page);

        return  this.http.post<Page<any>>(`${this.api}/solrDocuments/`, solrSearchFilter, {params: params} )
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getFiles(dcnId: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrlRepo}/publicatie/${dcnId}/_manifest`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }
    public getPlooiFiles(dcnId: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrlRepo}/publicatie/${dcnId}`)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getFile(dcnId: string, label: string, versie:string):void {
        window.open(`${this.apiUrlRepo}/publicatie/${dcnId}/${versie}/${label}`, '_blank');
    }

    public getStandardFile( label: string):void {
        window.open(`${this.apiUrlRepo}${label}`, '_blank');
    }


    public applyDocumentActionBulk(request: BulkActionRequest):Observable<any> {
        return  this.http.post<any>(`${this.api}/bulkAction`, request)
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getExportFiles(dcnId: string): Observable<any>{
        let params =   new HttpParams().set("dcnId", dcnId);
        return  this.http.get(`${environment.apiUrlRepo}/archief/export`, { params: params, responseType:"blob" })
          .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getPortalUrl(): any{
        return this.http.get(`${this.api}/portalUrl`, {responseType: "text"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getDuplicationList(dcnId: string ): Observable<any[]> {
        return this.http.get<any>(`${this.api}/duplications/${dcnId}`, { responseType: "json"})
            .pipe(catchError(error => this.catchApiErrors(error)));
    }

    public getNotIndexedPublishStateList(page: Page<PublishingStateSummary> ): Observable<Page<PublishingStateSummary>> {
        let params = AdminApiService.getPageParameters(page);
        return this.http.get<Page<PublishingStateSummary>>(`${this.api}/getPublishingStates`, { params: params, responseType: "json"})
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
            case 400 : {
                return `${error.error}`;
            }
            case 401 : {
                this.tokenStorageService.refreshToken();
                break;
            }
            case 404: {
                return `Not Found: ${error.message}`;
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
