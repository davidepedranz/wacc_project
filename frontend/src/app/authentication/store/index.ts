import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromAuthentication from './authentication.reducer';

export interface State {
    authentication: AuthenticationState;
}

export interface AuthenticationState {
    status: fromAuthentication.State;
}

export const reducers: ActionReducerMap<any> = {
    status: fromAuthentication.reducer
};

export const selectAuthenticationState = createFeatureSelector<AuthenticationState>('authentication');
export const selectAuthenticationStatusState = createSelector(
    selectAuthenticationState,
    (state: AuthenticationState) => state.status
);

export const getToken = createSelector(selectAuthenticationStatusState, fromAuthentication.getToken);
export const getUsername = createSelector(selectAuthenticationStatusState, fromAuthentication.getUsername);
export const isLoggedIn = createSelector(selectAuthenticationStatusState, fromAuthentication.isLoggedIn);
export const isLoginPending = createSelector(selectAuthenticationStatusState, fromAuthentication.isLoginPending);
export const isLoginError = createSelector(selectAuthenticationStatusState, fromAuthentication.isLoginError);
export const getRedirectPathAfterLogin = createSelector(selectAuthenticationStatusState, fromAuthentication.getRedirectPathAfterLogin);
