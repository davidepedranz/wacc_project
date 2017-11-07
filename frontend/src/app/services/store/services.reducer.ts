import * as ServicesActions from './services.actions';
import { Component } from '../models/component';

export interface State {
    fetching: boolean;
    error: boolean;
    components: Component[];
}

const initialState: State = {
    fetching: false,
    error: false,
    components: []
};

export function reducer(state = initialState, action: ServicesActions.All): State {
    switch (action.type) {

        case ServicesActions.FETCH_COMPONENTS: {
            return {
                ...state,
                fetching: true,
                error: false
            };
        }

        case ServicesActions.FETCH_COMPONENTS_SUCCESS: {
            return {
                ...state,
                fetching: false,
                error: false,
                components: action.payload
            };
        }

        case ServicesActions.FETCH_COMPONENTS_FAILURE: {
            return {
                ...state,
                fetching: false,
                error: true
            };
        }

        default: {
            return state;
        }
    }
}

export const getComponents = (state: State): Component[] => state.components;
