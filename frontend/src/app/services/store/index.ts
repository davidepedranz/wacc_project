import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as ServicesState from './services.reducer';

export interface State {
    components: ComponentsState;
}

export interface ComponentsState {
    status: ServicesState.State;
}

export const reducers: ActionReducerMap<any> = {
    status: ServicesState.reducer
};

export const selectComponentsState = createFeatureSelector<ComponentsState>('services');
export const selectComponentsStatusState = createSelector(
    selectComponentsState,
    (state: ComponentsState) => state.status
);

export const getServices = createSelector(selectComponentsStatusState, ServicesState.getComponents);
