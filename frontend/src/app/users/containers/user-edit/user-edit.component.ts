import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';

import { User } from '../../store/user.model';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserEditComponent implements OnInit {
  user$: Observable<User>;

  constructor(
    private route: ActivatedRoute,
    private store: Store<fromUsers.UsersState>
  ) {
    this.user$ = this.route.paramMap
      .map((params: ParamMap) => params.get('username'))
      .switchMap((username: string) => store.select(fromUsers.getUserByUsername(username)));
  }

  ngOnInit() {
    this.store.dispatch(new UsersActions.FetchUsers());
  }
}
