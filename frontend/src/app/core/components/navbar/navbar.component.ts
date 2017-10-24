import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as AuthenticationActions from '../../../authentication/store/authentication.actions';
import * as fromAuthentication from '../../../authentication/store';
import { User } from '../../../users/models/user.model';
import { PERMISSION_SERVICES, PERMISSION_EVENTS, PERMISSION_USER_READ } from '../../../users/services/users.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  show$: Observable<boolean>;
  username$: Observable<string | null>;

  showServicesButton$: Observable<boolean>;
  showEventsButton$: Observable<boolean>;
  showUsersButton$: Observable<boolean>;

  constructor(private store: Store<fromAuthentication.State>) {
    this.show$ = store.select(fromAuthentication.isLoggedIn);
    this.username$ = store.select(fromAuthentication.getCurrentUser).map(user => user && user.username || null);

    const user$ = store.select(fromAuthentication.getCurrentUser);
    this.showServicesButton$ = user$.map(NavbarComponent.hasPrivilege(PERMISSION_SERVICES));
    this.showEventsButton$ = user$.map(NavbarComponent.hasPrivilege(PERMISSION_EVENTS));
    this.showUsersButton$ = user$.map(NavbarComponent.hasPrivilege(PERMISSION_USER_READ));
  }

  static hasPrivilege(permission: string): (User) => boolean {
    return (user: User) => user && user.permissions && user.permissions.indexOf(permission) !== -1;
  }

  logout() {
    this.store.dispatch(new AuthenticationActions.Logout());
  }
}
