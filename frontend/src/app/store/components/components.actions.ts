import { Action } from '@ngrx/store';
import { ComponentUnit } from '../../models/component';

export const FETCH_COMPONENTS = '[Components] Fetch';
export const FETCH_COMPONENTS_SUCCESS = '[Components] Fetch Success';
export const FETCH_COMPONENTS_FAILURE = '[Components] Fetch Failure';

export class FetchComponents implements Action {
    readonly type = FETCH_COMPONENTS;
}

export class FetchComponentsSuccess implements Action {
    readonly type = FETCH_COMPONENTS_SUCCESS;

    constructor(public payload: ComponentUnit[]) { }
}

export class FetchComponentsFailure implements Action {
    readonly type = FETCH_COMPONENTS_FAILURE;
}

export type All =
    | FetchComponents
    | FetchComponentsSuccess
    | FetchComponentsFailure;
