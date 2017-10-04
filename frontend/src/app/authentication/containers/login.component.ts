import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as fromAuthentication from '../store';
import * as AuthenticationActions from '../store/authentication.actions';
import { Credentials } from '../models/credentials';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {
  pending$: Observable<boolean>;
  error$: Observable<boolean>;

  constructor(private store: Store<fromAuthentication.State>) {
    this.pending$ = store.select(fromAuthentication.isLoginPending);
    this.error$ = store.select(fromAuthentication.isLoginError);
  }

  ngOnInit() { }

  onSubmit($event: Credentials) {
    this.store.dispatch(new AuthenticationActions.Login($event));
  }
}
