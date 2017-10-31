import { Injectable, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/dom/webSocket';
import 'rxjs/add/observable/empty';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/bufferTime';
import 'rxjs/add/operator/retryWhen';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/takeUntil';
import 'rxjs/add/operator/share';


import * as fromAuthentication from '../../authentication/store';
import { Event } from '../models/event';

@Injectable()
export class EventsService {
    private token: string;
    events$: Observable<Event[]>;

    constructor(private store: Store<fromAuthentication.State>, @Inject(DOCUMENT) private document) {
        const protocol = document.location.protocol.replace('http', 'ws');
        this.events$ = store.select(fromAuthentication.getToken)
            .do(token => console.log('Token is ' + (token == null ? 'absent' : 'present')))
            .mergeMap(token => token == null ? Observable.empty() : Observable.of(token))
            .mergeMap(token => Observable.webSocket(`${protocol}//${document.location.host}/api/v1/events?token=${token}`))
            .map(msg => msg as Event)
            .bufferTime(250)
            .retryWhen(error => error.delay(1000));
    }
}
