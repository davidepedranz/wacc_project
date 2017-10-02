import { Action } from '@ngrx/store';

import { User } from './user.model';

export const FETCH_USERS = '[Users] Fetch';
export const FETCH_USERS_SUCCESS = '[Users] Fetch Success';
export const FETCH_USERS_FAILURE = '[Users] Fetch Failure';

export const DELETE_USER = '[Users] Delete User';

export class FetchUsers implements Action {
    readonly type = FETCH_USERS;
}

export class FetchUsersSuccess implements Action {
    readonly type = FETCH_USERS_SUCCESS;

    constructor(public payload: User[]) { }
}

export class FetchUsersFailure implements Action {
    readonly type = FETCH_USERS_FAILURE;
}

export class DeleteUser implements Action {
    readonly type = DELETE_USER;

    constructor(public payload: string) { }
}

export type All =
    | FetchUsers
    | FetchUsersSuccess
    | FetchUsersFailure
    | DeleteUser;
