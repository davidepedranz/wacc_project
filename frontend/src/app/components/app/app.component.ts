import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { State } from '../../store/reducers';
import * as Authentication from '../../store/authentication/authentication.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private store: Store<State>) { }

  public ngOnInit(): void {
    // first thing when app loads -> load token into ngrx store
    this.store.dispatch(new Authentication.LoadToken());
  }
}
