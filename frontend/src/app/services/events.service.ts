import { Injectable } from '@angular/core';

import 'rxjs/add/observable/interval';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/scan';
import 'rxjs/add/operator/share';
import { Observable } from 'rxjs/Observable';

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
            .map(EventsService.randomEvent)
            .share();
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
