import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';

import { Event } from '../models/event';
import * as EventsActions from './events.actions';

export type State = EntityState<Event>;

export const adapter: EntityAdapter<Event> = createEntityAdapter<Event>({
    selectId: (event: Event) => event.time,
    sortComparer: (a: Event, b: Event) => b.time - a.time
});

export const initialState: State = adapter.getInitialState();

export function reducer(state = initialState, action: EventsActions.All): EntityState<Event> {
    switch (action.type) {

        case EventsActions.NEW_EVENTS: {
            return adapter.addMany(action.payload, state);
        }

        default: {
            return state;
        }
    }
}
