import { Action } from '@ngrx/store';

import { Event } from '../../models/event';

export const STREAM_EVENTS = '[Events] Stream';
export const STREAM_EVENTS_CONNECTED = '[Events] Stream Connected';
export const STREAM_EVENTS_FAILURE = '[Events] Stream Failure';

export const NEW_EVENT = '[Events] New';

export class StreamEvents implements Action {
    readonly type = STREAM_EVENTS;
}

export class StreamEventsConnected implements Action {
    readonly type = STREAM_EVENTS_CONNECTED;
}

export class StreamEventsFailure implements Action {
    readonly type = STREAM_EVENTS_FAILURE;
}

export class NewEvent implements Action {
    readonly type = NEW_EVENT;

    constructor(public payload: Event) { }
}

export type All = Action;
