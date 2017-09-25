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
import * as Authentication from './authentication.actions';
import { AuthenticationService } from '../../services/authentication.service';
import { TokenService } from '../../services/token.service';

@Injectable()
export class AuthenticationEffects {

    @Effect({ dispatch: false })
    loadToken$ = this.actions$
        .ofType(Authentication.LOAD_TOKEN)
        .map(action => this.tokenService.readToken())
        .do(token => {
            if (token != null) {
                this.store$.dispatch(new Authentication.LoginSuccess(token));
            }
        });

    @Effect({ dispatch: false })
    removeToken$ = this.actions$
        .ofType(Authentication.LOGOUT)
        .do(action => this.tokenService.removeToken());

    @Effect()
    login$ = this.actions$
        .ofType(Authentication.LOGIN)
        .map((action: Authentication.Login) => action.payload)
        .exhaustMap(credentials =>
            this.authenticationService
                .login(credentials)
                .map(token => new Authentication.LoginSuccess(token))
                .catch(error => Observable.of(new Authentication.LoginFailure()))
        );

    @Effect({ dispatch: false })
    loginSuccessSaveToken$ = this.actions$
        .ofType(Authentication.LOGIN_SUCCESS)
        .map(action => action as Authentication.LoginSuccess)
        .do(action => this.tokenService.saveToken(action.payload));

    @Effect({ dispatch: false })
    loginSuccessRedirect$ = this.actions$
        .ofType(Authentication.LOGIN_SUCCESS)
        .withLatestFrom(this.store$)
        .map(([action, state]) => fromRoot.selectRedirectPathAfterLogin(state))
        .do(path => this.router.navigate([path]));

    @Effect({ dispatch: false })
    redirectToLogin$ = this.actions$
        .ofType(Authentication.LOGIN_REDIRECT, Authentication.LOGOUT)
        .do(_ => this.router.navigate(['/login']));

    constructor(
        private store$: Store<fromRoot.State>,
        private actions$: Actions,
        private authenticationService: AuthenticationService,
        private tokenService: TokenService,
        private router: Router
    ) { }
}
