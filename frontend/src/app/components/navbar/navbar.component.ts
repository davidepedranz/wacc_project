import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as AuthenticationActions from '../../authentication/store/authentication.actions';
import * as fromAuthentication from '../../authentication/store';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit {
  show: Observable<boolean>;

  constructor(private store: Store<fromAuthentication.State>) {
    this.show = store.select(fromAuthentication.isLoggedIn);
  }

  ngOnInit() {
  }

  logout() {
    this.store.dispatch(new AuthenticationActions.Logout());
  }
}
