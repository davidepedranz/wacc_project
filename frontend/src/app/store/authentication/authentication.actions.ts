import { Action } from '@ngrx/store';
import { Credentials } from '../../models/credentials';

// login
export const LOGIN = '[Authentication] Login';
export const LOGIN_SUCCESS = '[Authentication] Login Success';
export const LOGIN_FAILURE = '[Authentication] Login Failure';
export const LOGIN_REDIRECT = '[Authentication] Login Redirect';
export const LOGOUT = '[Authentication] Logout';

// action dispatched to request a login (eg. from the login component)
export class Login implements Action {
    readonly type = LOGIN;

    constructor(public payload: Credentials) { }
}

// action dispatched as the result of a successful login, contains the token in the payload
export class LoginSuccess implements Action {
    readonly type = LOGIN_SUCCESS;

    constructor(public payload: string) { }
}

// action dispatched as the result of a failed login
export class LoginFailure implements Action {
    readonly type = LOGIN_FAILURE;
}

// action dispatched when a guard prevents to route to a certain page (login required)
export class LoginRedirect implements Action {
    readonly type = LOGIN_REDIRECT;
}

export class Logout implements Action {
    readonly type = LOGOUT;
}

export type All =
    | Login
    | LoginSuccess
    | LoginFailure
    | LoginRedirect
    | Logout;
