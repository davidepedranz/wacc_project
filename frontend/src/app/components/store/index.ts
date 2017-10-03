import { ActionReducerMap, createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromComponents from './components.reducer';

export interface State {
    components: ComponentsState;
}

export interface ComponentsState {
    status: fromComponents.State;
}

export const reducers: ActionReducerMap<any> = {
    status: fromComponents.reducer
};

export const selectComponentsState = createFeatureSelector<ComponentsState>('components');
export const selectComponentsStatusState = createSelector(
    selectComponentsState,
    (state: ComponentsState) => state.status
);

export const getComponents = createSelector(selectComponentsStatusState, fromComponents.getComponents);
