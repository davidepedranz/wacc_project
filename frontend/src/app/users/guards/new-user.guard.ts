import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import * as fromAuthentication from '../../authentication/store';
import { PERMISSION_USER_WRITE } from '../services/users.service';

@Injectable()
export class NewUserGuard implements CanActivate {

  constructor(private store: Store<fromAuthentication.State>, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.checkRoute(state.url);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.canActivate(route, state);
  }

  checkRoute(url: string): Observable<boolean> {
    return this.store
      .select(fromAuthentication.getCurrentUser)
      .map(user => {
        const allowed = user.permissions.indexOf(PERMISSION_USER_WRITE) !== -1;
        if (!allowed) {
          console.warn('Current user has not the permission to create new users.', user);
          this.router.navigate(['users']);
        }
        return allowed;
      });
  }
}
