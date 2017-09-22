import * as fromAuthentication from './authentication/authentication.reducer';

// global state for the application
export interface State {
    authentication: fromAuthentication.State;
}

// global reducer for the application
export const reducers = {
    authentication: fromAuthentication.reducer
};

export function selectRedirectPathAfterLogin(state: State): string {
    return state.authentication.redirectPageAfterLogin;
}

export function isLoggedIn(state: State): boolean {
    return selectToken(state) != null;
}

export function isLoginPending(state: State): boolean {
    return state.authentication.loginPending;
}

export function isLoginError(state: State): boolean {
    return state.authentication.loginError;
}

export function selectToken(state: State): string {
    return state.authentication.token;
}
