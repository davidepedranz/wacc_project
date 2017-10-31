import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, ObservableInput } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/retryWhen';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/take';
import 'rxjs/add/operator/concat';

@Injectable()
export class CallsInterceptor implements HttpInterceptor {

    constructor() { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // tslint:disable-next-line:no-console
        console.info(`HTTP call: ${request.method}, ${request.url}`);
        return next.handle(request)
            .retryWhen((errors) => {
                return errors
                    .mergeMap((error: HttpErrorResponse) => (error.status >= 500) ? Observable.of(error) : Observable.throw(error))
                    .delay(1000)
                    .take(20)
                    .concat(Observable.throw(new Error(`HTTP call '${request.method} ${request.url}' failed after retries.`)));
            });
    }
}
