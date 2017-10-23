import { Action } from '@ngrx/store';

import { Event } from '../models/event';

export const NEW_EVENT = '[Events] New';

export class NewEvent implements Action {
    readonly type = NEW_EVENT;

    constructor(public payload: Event) { }
}

export type All =
    | NewEvent;
