import * as AuthenticationActions from './authentication.actions';
import { Credentials } from '../models/credentials';
import { User } from '../../users/models/user.model';

export interface State {
    redirectPageAfterLogin: string;
    loginPending: boolean;
    loginError: boolean;
    token: string | null;
    currentUser: User | null;
}

export const initialState: State = {
    redirectPageAfterLogin: '/',
    loginPending: false,
    loginError: false,
    token: null,
    currentUser: null
};

export function reducer(state = initialState, action: AuthenticationActions.All): State {
    switch (action.type) {

        case AuthenticationActions.SAVE_TOKEN: {
            return {
                ...state,
                token: action.payload
            };
        }

        case AuthenticationActions.ME: {
            return {
                ...state,
                currentUser: action.payload
            };
        }

        case AuthenticationActions.LOGIN: {
            return {
                ...state,
                loginPending: true,
                loginError: false
            };
        }

        case AuthenticationActions.LOGIN_SUCCESS: {
            return {
                ...state,
                loginPending: false,
                loginError: false,
                currentUser: action.payload.user
            };
        }

        case AuthenticationActions.LOGIN_FAILURE: {
            return {
                ...state,
                loginPending: false,
                loginError: true
            };
        }

        case AuthenticationActions.LOGIN_REDIRECT: {
            return {
                ...state,
                redirectPageAfterLogin: action.payload
            };
        }

        case AuthenticationActions.LOGOUT: {
            return initialState;
        }

        default: {
            return state;
        }
    }
}

// selectors
export const getToken = (state: State): string => state.token;
export const getCurrentUser = (state: State): User | null => state.currentUser;
export const isLoggedIn = (state: State): boolean => getToken(state) != null;
export const isLoginPending = (state: State): boolean => state.loginPending;
export const isLoginError = (state: State): boolean => state.loginError;
export const getRedirectPathAfterLogin = (state: State): string => state.redirectPageAfterLogin;
