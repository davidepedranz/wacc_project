import * as fromUsers from '../users/store/users.reducer';
import * as fromComponentUnits from '../store/components/components.reducer';
import * as fromEvents from '../store/events/events.reducer';

import { ComponentUnit } from '../models/component';

// global state for the application
export interface State {
    // authentication: fromAuthentication.State;
    // users: fromUsers.State;
    componentUnits: fromComponentUnits.State;
    events: fromEvents.State;
}

// global reducer for the application
export const reducers = {
    // authentication: fromAuthentication.reducer,
    // users: fromUsers.reducer,
    componentUnits: fromComponentUnits.reducer,
    events: fromEvents.reducer
};

// export function selectRedirectPathAfterLogin(state: State): string {
//     return state.authentication.redirectPageAfterLogin;
// }

// export function isLoggedIn(state: State): boolean {
//     return selectToken(state) != null;
// }

// export function isLoginPending(state: State): boolean {
//     return state.authentication.loginPending;
// }

// export function isLoginError(state: State): boolean {
//     return state.authentication.loginError;
// }

// export function selectToken(state: State): string {
//     return state.authentication.token;
// }

// export function isFetchingUsers(state: State): boolean {
//     return state.users.fetching;
// }

// export function isFetchingUsersError(state: State): boolean {
//     return state.users.error;
// }

// export function selectUsers(state: State): fromUsers.User[] {
//     return state.users.users.valueSeq().toArray();
// }

// export function selectUserByUsername(username: string): (State) => fromUsers.User {
//     return function (state: State): fromUsers.User {
//         return state.users.users.get(username);
//     };
// }

export function selectComponents(state: State): ComponentUnit[] {
    return state.componentUnits.componentUnits;
}
