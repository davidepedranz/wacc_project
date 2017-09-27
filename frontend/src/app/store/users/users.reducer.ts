import { User } from '../../models/user';
import * as UserActions from './users.actions';

export interface State {
    fetching: boolean;
    error: boolean;
    users: User[];
}

const initialState: State = {
    fetching: false,
    error: false,
    users: []
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
                users: action.payload
            };
        }

        case UserActions.FETCH_USERS_FAILURE: {
            return {
                ...state,
                fetching: false,
                error: true
            };
        }

        default: {
            return state;
        }
    }
}
