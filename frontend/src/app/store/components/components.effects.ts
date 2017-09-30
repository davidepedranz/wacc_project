import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as fromRoot from '../reducers';
import * as ComponentUnits from './components.actions';
import { ComponentsService } from '../../services/components.service';

@Injectable()
export class ComponentsEffects {

    // TODO: fetch only once!
    @Effect()
    fetch$ = this.actions$
        .ofType(ComponentUnits.FETCH_COMPONENTS)
        .exhaustMap(_ => this.componentsService
            .fetchComponents()
            .map(componentUnits => new ComponentUnits.FetchComponentsSuccess(componentUnits))
            .catch(error => Observable.of(new ComponentUnits.FetchComponentsFailure()))
        );

    constructor(
        private store$: Store<fromRoot.State>,
        private actions$: Actions,
        private componentsService: ComponentsService
    ) { }
}
