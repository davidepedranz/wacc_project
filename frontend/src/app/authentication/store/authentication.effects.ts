import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/take';
import 'rxjs/add/operator/withLatestFrom';

import * as fromAuthentication from './index';
import * as AuthenticationActions from './authentication.actions';
import { AuthenticationService } from '../services/authentication.service';
import { TokenService } from '../services/token.service';

@Injectable()
export class AuthenticationEffects {

    @Effect({ dispatch: false })
    loadToken$ = this.actions$
        .ofType(AuthenticationActions.LOAD_TOKEN)
        .map(action => this.tokenService.readToken())
        .do(token => {
            if (token != null) {
                this.store$.dispatch(new AuthenticationActions.SaveToken(token));
            }
        });

    @Effect({ dispatch: false })
    saveToken$ = this.actions$
        .ofType(AuthenticationActions.SAVE_TOKEN)
        .map((action: AuthenticationActions.SaveToken) => action.payload)
        .do(token => this.tokenService.saveToken(token));

    @Effect()
    me$ = this.actions$
        .ofType(AuthenticationActions.SAVE_TOKEN)
        .exhaustMap(token =>
            this.authenticationService
                .me()
                .map(user => new AuthenticationActions.Me(user))
                .catch(_ => Observable.of(new AuthenticationActions.Logout()))
        );

    @Effect({ dispatch: false })
    removeToken$ = this.actions$
        .ofType(AuthenticationActions.LOGOUT)
        .do(action => this.tokenService.removeToken());

    @Effect()
    login$ = this.actions$
        .ofType(AuthenticationActions.LOGIN)
        .map((action: AuthenticationActions.Login) => action.payload)
        .exhaustMap(credentials =>
            this.authenticationService
                .login(credentials)
                .map(token => new AuthenticationActions.LoginSuccess(token))
                .catch(error => Observable.of(new AuthenticationActions.LoginFailure()))
        );

    @Effect()
    loginSuccessStoreToken$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_SUCCESS)
        .map((action: AuthenticationActions.LoginSuccess) => action.payload)
        .map(result => new AuthenticationActions.SaveToken(result.token));

    @Effect({ dispatch: false })
    loginSuccessRedirect$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_SUCCESS)
        .withLatestFrom(this.store$)
        .map(([action, state]) => fromAuthentication.getRedirectPathAfterLogin(state))
        .do(path => this.router.navigate([path]));

    @Effect({ dispatch: false })
    redirectToLogin$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_REDIRECT, AuthenticationActions.LOGOUT)
        .do(_ => this.router.navigate(['/login']));

    constructor(
        private store$: Store<fromAuthentication.State>,
        private actions$: Actions,
        private authenticationService: AuthenticationService,
        private tokenService: TokenService,
        private router: Router
    ) { }
}
