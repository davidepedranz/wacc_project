import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import * as fromAuthentication from '../store';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    private token: string;

    constructor(private store: Store<fromAuthentication.State>) {
        store.select(fromAuthentication.getToken)
            .subscribe(token => this.token = token);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const newRequest = this.token ? request.clone({
            setHeaders: {
                Authorization: `Bearer ${this.token}`
            }
        }) : request;
        return next.handle(newRequest);
    }
}
