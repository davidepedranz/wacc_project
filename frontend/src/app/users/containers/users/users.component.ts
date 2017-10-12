import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../models/user.model';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';
import * as fromAuthentication from '../../../authentication/store';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent implements OnInit {
  currentUser$: Observable<string>;
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  users$: Observable<User[]>;

  constructor(private authenticationStore: Store<fromAuthentication.State>, private usersStore: Store<fromUsers.State>) {
    this.currentUser$ = authenticationStore.select(fromAuthentication.getUsername);
    this.fetching$ = usersStore.select(fromUsers.isFetchingUsers);
    this.error$ = usersStore.select(fromUsers.isFetchingUsersError);
    this.users$ = usersStore.select(fromUsers.getUsers);
  }

  ngOnInit() {
    this.usersStore.dispatch(new UsersActions.FetchUsers());
  }

  deleteUser(username: string) {
    this.usersStore.dispatch(new UsersActions.DeleteUser(username));
  }
}
