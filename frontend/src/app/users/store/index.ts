import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromRoot from '../../store';
import * as fromUsers from './users.reducer';

export interface UsersState {
    users: fromUsers.State;
}

export interface State extends fromRoot.State {
    users: UsersState;
}

export const reducers: ActionReducerMap<any> = {
    users: fromUsers.reducer
};

export const selectUsersState = createFeatureSelector<UsersState>('users');
export const selectUsersUsersState = createSelector(selectUsersState, (state: UsersState) => state.users);

export const getUserByUsername = (username: string) => createSelector(selectUsersUsersState, fromUsers.getUserByUsername(username));
export const getUsers = createSelector(selectUsersUsersState, fromUsers.getUsers);
export const isFetchingUsers = createSelector(selectUsersUsersState, fromUsers.isFetchingUsers);
export const isFetchingUsersError = createSelector(selectUsersUsersState, fromUsers.isFetchingUsersError);
