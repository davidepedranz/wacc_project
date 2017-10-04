import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/exhaustMap';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as fromComponents from './index';
import * as ComponentsActions from './components.actions';
import { ComponentsService } from '../services/components.service';

@Injectable()
export class ComponentsEffects {

    @Effect()
    fetch$ = this.actions$
        .ofType(ComponentsActions.FETCH_COMPONENTS)
        .exhaustMap(_ => this.componentsService
            .fetchComponents()
            .map(components => new ComponentsActions.FetchComponentsSuccess(components))
            .catch(error => Observable.of(new ComponentsActions.FetchComponentsFailure()))
        );

    constructor(
        private store$: Store<fromComponents.State>,
        private actions$: Actions,
        private componentsService: ComponentsService
    ) { }
}
