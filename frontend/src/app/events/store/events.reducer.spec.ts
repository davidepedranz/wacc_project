import { reducer, initialState, State } from './events.reducer';
import * as EventsActions from './events.actions';

describe('Events Store', () => {
    describe('reducer', () => {
        it('should have a meaningful initial state', () => {
            const expectedState: State = {
                events: [],
                connecting: false,
                connected: false,
                error: false
            };
            expect(initialState).toEqual(expectedState);
        });
        it('should return the initial state if invoked with "undefined"', () => {
            const actualState = reducer(undefined, {} as EventsActions.All);
            expect(actualState).toEqual(initialState);
        });
        it('should add a new event to the store', () => {
            const event1 = {
                id: '1',
                timestamp: new Date(123),
                action: 'a',
                component: 'b',
                description: 'c'
            };
            const event2 = {
                id: '2',
                timestamp: new Date(123),
                action: 'a',
                component: 'b',
                description: 'c'
            };
            const state: State = {
                ...initialState,
                events: [event1]
            };
            const expectedState: State = {
                ...initialState,
                events: [
                    event1,
                    event2
                ]
            };
            const actualState = reducer(state, new EventsActions.NewEvent(event2));
            expect(actualState).toEqual(expectedState);
        });
    });
});
