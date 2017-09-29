import { Injectable } from '@angular/core';

import 'rxjs/add/operator/map';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as fromRoot from '../reducers';
import * as EventsActions from './events.actions';
import { EventsService } from '../../services/events.service';

@Injectable()
export class EventsEffects {

    @Effect()
    newEvent$ = this.eventsService.events$
        .map(event => new EventsActions.NewEvent(event));

    constructor(
        private store$: Store<fromRoot.State>,
        private actions$: Actions,
        private eventsService: EventsService
    ) { }
}
