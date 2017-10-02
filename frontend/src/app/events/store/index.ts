import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromRoot from '../../store';
import * as fromEvents from './events.reducer';

export interface State extends fromRoot.State {
    authentication: EventsState;
}

export interface EventsState {
    status: fromEvents.State;
}

export const reducers: ActionReducerMap<any> = {
    status: fromEvents.reducer
};

export const selectEventsState = createFeatureSelector<EventsState>('events');
export const selectEventsStatusState = createSelector(selectEventsState, (state: EventsState) => state.status);

export const getEvents = createSelector(selectEventsStatusState, fromEvents.getEvents);
