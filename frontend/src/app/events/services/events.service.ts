import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import 'rxjs/add/observable/dom/webSocket';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/scan';
import 'rxjs/add/operator/share';
import 'rxjs/add/operator/retry';
import 'rxjs/add/operator/retryWhen';
import 'rxjs/add/operator/delay';

import { Event } from '../models/event';

@Injectable()
export class EventsService {

    // stream of events coming from somewhere
    // TODO: read them from a websocket
    // see https://github.com/lwojciechowski/mildchat-client/blob/master/src/app/chat.service.ts
    // see http://reactivex.io/rxjs/class/es6/Observable.js~Observable.html#static-method-webSocket
    events$: Observable<Event>;

    constructor() {
        // simulate websocket by generating random events
        this.events$ = Observable.interval(2500)
            .map(EventsService.randomEvent);
            // .share();

        const subject1 = Observable.webSocket('ws://localhost:9000/v1/events?token=test').share();
        const subject2 = subject1.retryWhen(error => error.delay(5000));
        subject1.subscribe(
            (msg) => console.log('sub1', msg),
            (err) => console.log('sub1', err),
            () => console.log('sub1', 'complete')
        );
        subject2.subscribe(
            (msg) => console.log('sub2', msg),
            (err) => console.log('sub2', err),
            () => console.log('sub2', 'complete')
        );
    }

    private static randomEvent(id: number): Event {
        return {
            id: id.toString(),
            timestamp: new Date(),
            component: EventsService.randomComponent(),
            action: EventsService.randomAction(),
            description: 'this is a fake event'
        };
    }

    private static randomElement(array: string[]): string {
        return array[Math.floor(Math.random() * array.length)];
    }

    private static randomComponent(): string {
        const components = ['mongo', 'cassandra', 'frontend', 'backend'];
        return EventsService.randomElement(components);
    }

    private static randomAction(): string {
        const actions = ['new instance', 'removed', 'scale up', 'scale down'];
        return EventsService.randomElement(actions);
    }
}
