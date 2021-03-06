import { Component, ChangeDetectionStrategy, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import { TimerObservable } from 'rxjs/observable/TimerObservable';
import { Subscription } from 'rxjs/Subscription';

import { User } from '../../models/user.model';
import { PERMISSION_USER_WRITE } from '../../services/users.service';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';
import * as fromAuthentication from '../../../authentication/store';
import { ChangePermission } from '../../components/users-table/users-table.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent implements OnInit, OnDestroy {

  private subscription: Subscription;
  currentUser$: Observable<User | null>;
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  users$: Observable<User[]>;
  canAddNewUsers$: Observable<boolean>;

  constructor(private authenticationStore: Store<fromAuthentication.State>, private usersStore: Store<fromUsers.State>) {
    this.currentUser$ = authenticationStore.select(fromAuthentication.getCurrentUser);
    this.fetching$ = usersStore.select(fromUsers.isFetchingUsers);
    this.error$ = usersStore.select(fromUsers.isFetchingUsersError);
    this.users$ = usersStore.select(fromUsers.getUsers);
    this.canAddNewUsers$ = this.currentUser$.map(user => user && user.permissions.indexOf(PERMISSION_USER_WRITE) !== -1);
  }

  ngOnInit() {
    const timer = TimerObservable.create(0, 60 * 1000);
    this.subscription = timer.subscribe(t => this.loadUsers());
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  changePermission($event: ChangePermission) {
    if ($event.status) {
      this.usersStore.dispatch(new UsersActions.AddPermission({
        username: $event.username,
        permission: $event.permission
      }));
    } else {
      this.usersStore.dispatch(new UsersActions.RemovePermission({
        username: $event.username,
        permission: $event.permission
      }));
    }
  }

  deleteUser(username: string) {
    this.usersStore.dispatch(new UsersActions.DeleteUser(username));
  }

  private loadUsers() {
    this.usersStore.dispatch(new UsersActions.FetchUsers());
  }
}
