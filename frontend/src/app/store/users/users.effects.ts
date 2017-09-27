import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';

import * as fromRoot from '../reducers';
import * as Users from './users.actions';
import { UsersService } from '../../services/users.service';

@Injectable()
export class UsersEffects {

    // TODO: fetch only once!
    @Effect()
    fetch$ = this.actions$
        .ofType(Users.FETCH_USERS)
        .exhaustMap(_ => this.usersService
            .fetchUsers()
            .map(users => new Users.FetchUsersSuccess(users))
            .catch(error => Observable.of(new Users.FetchUsersFailure()))
        );

    constructor(
        private store$: Store<fromRoot.State>,
        private actions$: Actions,
        private usersService: UsersService
    ) { }
}
