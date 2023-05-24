import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoadingService} from '../services/loading.service';
import {finalize} from 'rxjs/operators';

@Injectable()
export class HttpCallInterceptor implements HttpInterceptor {

    constructor(private loader: LoadingService) {}

    intercept(
        request: HttpRequest<unknown>,
        next: HttpHandler
    ): Observable<HttpEvent<unknown>> {
        this.loader.show();
        return next.handle(request).pipe(
            finalize(() => {
                this.loader.hide();
            })
        );
    }
}
