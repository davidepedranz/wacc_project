import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';

import * as Authentication from '../../store/authentication/authentication.actions';
import * as fromRoot from '../../store/reducers';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  show: Observable<boolean>;

  constructor(private store: Store<fromRoot.State>) {
    this.show = store.select(fromRoot.isLoggedIn);
  }

  ngOnInit() {
  }

  logout() {
    this.store.dispatch(new Authentication.Logout());
  }
}