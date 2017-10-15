import { Injectable } from '@angular/core';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import 'rxjs/add/operator/map';

import * as EventsActions from './events.actions';
import { EventsService } from '../services/events.service';

@Injectable()
export class EventsEffects {

    @Effect()
    newEvent$ = this.eventsService.events$
        .map(event => new EventsActions.NewEvent(event));

    constructor(
        private eventsService: EventsService
    ) { }
}
