import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as UsersActions from './users.actions';
import { UsersService } from '../services/users.service';

@Injectable()
export class UsersEffects {

    // TODO: fetch only once!
    @Effect()
    fetch$ = this.actions$
        .ofType(UsersActions.FETCH_USERS)
        .exhaustMap(_ => this.usersService
            .fetchUsers()
            .map(users => new UsersActions.FetchUsersSuccess(users))
            .catch(error => Observable.of(new UsersActions.FetchUsersFailure()))
        );

    constructor(
        private actions$: Actions,
        private usersService: UsersService
    ) { }
}
