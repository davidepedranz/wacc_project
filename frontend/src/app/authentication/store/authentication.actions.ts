import { Action } from '@ngrx/store';

import { Credentials } from '../models/credentials';
import { Token } from '../models/token';
import { User } from '../../users/models/user.model';

export const SAVE_TOKEN = '[Authentication] Save Token';
export const LOAD_TOKEN = '[Authentication] Load Token';

export const ME = '[Authentication] Me';

export const LOGIN = '[Authentication] Login';
export const LOGIN_SUCCESS = '[Authentication] Login Success';
export const LOGIN_FAILURE = '[Authentication] Login Failure';
export const LOGIN_REDIRECT = '[Authentication] Login Redirect';
export const LOGOUT = '[Authentication] Logout';

// action dispatched to load the token from the local storage
export class LoadToken implements Action {
    readonly type = LOAD_TOKEN;
}

// action dispatched to save the token in the store
export class SaveToken implements Action {
    readonly type = SAVE_TOKEN;

    constructor(public payload: string) { }
}

// action dispatched when got the information about the current user
export class Me implements Action {
    readonly type = ME;

    constructor(public payload: User) { }
}

// action dispatched to request a login (eg. from the login component)
export class Login implements Action {
    readonly type = LOGIN;

    constructor(public payload: Credentials) { }
}

// action dispatched as the result of a successful login, contains the token in the payload
export class LoginSuccess implements Action {
    readonly type = LOGIN_SUCCESS;

    constructor(public payload: Token) { }
}

// action dispatched as the result of a failed login
export class LoginFailure implements Action {
    readonly type = LOGIN_FAILURE;
}

// action dispatched when a guard prevents to route to a certain page (login required)
export class LoginRedirect implements Action {
    readonly type = LOGIN_REDIRECT;

    constructor(public payload: string) { }
}

// action dispatched to logout
export class Logout implements Action {
    readonly type = LOGOUT;
}

export type All =
    | LoadToken
    | SaveToken
    | Me
    | Login
    | LoginSuccess
    | LoginFailure
    | LoginRedirect
    | Logout;
