import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import 'rxjs/add/operator/take';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as Authentication from '../store/authentication/authentication.actions';
import * as fromRoot from '../store';

@Injectable()
export class AuthenticationGuard implements CanActivate {

  constructor(private store: Store<fromRoot.State>) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.checkLogin(state.url);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.canActivate(route, state);
  }

  checkLogin(url: string): Observable<boolean> {
    return this.store
      .select(fromRoot.isLoggedIn)
      .map(authenticated => {
        if (authenticated) {
          return true;
        } else {
          this.store.dispatch(new Authentication.LoginRedirect(url));
          return false;
        }
      })
      .take(1);
  }
}
