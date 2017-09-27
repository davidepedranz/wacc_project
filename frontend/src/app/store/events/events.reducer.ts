import { Event } from '../../models/event';
import * as EventsActions from './events.actions';

export interface State {
    events: Event[];
    connected: boolean;
    connecting: boolean;
    error: boolean;
}

export const initialState: State = {
    events: [],
    connected: false,
    connecting: false,
    error: false
};

export function reducer(state = initialState, action: EventsActions.All): State {
    switch (action.type) {

        case EventsActions.STREAM_EVENTS: {
            return {
                ...state,
                connecting: true
            };
        }

        case EventsActions.STREAM_EVENTS_CONNECTED: {
            return {
                ...state,
                connected: true,
                connecting: false,
                error: false
            };
        }

        case EventsActions.STREAM_EVENTS_FAILURE: {
            return {
                ...state,
                connected: false,
                connecting: false,
                error: false
            };
        }

        case EventsActions.NEW_EVENT: {
            const newEvent = action as EventsActions.NewEvent;
            return {
                ...state,
                events: [...state.events, newEvent.payload]
            };
        }

        default: {
            return state;
        }
    }
}
