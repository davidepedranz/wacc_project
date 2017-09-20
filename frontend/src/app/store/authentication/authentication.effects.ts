import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/take';
import { of } from 'rxjs/observable/of';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import * as AuthenticationActions from './authentication.actions';
import { AuthenticationService } from '../../services/authentication/authentication.service';

@Injectable()
export class AuthenticationEffects {

    @Effect()
    login$ = this.actions$.ofType(AuthenticationActions.LOGIN)
        .map((action: AuthenticationActions.Login) => action.payload)
        .exhaustMap(credentials =>
            this.authenticationService
                .login(credentials)
                .map(token => new AuthenticationActions.LoginSuccess(token))
                .catch(error => of(new AuthenticationActions.LoginFailure()))
        );

    @Effect({ dispatch: false })
    loginSuccess$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_SUCCESS)
        .do(() => this.router.navigate(['/']));

    @Effect({ dispatch: false })
    loginRedirect$ = this.actions$
        .ofType(AuthenticationActions.LOGIN_REDIRECT, AuthenticationActions.LOGOUT)
        .do(_ => {
            this.router.navigate(['/login']);
        });

    constructor(
        private actions$: Actions,
        private authenticationService: AuthenticationService,
        private router: Router        
    ) { }
}