import { Injectable, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/dom/webSocket';
import 'rxjs/add/operator/retryWhen';
import 'rxjs/add/operator/map';

import { Event } from '../models/event';

@Injectable()
export class EventsService {
    events$: Observable<Event>;

    constructor( @Inject(DOCUMENT) private document) {
        this.events$ = Observable.webSocket(`ws://${document.location.host}/api/v1/events?token=test`)
            .retryWhen(error => error.delay(5000))
            .map(msg => msg as Event);
    }
}
