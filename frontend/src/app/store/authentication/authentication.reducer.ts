import { Credentials } from '../../models/credentials';
import * as AuthenticationActions from './authentication.actions';

export interface State {
    redirectPageAfterLogin: string;
    loginPending: boolean;
    loginError: boolean;
    token: string | null;
}

const initialState: State = {
    redirectPageAfterLogin: '/',
    loginPending: false,
    loginError: false,
    token: null
};

export function reducer(state = initialState, action: AuthenticationActions.All): State {
    switch (action.type) {

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
                token: action.payload
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
