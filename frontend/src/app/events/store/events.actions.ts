import { Action } from '@ngrx/store';

import { Event } from '../models/event';

export const NEW_EVENTS = '[Events] New';

export class NewEvents implements Action {
    readonly type = NEW_EVENTS;

    constructor(public payload: Event[]) { }
}

export type All =
    | NewEvents;
