import * as fromAuthentication from './authentication/authentication.reducer';
import * as fromUsers from './users/users.reducer';
import * as fromComponentUnits from './components/components.reducer';
import { User } from '../models/user';
import { ComponentUnit } from '../models/component';

// global state for the application
export interface State {
    authentication: fromAuthentication.State;
    users: fromUsers.State;
    componentUnits: fromComponentUnits.State;
}

// global reducer for the application
export const reducers = {
    authentication: fromAuthentication.reducer,
    users: fromUsers.reducer,
    componentUnits: fromComponentUnits.reducer
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

export function isFetchingUsers(state: State): boolean {
    return state.users.fetching;
}

export function isFetchingUsersError(state: State): boolean {
    return state.users.error;
}

export function selectUsers(state: State): User[] {
    return state.users.users;
}

export function selectComponents(state: State): ComponentUnit[] {
    return state.componentUnits.componentUnits;
}
