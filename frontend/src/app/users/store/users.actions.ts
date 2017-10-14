import { Action } from '@ngrx/store';

import { User } from '../models/user.model';
import { UserWithPassword } from '../models/user-with-password.model';

export const FETCH_USERS = '[Users] Fetch';
export const FETCH_USERS_SUCCESS = '[Users] Fetch Success';
export const FETCH_USERS_FAILURE = '[Users] Fetch Failure';

export const CREATE_USER = '[Users] Create User';
export const CREATE_USER_SUCCESS = '[Users] Create User Success';
export const CREATE_USER_FAILURE = '[Users] Create User Failure';
export const DELETE_USER = '[Users] Delete User';
export const ADD_PERMISSION = '[Users] Add Permission';
export const REMOVE_PERMISSION = '[Users] Remove Permission';

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

export class CreateUser implements Action {
    readonly type = CREATE_USER;

    constructor(public payload: UserWithPassword) { }
}

export class CreateUserSuccess implements Action {
    readonly type = CREATE_USER_SUCCESS;
}

export class CreateUserFailure implements Action {
    readonly type = CREATE_USER_FAILURE;

    constructor(public payload: string) { }
}

export class DeleteUser implements Action {
    readonly type = DELETE_USER;

    constructor(public payload: string) { }
}

export class AddPermission implements Action {
    readonly type = ADD_PERMISSION;

    constructor(public payload: { username: string, permission: string }) { }
}

export class RemovePermission implements Action {
    readonly type = REMOVE_PERMISSION;

    constructor(public payload: { username: string, permission: string }) { }
}

export type All =
    | FetchUsers
    | FetchUsersSuccess
    | FetchUsersFailure
    | CreateUser
    | CreateUserSuccess
    | CreateUserFailure
    | DeleteUser
    | AddPermission
    | RemovePermission;
