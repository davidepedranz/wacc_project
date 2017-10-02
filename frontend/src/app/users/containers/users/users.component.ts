import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../store/user.model';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  users$: Observable<User[]>;

  constructor(private store: Store<fromUsers.State>) {
    this.fetching$ = store.select(fromUsers.isFetchingUsers);
    this.error$ = store.select(fromUsers.isFetchingUsersError);
    this.users$ = store.select(fromUsers.getUsers);
  }

  ngOnInit() {
    this.store.dispatch(new UsersActions.FetchUsers());
  }

  deleteUser(username: string) {
    this.store.dispatch(new UsersActions.DeleteUser(username));
  }
}
