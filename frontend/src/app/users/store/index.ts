import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromUsers from './users.reducer';

export interface State {
    users: UsersState;
}

export interface UsersState {
    status: fromUsers.State;
}

export const reducers: ActionReducerMap<any> = {
    status: fromUsers.reducer
};

export const selectUsersState = createFeatureSelector<UsersState>('users');
export const selectUsersStatusState = createSelector(selectUsersState, (state: UsersState) => state.status);

export const getUserByUsername = (username: string) => createSelector(selectUsersStatusState, fromUsers.getUserByUsername(username));
export const getUsers = createSelector(selectUsersStatusState, fromUsers.getUsers);
export const isFetchingUsers = createSelector(selectUsersStatusState, fromUsers.isFetchingUsers);
export const isFetchingUsersError = createSelector(selectUsersStatusState, fromUsers.isFetchingUsersError);
