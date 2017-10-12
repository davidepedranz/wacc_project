import * as Immutable from 'immutable';

import * as UserActions from './users.actions';
import { User } from '../models/user.model';

export interface AddUserState {
    running: boolean;
    error: string | null;
}

export interface State {
    fetching: boolean;
    error: boolean;
    users: Immutable.Map<string, User>;
    create: AddUserState;
}

const initialState: State = {
    fetching: false,
    error: false,
    users: Immutable.Map<string, User>(),
    create: {
        running: false,
        error: null
    }
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

        case UserActions.CREATE_USER: {
            return {
                ...state,
                create: {
                    running: true,
                    error: null
                }
            };
        }

        case UserActions.CREATE_USER_SUCCESS: {
            return {
                ...state,
                create: {
                    running: false,
                    error: null
                }
            };
        }

        case UserActions.CREATE_USER_FAILURE: {
            return {
                ...state,
                create: {
                    running: false,
                    error: action.payload
                }
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

// selectors
export const getUserByUsername = (username: string) => (state: State): User => state.users.get(username);
export const getUsers = (state: State): User[] => state.users.valueSeq().toArray();
export const isFetchingUsers = (state: State): boolean => state.fetching;
export const isFetchingUsersError = (state: State): boolean => state.error;
export const isCreatingUser = (state: State): boolean => state.create.running;
export const isCreatingUserError = (state: State): string | null => state.create.error;
