import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/exhaustMap';

import * as fromServices from './index';
import * as ServicesActions from './services.actions';
import { ServicesService } from '../services/services.service';

@Injectable()
export class ServicesEffects {

    @Effect()
    fetch$ = this.actions$
        .ofType(ServicesActions.FETCH_COMPONENTS)
        .exhaustMap(_ => this.componentsService
            .fetchComponents()
            .map(components => new ServicesActions.FetchComponentsSuccess(components))
            .catch(error => Observable.of(new ServicesActions.FetchComponentsFailure()))
        );

    constructor(
        private store$: Store<fromServices.State>,
        private actions$: Actions,
        private componentsService: ServicesService
    ) { }
}
