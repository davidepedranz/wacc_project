import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as AuthenticationActions from '../../../authentication/store/authentication.actions';
import * as fromAuthentication from '../../../authentication/store';
import { User } from '../../../users/models/user.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  show$: Observable<boolean>;
  username$: Observable<string | null>;

  constructor(private store: Store<fromAuthentication.State>) {
    this.show$ = store.select(fromAuthentication.isLoggedIn);
    this.username$ = store.select(fromAuthentication.getCurrentUser).map(user => user && user.username || null);
  }

  logout() {
    this.store.dispatch(new AuthenticationActions.Logout());
  }
}
