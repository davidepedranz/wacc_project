import * as Immutable from 'immutable';

import { User } from '../../models/user';
import * as UserActions from './users.actions';

export interface State {
    fetching: boolean;
    error: boolean;
    users: Immutable.Map<string, User>;
}

const initialState: State = {
    fetching: false,
    error: false,
    users: Immutable.Map<string, User>()
};

export function reducer(state = initialState, action: UserActions.All): State {
    switch (action.type) {

        case UserActions.FETCH_USERS: {
            return {
                ...state,
                fetching: true,
                error: false
            };
        }

        case UserActions.FETCH_USERS_SUCCESS: {
            return {
                ...state,
                fetching: false,
                error: false,
                users: action.payload.reduce(
                    (accumulator, current) => accumulator.set(current.username, current), Immutable.Map<string, User>()
                )
            };
        }

        case UserActions.FETCH_USERS_FAILURE: {
            return {
                ...state,
                fetching: false,
                error: true
            };
        }

        // TODO: on success only!
        case UserActions.DELETE_USER: {
            return {
                ...state,
                users: state.users.delete(action.payload)
            };
        }

        default: {
            return state;
        }
    }
}
