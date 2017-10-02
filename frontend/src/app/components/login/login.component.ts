import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import { Credentials } from '../../models/credentials';
import * as fromRoot from '../../store';
import * as Authentication from '../../store/authentication/authentication.actions';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {
  pending$: Observable<boolean>;
  error$: Observable<boolean>;

  constructor(private store: Store<fromRoot.State>) {
    this.pending$ = store.select(fromRoot.isLoginPending);
    this.error$ = store.select(fromRoot.isLoginError);
  }

  ngOnInit() { }

  onSubmit($event: Credentials) {
    this.store.dispatch(new Authentication.Login($event));
  }
}
