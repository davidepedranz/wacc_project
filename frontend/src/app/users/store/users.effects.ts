import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Effect, Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/exhaustMap';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import * as UsersActions from './users.actions';
import { UsersService } from '../services/users.service';

@Injectable()
export class UsersEffects {

    @Effect()
    fetchUsers$ = this.actions$
        .ofType(UsersActions.FETCH_USERS)
        .exhaustMap(_ => this.usersService
            .fetchUsers()
            .map(users => new UsersActions.FetchUsersSuccess(users))
            .catch(error => Observable.of(new UsersActions.FetchUsersFailure()))
        );

    @Effect()
    createUser$ = this.actions$
        .ofType(UsersActions.CREATE_USER)
        .map((action: UsersActions.CreateUser) => action.payload)
        .exhaustMap(user => this.usersService
            .createUser(user)
            .map(_ => new UsersActions.CreateUserSuccess())
            .catch(error => Observable.of(new UsersActions.CreateUserFailure(error)))
        );

    @Effect({ dispatch: false })
    createUserSuccessRedirect$ = this.actions$
        .ofType(UsersActions.CREATE_USER_SUCCESS)
        .do(path => this.router.navigate(['/users']));

    @Effect({ dispatch: false })
    deleteUser$ = this.actions$
        .ofType(UsersActions.DELETE_USER)
        .map((action: UsersActions.DeleteUser) => action.payload)
        .exhaustMap(username => this.usersService.deleteUser(username));

    @Effect({ dispatch: false })
    addPermission$ = this.actions$
        .ofType(UsersActions.ADD_PERMISSION)
        .map((action: UsersActions.AddPermission) => action.payload)
        .exhaustMap(o => this.usersService.addPermission(o.username, o.permission));

    @Effect({ dispatch: false })
    removePermission$ = this.actions$
        .ofType(UsersActions.REMOVE_PERMISSION)
        .map((action: UsersActions.RemovePermission) => action.payload)
        .exhaustMap(o => this.usersService.removePermission(o.username, o.permission));

    constructor(
        private actions$: Actions,
        private usersService: UsersService,
        private router: Router
    ) { }
}
