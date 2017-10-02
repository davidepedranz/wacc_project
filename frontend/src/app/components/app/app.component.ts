import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { State } from '../../store';
import * as AuthenticationActions from '../../authentication/store/authentication.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit {

  constructor(private store: Store<State>) { }

  ngOnInit() {
    // first thing when app loads -> load token into ngrx store
    this.store.dispatch(new AuthenticationActions.LoadToken());
  }
}
