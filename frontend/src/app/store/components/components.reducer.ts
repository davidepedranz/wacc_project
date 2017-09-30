import { ComponentUnit } from '../../models/component';
import * as ComponentUnitActions from './components.actions';

export interface State {
    fetching: boolean;
    error: boolean;
    componentUnits: ComponentUnit[];
}

const initialState: State = {
    fetching: false,
    error: false,
    componentUnits: []
};

export function reducer(state = initialState, action: ComponentUnitActions.All): State {
    switch (action.type) {

        case ComponentUnitActions.FETCH_COMPONENTS: {
            return {
                ...state,
                fetching: true,
                error: false
            };
        }

        case ComponentUnitActions.FETCH_COMPONENTS_SUCCESS: {
            return {
                ...state,
                fetching: false,
                error: false,
              componentUnits: action.payload
            };
        }

        case ComponentUnitActions.FETCH_COMPONENTS_FAILURE: {
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
