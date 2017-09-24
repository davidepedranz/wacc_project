import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/take';
import 'rxjs/add/operator/withLatestFrom';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as fromRoot from '../reducers';
import * as AuthenticationActions from './authentication.actions';
import { AuthenticationService } from '../../services/authentication.service';

@Injectable()
export class AuthenticationEffects {

    @Effect()
    login$ = this.actions$.ofType(AuthenticationActions.LOGIN)
        .map((action: AuthenticationActions.Login) => action.payload)
        .exhaustMap(credentials =>
            this.authenticationService
                .login(credentials)
                .map(token => new AuthenticationActions.LoginSuccess(token))
                .catch(error => Observable.of(new AuthenticationActions.LoginFailure()))
        );

    @Effect({ dispatch: false })
    loginSuccess$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_SUCCESS)
        .withLatestFrom(this.store$)
        .map(([action, state]) => fromRoot.selectRedirectPathAfterLogin(state))
        .do(path => this.router.navigate([path]));

    @Effect({ dispatch: false })
    redirectToLogin$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_REDIRECT, AuthenticationActions.LOGOUT)
        .do(_ => this.router.navigate(['/login']));

    constructor(
        private store$: Store<fromRoot.State>,
        private actions$: Actions,
        private authenticationService: AuthenticationService,
        private router: Router
    ) { }
}
